package shop.itbug.dd_kotlin_util.action

import com.intellij.lang.javascript.JavascriptLanguage
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import shop.itbug.dd_kotlin_util.dialog.showCode
import shop.itbug.dd_kotlin_util.util.KtUtil
import shop.itbug.dd_kotlin_util.util.getFunctionName
import shop.itbug.dd_kotlin_util.util.getRequestMappingType

/**
 * 生成 antd 请求
 */
class GenerateAntdRequestFunction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val funCode = KtUtil.getSelectKtNameFunction(e)!!
        val funName  = funCode.getFunctionName()
        val methodType = funCode.getRequestMappingType()
        val api = KtUtil.getApiWithKtFun(funCode)
        println(api)
        val sb = StringBuilder()
        sb.append("export async function $funName(){\n")
        sb.appendLine("\treturn request('$api',{\n" +
                "\t\tmethod: '$methodType'" +
                "\n\t})")
        sb.append("}")
        e.project?.showCode(sb.toString(),JavascriptLanguage.INSTANCE)

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
