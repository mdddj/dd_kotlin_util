package shop.itbug.dd_kotlin_util.annotator

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Iconable
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import shop.itbug.dd_kotlin_util.icons.DDIcon
import shop.itbug.dd_kotlin_util.util.MyKtPsiFactory
import shop.itbug.dd_kotlin_util.util.findFirstChild
import shop.itbug.dd_kotlin_util.util.findFirstParentChild
import shop.itbug.dd_kotlin_util.util.getUseAge
import javax.swing.Icon


///查找属性
private fun PsiElement.filerQueryWrapperRefElement(): KtNameReferenceExpression? {
    val find: PsiElement? = PsiTreeUtil.findFirstParent(
        this
    ) { t -> t is KtNameReferenceExpression && t.getReferencedName() == "QueryWrapper" && t.parent is KtCallExpression }
    return find as? KtNameReferenceExpression
}

///修复mybatis query mapper
class FixMyBatisQueryMapperFunction : Annotator, DumbAware {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val ref = element.filerQueryWrapperRefElement()
        if (ref != null) {
            holder.newSilentAnnotation(HighlightSeverity.WARNING).range(ref).newFix(Fix(ref)).registerFix().create()
        }
    }

    private inner class Fix(val ref: KtNameReferenceExpression) : PsiElementBaseIntentionAction(), Iconable, DumbAware {
        private val importText = "com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper"
        private val factory = MyKtPsiFactory(ref.project)
        private val psiFile = ref.originalElement.containingFile
        override fun getFamilyName(): String {
            return "QueryWrapper to KtQueryWrapper"
        }

        override fun getText(): String {
            return familyName
        }

        override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
            return getKtType() != null && ref.parent is KtCallExpression
        }

        override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
            setImportText()
            replaceUseAge()
            val newCall = createCallExpression()
            val oldCall = ref.parent as KtCallExpression
            oldCall.replace(newCall)

        }

        override fun getIcon(flags: Int): Icon = DDIcon.FixIcon


        ///替换KtQueryMapper
        private fun createCallExpression(): KtCallExpression {
            val type = getKtType()!!
            return factory.createCallExpression("KtQueryWrapper(${type.text}())")
        }


        ///替换引用
        private fun replaceUseAge() {
            ref.findFirstParentChild<KtProperty>()?.let {
                val useAges = psiFile.getUseAge(it)
                useAges.forEach { u ->
                    val namedPsi = u.element
                    if (namedPsi is KtNameReferenceExpression && namedPsi.parent is KtDotQualifiedExpression) {
                        val dotQ = namedPsi.parent as KtDotQualifiedExpression
                        val last = dotQ.lastChild
                        if (last.text == "lambda()") {
                            val namePsiNew = factory.createNameReferenceExpression(namedPsi.text)
                            val replacedElement = dotQ.replace(namePsiNew)
                            if (replacedElement is KtNameReferenceExpression) {
                                processLastFunction(replacedElement, getKtType()!!.text)
                            }
                        } else {
                            processLastFunction(namedPsi, getKtType()!!.text)
                        }
                    }
                }
            }
        }


        private fun processLastFunction(newElement: KtNameReferenceExpression, objectName: String) {
            when (val dot = newElement.parent) {
                is KtDotQualifiedExpression -> {
                    when (val call = dot.lastChild) {
                        is KtCallExpression -> {


                            ///获取对象的字段文本
                            fun getTypeText(blockExpression: KtBlockExpression): String? {
                                blockExpression.findFirstChild<KtReturnExpression>()?.let { ret ->
                                    return ret.returnedExpression?.lastChild?.text
                                }
                                return (blockExpression.firstChild as? KtDotQualifiedExpression)?.lastChild?.text
                            }

                            ///处理一个参数的 : albumUserQueryWrapper.orderByDesc{
                            //            return@orderByDesc AlbumUser::updateTime
                            //        }
                            val la = call.lambdaArguments
                            val find: KtLambdaArgument? =
                                la.firstOrNull { it.getLambdaExpression()?.functionLiteral?.findFirstChild<KtBlockExpression>() != null }
                            if (find != null) {
                                println("找到了lbd表达式吗?:${find}")
                                val propType =
                                    getTypeText(find.getLambdaExpression()!!.functionLiteral.findFirstChild<KtBlockExpression>()!!)
                                val newProp = factory.createArgumentList("($objectName::$propType)")
                                find.replace(newProp)
                            }

                            /// 处理两个参数的
                            val args = call.valueArgumentList?.arguments ?: emptyList()
                            val firstBlock = args.firstOrNull { it.findFirstChild<KtBlockExpression>() != null }
                            if (firstBlock != null) {
                                val block = firstBlock.findFirstChild<KtBlockExpression>() ?: return
                                val propText = getTypeText(block)
                                if (propText != null) {
                                    val newArgList = factory.createArgList("$objectName::$propText")
                                    firstBlock.replace(newArgList)
                                }

                            }
                        }
                    }
                }
            }
        }


        ///导包..
        private fun setImportText() {
            val ktImportList = psiFile.findFirstChild<KtImportList>() ?: return
            val hasImport = ktImportList.anyDescendantOfType<KtImportDirective> { it.lastChild.text == importText }
            if (!hasImport) {
                val imp = factory.createImport(importText)
                ktImportList.add(imp)
            }
        }


        ///获取泛型
        private fun getKtType(): KtTypeReference? {
            if (ref.nextSibling is KtTypeArgumentList) {
                return ref.nextSibling.findFirstChild<KtTypeReference>()
            }
            return null
        }

    }

}