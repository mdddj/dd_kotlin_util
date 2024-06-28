package shop.itbug.dd_kotlin_util.annotator

import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Iconable
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.elementType
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.psiUtil.prevLeaf
import shop.itbug.dd_kotlin_util.help.helper
import shop.itbug.dd_kotlin_util.icons.DDIcon
import shop.itbug.dd_kotlin_util.util.MyKtPsiFactory
import shop.itbug.dd_kotlin_util.util.filterByType
import shop.itbug.dd_kotlin_util.util.findByTypeAndText
import shop.itbug.dd_kotlin_util.util.findLastLeafChild
import javax.swing.Icon

class FixBeanProperties : Annotator, DumbAware {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is KtProperty) return
        if (!element.helper.isNullableType()) return
        if (!element.helper.isAutowiredOrResource()) return
        holder.newAnnotation(HighlightSeverity.WARNING, "Change to lateinit var").range(element).withFix(MyFix(element))
            .create()
    }


    private class MyFix(val element: KtProperty) : BaseIntentionAction(), Iconable {
        override fun getFamilyName(): String {
            return "Change to lateinit var"
        }

        override fun getText(): String {
            return familyName
        }

        override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
            return true
        }

        override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
            val factory = KtPsiFactory(project)
            val myFactory = MyKtPsiFactory(project)
            //1. 去除问号
            element.typeReference?.let {
                val newEle = factory.createType(it.text.removeSuffix("?"))
                it.replace(newEle)
            }

            // 2. 删除null和null前面的=号
            val nullPsi = element.children.find { it.elementType == KtNodeTypes.NULL }
            nullPsi?.let {
                it.prevLeaf { p -> p.text == "=" }?.delete()
                it.delete()
            }


            // 3替换val
            element.filterByType<LeafPsiElement>().find { it.text == "val" }?.let {
                val lateinitPsi = myFactory.createLateinit()
                /// 添加lateinit
                val ws = factory.createWhiteSpace(" ")
                element.modifierList?.let { modis ->
                    modis.addAfter(ws, modis.findLastLeafChild())
                    modis.addAfter(lateinitPsi, modis.findLastLeafChild())
                }

                ///替换var
                val valPsi = element.findByTypeAndText<LeafPsiElement>("val")
                val newVar = factory.createVarKeyword()
                valPsi?.replace(newVar) // val 替换为 var

            }


            //======= 修复双!!号
            if (file != null) {
                val find: MutableCollection<PsiReference> =
                    ReferencesSearch.search(element, LocalSearchScope(file)).findAll()
                find.forEach { ele ->
                    when (val e = ele.element.nextSibling) {
                        is KtOperationReferenceExpression -> {
                            if (e.text == "!!") {
                                val newPsi = myFactory.createNameReferenceExpression(element.nameIdentifier?.text ?: "")
                                ele.element.parent.replace(newPsi)
                            }
                        }
                    }
                }
            }

        }

        override fun getIcon(flags: Int): Icon {
            return DDIcon.FixIcon
        }

    }

}