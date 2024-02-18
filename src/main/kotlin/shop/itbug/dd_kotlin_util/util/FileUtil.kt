package shop.itbug.dd_kotlin_util.util

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.impl.file.PsiDirectoryFactory
import org.apache.commons.io.IOUtils


object FileUtil {


    /**
     * 加载模板文件
     */
    fun loadTemplateFile(filename: String): String {
        try {
            val resource = javaClass.getResource("/temp/$filename")
            resource?.let {
                return IOUtils.toString(it, "UTF-8")
            }
        } catch (e: Exception) {
            return ""
        }
        return ""
    }

    /**
     * 获取顶级包
     */
    private fun getPackageRoot(project: Project, psiDirectory: PsiDirectory): PsiDirectory {
        val manager = PsiDirectoryFactory.getInstance(project)
        var directory: PsiDirectory = psiDirectory
        var parent = directory.parent
        while (parent != null && manager.isPackage(parent)) {
            directory = parent
            parent = parent.parent
        }
        return directory
    }


    /**
     * 获取包名
     */
    private fun getPackageName(root: PsiDirectory,currDirectory: PsiDirectory): String {
        if (root.isEquivalentTo(currDirectory)) return ""
        val rootPath: String = root.virtualFile.path
        val current: String = currDirectory.virtualFile.path
        return current.substring(rootPath.length + 1)
            .replace("/", ".")
    }


    /**
     * 获取包名
     */
    fun getPackageName(project: Project,psiDirectory: PsiDirectory): String{
        val rootDirectory = getPackageRoot(project, psiDirectory)
        return getPackageName(rootDirectory,psiDirectory)
    }


}