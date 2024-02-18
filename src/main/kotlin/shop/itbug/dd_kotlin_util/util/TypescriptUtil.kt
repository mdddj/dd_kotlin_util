package shop.itbug.dd_kotlin_util.util

import com.intellij.lang.javascript.TypeScriptJSXFileType
import com.intellij.lang.javascript.psi.ecma6.impl.TypeScriptInterfaceImpl
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType

object TypescriptUtil {

    /**
     * 生成ts psi element
     */
    fun generatePsiElementByText(project: Project, text: String): PsiElement {
        val factory = PsiFileFactory.getInstance(project)

        val file = factory.createFileFromText("file.ts", TypeScriptJSXFileType.INSTANCE, text)
        return file
    }

    /**
     * 生成一个接口模型
     */
    fun generateInterface(project: Project,text: String): TypeScriptInterfaceImpl? {
        val psi = generatePsiElementByText(project, text)
        return psi.findDescendantOfType<TypeScriptInterfaceImpl>()
    }
}