package shop.itbug.dd_kotlin_util.util

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtPsiFactory


class Test {
    lateinit var test: String
    fun test(){
        test.plus("")
    }
}


data class CreatePsiElementException(val msg: String): Exception(){
    override fun getLocalizedMessage(): String {
        return msg
    }
}

class MyKtPsiFactory(val project: Project) {

    private var factory = KtPsiFactory(project)


    fun createLateinit(): PsiElement {
        return factory.createParameter("lateinit var test: String").modifierList?.firstChild ?: throw CreatePsiElementException("创建lateinit节点失败")
    }

    fun createNameReferenceExpression(name: String): KtNameReferenceExpression {
        val clazz = factory.createClass("""
            class Test {
                val $name: String = ""
                fun test(){
                    $name.plus("")
                }
            }
        """.trimIndent())
        return clazz.body?.functions?.first()?.bodyBlockExpression?.filterByType<KtDotQualifiedExpression>()?.first()?.firstChild as KtNameReferenceExpression
    }


}