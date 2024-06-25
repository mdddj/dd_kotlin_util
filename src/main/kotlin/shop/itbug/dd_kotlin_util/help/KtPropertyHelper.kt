package shop.itbug.dd_kotlin_util.help

import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.psi.KtProperty

val KtProperty.helper: KtPropertyHelper get() = KtPropertyHelper(this)
///帮助类
class KtPropertyHelper(private val element: KtProperty) {




    ///是否有[name]注解
    fun hasAnnotation(name: String): Boolean {
       return element.modifierList?.annotationEntries?.any { it.shortName?.asString() == name } == true
    }


    fun isAutowired() = hasAnnotation("Autowired")
    fun isResource() = hasAnnotation("Resource")
    fun isAutowiredOrResource() = isAutowired() || isResource()

    ///
    fun isNullableType() = element.typeReference?.typeElement is KtNullableType

}