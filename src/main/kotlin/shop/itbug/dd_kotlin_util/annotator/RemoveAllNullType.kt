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
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry
import shop.itbug.dd_kotlin_util.help.helper
import shop.itbug.dd_kotlin_util.icons.DDIcon
import javax.swing.Icon


///将接口定义的函数列表里面可空的类型全部设置为不可空类型
class RemoveAllNullType : Annotator, DumbAware, Iconable {

    override fun getIcon(flags: Int): Icon {
        return DDIcon.FixIcon
    }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is KtClass) {
            println("name psi : ${element.nameIdentifier?.text}")
            val namePsi = element.nameIdentifier ?: return
            holder.newAnnotation(HighlightSeverity.INFORMATION,"Set all nullable types to non-nullable")
                .range(namePsi)
                .withFix(Fix(element))
                .create()
        }
    }

    private class Fix(val ktClass: KtClass): BaseIntentionAction(),Iconable  {
        override fun getFamilyName(): String {
            return "Set all nullable types to non-nullable"
        }

        override fun getText(): String {
            return familyName
        }

        override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
            return true
        }

        override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
            val functions = ktClass.body?.functions ?: emptyList()
            functions.forEach {
                it.valueParameterList?.parameters?.forEach { prop ->
                    prop.typeReference?.helper?.nullableRemove()
                }
                it.typeReference?.helper?.nullableRemove()
            }

            ktClass.superTypeListEntries.forEach { type: KtSuperTypeListEntry ->
                type.typeReference?.helper?.nullableRemove()
            }

        }

        override fun getIcon(flags: Int): Icon {
            return DDIcon.FixIcon
        }


    }
}