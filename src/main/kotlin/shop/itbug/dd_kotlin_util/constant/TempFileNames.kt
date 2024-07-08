package shop.itbug.dd_kotlin_util.constant

import shop.itbug.dd_kotlin_util.model.GenerateModuleConfiguration
import java.util.*

enum class TempFileNames(val fileName: String, val fileNameSuffix: String) {
    entity("entity.txt", ""),
    repository("repository.txt", "Repository"),
    service("service.txt", "Service"),
    serviceImpl("service_impl.txt", "ServiceImpl"),
    controller("controller.txt", "Controller");


    fun format(temp: TempFileNames, content: String, configuration: GenerateModuleConfiguration): String {
        return when (temp) {
            entity -> content.format(configuration.packageName,configuration.className)
            repository -> content.format(configuration.packageName,configuration.className,configuration.className)
            service -> content.replace("@package", configuration.packageName).replace("@name", configuration.className)
            serviceImpl -> content.replace("@package", configuration.packageName)
                .replace("@name", configuration.className)
                .replace("@mininame", configuration.className.lowercase(Locale.getDefault()))

            controller -> content.replace("@package", configuration.packageName)
                .replace("@name", configuration.className)
                .replace("@mininame", configuration.className.lowercase(Locale.getDefault()))
        }
    }
}

