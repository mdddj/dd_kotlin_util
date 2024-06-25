package shop.itbug.dd_kotlin_util.help

import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtUserType

val KtTypeReference.helper: KtTypeHelper get() = KtTypeHelper(this)

///类型转换帮助类
class KtTypeHelper(private val element: KtTypeReference) {
    private val factory = KtPsiFactory(element.project)

    //可空替换为不可空
    fun nullableRemove() {
        when (val ele = element.firstChild) {
            is KtNullableType -> {
                val typeText = ele.firstChild.text
                val newType = factory.createType(typeText)
                element.replace(newType)
            }

            is KtUserType -> {
                ele.typeArgumentsAsTypes.forEach { ref: KtTypeReference ->
                    ref.helper.nullableRemove()
                }
            }
        }
    }
}