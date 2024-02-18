package shop.itbug.dd_kotlin_util.util

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.idea.base.psi.findSingleLiteralStringTemplateText
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import shop.itbug.dd_kotlin_util.action.getParameterType
import shop.itbug.dd_kotlin_util.action.getType
import shop.itbug.dd_kotlin_util.action.isNull
import shop.itbug.dd_kotlin_util.extend.getModel
import shop.itbug.dd_kotlin_util.extend.getMyParameterModel
import shop.itbug.dd_kotlin_util.model.MyClassType
import shop.itbug.dd_kotlin_util.model.MyException
import shop.itbug.dd_kotlin_util.model.generateTypescriptInterface
import shop.itbug.dd_kotlin_util.model.getSchemaDescription

/// kt class 对象函数

//获取class类名
val KtClass.getClassName: String get() = name ?: throw MyException("获取类型失败")

//获取typescript模型
val KtClass.getTypescriptInterface: String?
    get() {
        try {
            val classTypeList = KtUtil.getClassTypeList(this)
            return classTypeList.generateTypescriptInterface(getClassName)
        } catch (e: Exception) {
            return null
        }
    }


/// kt 函数


object KtUtil {

    /**
     * 获取注解属性的值
     */
    private fun getAnnotationPropertiesName(ktProperty: KtProperty, annotationName: String, key: String): String {
        var name = ktProperty.name ?: "未知"
        val annotationList = PsiTreeUtil.findChildrenOfType(ktProperty, KtAnnotationEntry::class.java)
        annotationList.forEach { anno ->
            if (anno.shortName.toString() == annotationName) {
                val nameArg =
                    anno.valueArgumentList?.arguments?.find { ag -> ag.getArgumentName() != null && ag.getArgumentName() != null && ag.getArgumentName()!!.text == key }
                nameArg?.stringTemplateExpression?.text?.apply {
                    name = this.replace("\"", "")
                }
            }
        }
        return name
    }


    /**
     * 获取方法体
     */
    fun getSelectKtNameFunction(e: AnActionEvent): KtNamedFunction? {
        val value = PsiTreeUtil.findFirstParent(e.getData(CommonDataKeys.PSI_ELEMENT)) { t -> t is KtNamedFunction }
        return value as? KtNamedFunction
    }


    /**
     * 获取 springboot api类型注解
     */
    fun findMappingAnnotationEntries(namedFunction: KtNamedFunction): KtAnnotationEntry? {
        val mappingTexts = listOf("PostMapping", "GetMapping", "DeleteMapping", "PutMapping", "PatchMapping")
        val entries = namedFunction.annotationEntries
        if (entries.isEmpty()) return null
        return entries.find { mappingTexts.contains(it.shortName.toString()) }
    }

    private fun findParentClassRequestMappingApi(namedFunction: KtNamedFunction): String? {
        val parent = PsiTreeUtil.findFirstParent(namedFunction) { it is KtClass } as? KtClass
        if (parent != null) {
            findAnnotationEntries(parent.annotationEntries) { it.shortName?.toString() == "RequestMapping" }?.apply {
                val args = this.valueArguments
                if (args.isNotEmpty()) {
                    return args.first().findSingleLiteralStringTemplateText()
                }
            }
        }
        return null
    }

    private fun findAnnotationEntries(
        entriesList: List<KtAnnotationEntry>,
        match: (entries: KtAnnotationEntry) -> Boolean
    ): KtAnnotationEntry? {
        return entriesList.find { match.invoke(it) }
    }


    /**
     * 获取访问 api
     */
    fun getApiWithKtFun(ktNamedFunction: KtNamedFunction): String? {
        val entry = findMappingAnnotationEntries(ktNamedFunction)!!
        val valueArguments = entry.valueArguments
        val apiTexts = valueArguments.map { it.findSingleLiteralStringTemplateText() ?: "" }.filter { it.isNotBlank() }
        val classApiText = findParentClassRequestMappingApi(ktNamedFunction) ?: ""
        if (apiTexts.isNotEmpty()) {
            val api = apiTexts.first()
            return classApiText + api
        }
        return null
    }


    fun getClassTypeList(ktClass: KtClass): MutableList<MyClassType> {
        val properties = ktClass.getProperties()
        val objects = mutableListOf<MyClassType>()
        properties.forEach {
            val model = it.getModel()
            objects.add(
                MyClassType(
                    it.name ?: "",
                    it.name ?: "",
                    model.getSchemaDescription(),
                    it.getType() ?: "",
                    it.isNull()
                )
            )
        }
        println(ktClass.primaryConstructor?.valueParameterList?.parameters)
        ktClass.primaryConstructor?.valueParameterList?.parameters?.forEach { item ->
            run {
                val model = item.getMyParameterModel()
                objects.add(
                    MyClassType(
                        item.name ?: "",
                        item.name ?: "",
                        model.getSchemaDescription(),
                        item.getParameterType() ?: "",
                        item.isNull()
                    )
                )
            }
        }
        return objects
    }


}
