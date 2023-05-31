package shop.itbug.dd_kotlin_util.util

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import com.intellij.psi.codeStyle.CodeStyleManager
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.idea.util.application.runWriteAction
import org.jetbrains.kotlin.psi.KtFile

/**
 * psi工具类
 */
object PsiUtil {

    /**
     * 获取选中的目录
     */
    fun findPsiDirectory(e: AnActionEvent): PsiDirectory? {
        e.project?.let {
            e.getData(CommonDataKeys.VIRTUAL_FILE)?.let { file ->
                return PsiManager.getInstance(it).findDirectory(file)
            }
        }
        return null
    }

    /**
     * 使用文本创建一个kotlin文件
     */
    fun createKtPsiFile(project: Project, fileName: String, text: String): KtFile {
        return PsiFileFactory.getInstance(project).createFileFromText(fileName, KotlinLanguage.INSTANCE, text) as KtFile
    }


    /**
     * 添加文件到目录
     */
    fun addPsiFileToDirectory(psiFile: PsiFile,psiDirectory: PsiDirectory) {
        runWriteAction {
            psiDirectory.add(psiFile)
        }
    }

    /**
     * 格式化psiFile文件
     */
    fun reformatPsiFile(project: Project ,psiFile: PsiFile) {
        WriteCommandAction.runWriteCommandAction(project) {
            CodeStyleManager.getInstance(project).reformat(psiFile)
        }
    }


}