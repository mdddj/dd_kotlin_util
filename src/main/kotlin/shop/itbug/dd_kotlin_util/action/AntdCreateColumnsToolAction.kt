package shop.itbug.dd_kotlin_util.action

import cn.hutool.json.JSONUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtClass
import shop.itbug.dd_kotlin_util.util.KtUtil
import shop.itbug.dd_kotlin_util.util.MyUtil

data class ColumnTypeObject(val key: String,val dataIndex: String,val title: String)


///生成表格列

class AntdCreateColumnsToolAction : AnAction(){



    override fun actionPerformed(e: AnActionEvent) {
        val ktClass = e.getKtClass()!!
        val properties = ktClass.getProperties()
        val objects = mutableListOf<ColumnTypeObject>()
        properties.forEach {
            val name = KtUtil.getAnnotationPropertiesName(it, "Schema", "name")
            objects.add(ColumnTypeObject(it.name?:"",it.name?:"",name))
        }
        val string = JSONUtil.toJsonPrettyStr(objects)
        MyUtil.copyTextToClipboard(string)
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.getKtClass() != null
        super.update(e)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

}

/**
 * 获取 ktClass
 */
fun AnActionEvent.getKtClass(): KtClass? {
    val data = getData(CommonDataKeys.PSI_ELEMENT)
    return PsiTreeUtil.getParentOfType(data, KtClass::class.java,false)
}

