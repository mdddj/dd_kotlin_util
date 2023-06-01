package shop.itbug.dd_kotlin_util.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import shop.itbug.dd_kotlin_util.util.KtUtil

/**
 * 生成 antd 请求
 */
class GenerateAntdRequestFunction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val funCode = KtUtil.getSelectKtNameFunction(e)!!
        val api = KtUtil.getApiWithKtFun(funCode)
        println(api)
    }

    override fun update(e: AnActionEvent) {
        val selectKtNameFunction = KtUtil.getSelectKtNameFunction(e)
        e.presentation.isEnabled = selectKtNameFunction != null && KtUtil.findMappingAnnotationEntries(
            selectKtNameFunction
        ) != null
        super.update(e)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

}
