package shop.itbug.dd_kotlin_util.icons

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object DDIcon {
    val FixIcon = getIconName("fix")
    private fun getIconName(iconName: String): Icon {
        return IconLoader.getIcon("/icons/$iconName.svg",DDIcon::class.java)
    }

}