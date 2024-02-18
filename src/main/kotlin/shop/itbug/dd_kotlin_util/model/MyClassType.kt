package shop.itbug.dd_kotlin_util.model

data class MyClassType(
    val key: String,
    val dataIndex: String,
    val chineseName: String,
    val type: String,
    val isNull: Boolean
)

fun MyClassType.getName(): String {
    if (isNull) {
        return "$key?"
    }
    return key
}





fun MyClassType.getTypescriptTypeName(): String {
    return when (type) {
        "String" -> "string"
        "Int" -> "number"
        "Boolean" -> "boolean"
        "BigInteger" -> "BigInt"
        "BigDecimal" -> "Decimal"
        "Date" -> "Date"
        "Long" -> "number"
        else -> type
    }
}


fun MyClassType.generateAttr(): String {
    return "\t${getName()}: ${getTypescriptTypeName()},"
}

fun MutableList<MyClassType>.generateTypescriptInterface(name: String): String {
    val sb = StringBuilder()
    sb.append("export interface $name {\n")
    forEach {
        sb.appendLine(it.generateAttr())
    }
    sb.append("}")
    return sb.toString()
}


