    package shop.itbug.dd_kotlin_util.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import shop.itbug.dd_kotlin_util.dialog.showCode
import shop.itbug.dd_kotlin_util.model.generateAntdFormString

    /**
 * kotlin类生成Antd表单
 */
class KtClassGenerateAntdFormAction: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val ktClass = e.getKtClass()!!
        val formString = ktClass.generateAntdFormString()
        e.project?.showCode(formString)
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.getKtClass()!=null
        super.update(e)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}