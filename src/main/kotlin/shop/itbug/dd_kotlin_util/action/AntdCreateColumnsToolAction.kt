package shop.itbug.dd_kotlin_util.action

import com.intellij.json.JsonLanguage
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.*
import shop.itbug.dd_kotlin_util.dialog.showCode
import shop.itbug.dd_kotlin_util.util.KtUtil
import shop.itbug.dd_kotlin_util.util.MyUtil

data class ColumnTypeObject(val key: String,val dataIndex: String,val title: String)


///生成表格列
class AntdCreateColumnsToolAction : AnAction(){

    override fun actionPerformed(e: AnActionEvent) {
        val objects = KtUtil.getClassTypeList(e.getKtClass()!!).map { ColumnTypeObject(it.key,it.dataIndex,it.chineseName) }
        val string = MyUtil.getPrettyJson(objects)
        e.project?.showCode(string,JsonLanguage.INSTANCE)
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


fun KtProperty.getType(): String? {
    val r = this.children.find { it is KtTypeReference }
    if(r!=null){
       return PsiTreeUtil.findChildOfType(r,KtUserType::class.java)?.text
    }
    return null
}

fun KtParameter.getParameterType(): String? {
    val r = this.children.find { it is KtTypeReference }
    if(r!=null){
        return PsiTreeUtil.findChildOfType(r,KtUserType::class.java)?.text
    }
    return null
}

/**
 * 是否可空类型
 */
fun KtProperty.isNull(): Boolean {
    return PsiTreeUtil.findChildOfType(this,KtNullableType::class.java) != null
}

/**
 * 是否可空类型
 */
fun KtParameter.isNull(): Boolean {
    return PsiTreeUtil.findChildOfType(this,KtNullableType::class.java) != null
}
