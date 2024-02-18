package shop.itbug.dd_kotlin_util.extend

import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.idea.base.psi.findSingleLiteralStringTemplateText
import org.jetbrains.kotlin.psi.KtModifierList
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtValueArgumentName
import shop.itbug.dd_kotlin_util.action.getParameterType
import shop.itbug.dd_kotlin_util.action.getType
import shop.itbug.dd_kotlin_util.model.KotlinEnumModel
import shop.itbug.dd_kotlin_util.model.KotlinPropertiesModel



///data class properties
fun KtParameter.getMyParameterModel(): KotlinPropertiesModel {
    return  KotlinPropertiesModel(
        isFinal = !this.isVarArg,
        name = this.nameIdentifier?.text?:"",
        initValue = "",
        optionValue = this.typeReference?.text?.endsWith("?") ?: false,
        valueTypeString = getParameterType(),
        enums = modifierList.getMyEnums()
    )

}

fun KtProperty.getModel(): KotlinPropertiesModel {
    val initValueText = if (initializer?.text == "null") null else initializer?.text //初始化值,如果是null则不存在
    return KotlinPropertiesModel(
        isFinal = isVar.not(),
        initValue = initValueText,
        name = name ?: "",
        optionValue = initValueText == null,
        valueTypeString = getType(),
        enums = modifierList.getMyEnums()
    )
}

fun KtModifierList?.getMyEnums()  : MutableList<KotlinEnumModel> {
    val enums = mutableListOf<KotlinEnumModel>()
    if (this != null) {
        annotationEntries.forEach {
            val nameText = it.calleeExpression?.typeReference?.getTypeText()//枚举名字
            val ktValueArgumentName = PsiTreeUtil.findChildOfType(it.valueArgumentList, KtValueArgumentName::class.java)
            val constructorValue =
                if (ktValueArgumentName == null && it.valueArguments.size == 1) it.valueArguments.first()
                    .findSingleLiteralStringTemplateText() else null
            val properties = PsiTreeUtil.findChildrenOfAnyType(it.valueArgumentList, KtValueArgumentName::class.java)
                .map { e -> e.text }

            val args = mutableMapOf<String, Any>()
            it.valueArguments.forEach { a ->
                val k = a.getArgumentName()?.referenceExpression?.text ?: ""
                val text = a.findSingleLiteralStringTemplateText()
                if (text != null) {
                    args[k] = text
                } else {
                    args[k] = a.asElement().lastChild.text
                }

            }


            enums.add(
                KotlinEnumModel(
                    enumName = nameText ?: "",
                    constructorValue = constructorValue,
                    properties = properties,
                    propertiesValue = args,
                )
            )
        }
    }

    return enums
}