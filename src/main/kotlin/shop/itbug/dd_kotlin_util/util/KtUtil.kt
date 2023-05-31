package shop.itbug.dd_kotlin_util.util

import com.intellij.openapi.project.Project
import com.intellij.psi.util.PsiTreeUtil
import org.apache.commons.io.IOUtils
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPsiFactory
import java.io.File

object KtUtil {
    fun createClass(project: Project) {
        val ktClass = KtPsiFactory(project, true).createClass("")
    }

    /**
     * 获取注解属性的值
     */
    fun getAnnotationPropertiesName(ktProperty: KtProperty,annotationName: String,key: String) : String {
        var name = ktProperty.name ?: "未知"
        val annotationList = PsiTreeUtil.findChildrenOfType(ktProperty, KtAnnotationEntry::class.java)
        annotationList.forEach { anno ->
            if(anno.shortName.toString() == annotationName){
                val nameArg = anno.valueArgumentList?.arguments?.find{ ag -> ag.getArgumentName()!=null && ag.getArgumentName()!=null && ag.getArgumentName()!!.text == key }
                nameArg?.stringTemplateExpression?.text?.apply {
                    name = this.replace("\"","")
                }
            }
        }
        return name
    }


}