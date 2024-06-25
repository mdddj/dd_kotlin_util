package shop.itbug.dd_kotlin_util.intention

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Iconable
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtTypeReference
import shop.itbug.dd_kotlin_util.help.helper
import shop.itbug.dd_kotlin_util.icons.DDIcon
import javax.swing.Icon

class FixBeanProperties: PsiElementBaseIntentionAction(),IntentionAction,DumbAware,Iconable {

    override fun getIcon(flags: Int): Icon = DDIcon.FixIcon

    override fun getFamilyName(): String = "Change to latevar init"

    override fun getText(): String = getFamilyName()

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        println(element::class.java)
        return element is KtTypeReference && (element.parent is KtProperty && (element.parent as KtProperty).helper.isAutowiredOrResource() && (element.parent as KtProperty).helper.isNullableType())
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
    }
}