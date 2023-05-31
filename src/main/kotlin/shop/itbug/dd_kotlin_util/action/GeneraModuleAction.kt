package shop.itbug.dd_kotlin_util.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import shop.itbug.dd_kotlin_util.dialog.showGenerateModuleConfigurationDialog

/**
 * 生成jpa模块的操作
 */
class GeneraModuleAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        e.project!!.showGenerateModuleConfigurationDialog(e)
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.project != null
        super.update(e)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}
