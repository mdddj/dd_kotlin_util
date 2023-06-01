package shop.itbug.dd_kotlin_util.action

import com.intellij.lang.javascript.JavascriptLanguage
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import shop.itbug.dd_kotlin_util.dialog.showCode
import shop.itbug.dd_kotlin_util.model.generateTypescriptInterface
import shop.itbug.dd_kotlin_util.util.KtUtil

class GenerateTypescriptInterface : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val ktClass = e.getKtClass()!!
        val types = KtUtil.getClassTypeList(ktClass)
        val className = ktClass.name ?: "--"
        val generateTypescriptInterface = types.generateTypescriptInterface(className)
        e.project?.showCode(generateTypescriptInterface, JavascriptLanguage.INSTANCE)
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.getKtClass()!=null
        super.update(e)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

}
