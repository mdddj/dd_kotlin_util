package shop.itbug.dd_kotlin_util.help

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.IncorrectOperationException
import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtUserType

val KtNullableType.helper: KtNullableTypeHelper get() = KtNullableTypeHelper(this)

///类型转换帮助类
class KtNullableTypeHelper(private val element: KtNullableType) {
    private val factory = KtPsiFactory(element.project)

    fun nullableRemove() {
        element.fix()
    }

    private fun checkChildElement(checkElement: PsiElement) {
        val findAll = PsiTreeUtil.findChildrenOfAnyType(checkElement, KtNullableType::class.java)
        findAll.forEach {
            it.fix()
        }
    }

    private fun KtNullableType.fix() {
        findUserType()?.let { userType ->
            run {
                if (text != userType.text) {
                    val newElement = factory.createType(userType.text)
                    try {
                        val newInsertElement = this.replace(newElement)
                        checkChildElement(newInsertElement)
                    } catch (_: IncorrectOperationException) { }
                }
            }
        }
    }

    private fun KtNullableType.findUserType(): KtUserType? {
        return PsiTreeUtil.findChildOfAnyType(this, KtUserType::class.java)
    }
}

