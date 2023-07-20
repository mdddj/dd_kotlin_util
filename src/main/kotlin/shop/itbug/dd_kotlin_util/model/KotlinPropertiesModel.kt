package shop.itbug.dd_kotlin_util.model

import com.alibaba.fastjson2.JSONWriter
import com.alibaba.fastjson2.toJSONString
import org.jetbrains.kotlin.psi.KtClass
import shop.itbug.dd_kotlin_util.extend.getModel


fun KotlinPropertiesModel.string(): String {
    return this.toJSONString(JSONWriter.Feature.PrettyFormat)
}


fun KotlinPropertiesModel.getTypeScriptType(): TypescriptValType {
    return when (valueTypeString) {
        "String" -> TypescriptValType.String
        "Date" -> TypescriptValType.Date
        "Long" -> TypescriptValType.Number
        "Int" -> TypescriptValType.Number
        "BigInteger" -> TypescriptValType.Number
        "BigDecimal" -> TypescriptValType.Number
        "Boolean" -> TypescriptValType.Boolean
        else -> TypescriptValType.Unknown
    }
}


/**
 * 获取字段名称,如果不存在,则使用[KotlinPropertiesModel#name]
 */
fun KotlinPropertiesModel.getSchemaDescription(): String {
    return getEnumValue("Schema", "description", name) as? String ?: name
}


/**
 * 简单的: <ProFormText name={'title'} label={'标题'} />
 */
fun KotlinPropertiesModel.generateAntdFormItem(): String {

    val rules = mutableListOf<AntdFormRule>() //这里可以定义多个规则
    if (!optionValue) {
        rules.add(AntdFormRule(required = true, message = getEnumValue("NotBlank", "message", "请输入字段内容")))
    }
    val ruleString = if (rules.isNotEmpty()) "rules={${rules.toJSONString(JSONWriter.Feature.PrettyFormat)}}" else ""

    val textItem =  "\n<ProFormText name={'$name'} label={'${getSchemaDescription()}'} $ruleString />\n"

    val checkboxItem = """
        \n
        <ProFormCheckbox name={'$name'} $ruleString >
            ${getSchemaDescription()}
        </ProFormCheckbox>
      \n
    """.trimIndent()


    val dataItem = """
        <ProFormDatePicker name={'$name'} label={'${getSchemaDescription()}'} $ruleString />
    """.trimIndent()

    return when(getTypeScriptType()){
        TypescriptValType.Boolean -> checkboxItem
        TypescriptValType.Date -> dataItem
        else -> textItem
    }
}

///生成antd表单
fun KtClass.generateAntdFormString() : String {
    val properties = this.getProperties()
    val sb = StringBuilder()
    properties.forEach {
        sb.appendLine("\t\t"+it.getModel().generateAntdFormItem())
    }
    return """
    <ModalForm>
       $sb
    </ModalForm>
    """.trimIndent()
}

/**
 * 获取枚举的值
 */
fun KotlinPropertiesModel.getEnumValue(enumName: String, properties: String, defaultValue: String?): String {
    val schema = enums.find { it.enumName == enumName }
    if (schema != null && schema.properties.contains(properties)) {
        return schema.propertiesValue[properties]?.toString() ?: defaultValue ?: ""
    }
    return defaultValue ?: ""
}

//类型
enum class TypescriptValType {
    String, Number, Boolean, Date, Unknown
}


//kt属性
data class KotlinPropertiesModel(
    //是val 还是 var,也就是是否可变
    val isFinal: Boolean,

    //字段名
    val name: String,

    //初始值
    val initValue: String?,

    //是否可空
    val optionValue: Boolean,

    //值的类型
    val valueTypeString: String?,

    //枚举列表
    val enums: List<KotlinEnumModel> = emptyList(),


    )

data class KotlinEnumModel(

    //枚举的名字
    val enumName: String,

    //构造值
    val constructorValue: String?,

    //声明的属性
    val properties: List<String> = emptyList(),

    //值 <-> value
    val propertiesValue: MutableMap<String, Any>

)

//antd 规则
data class AntdFormRule(
    val required: Boolean?,
    val message: String?
)