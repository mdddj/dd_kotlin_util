package shop.itbug.dd_kotlin_util.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import shop.itbug.dd_kotlin_util.dialog.MyTypeScriptCodeShow
import shop.itbug.dd_kotlin_util.util.getTypescriptInterface

class GenerateTypescriptInterface : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        e.getKtClass()?.let {
            it.getTypescriptInterface?.let { interfaceText -> MyTypeScriptCodeShow(e.project!!, interfaceText).show() }
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.getKtClass() != null && e.project != null
        super.update(e)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

}
