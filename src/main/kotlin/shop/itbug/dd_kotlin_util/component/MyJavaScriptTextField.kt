package shop.itbug.dd_kotlin_util.component

import com.intellij.lang.Language
import com.intellij.lang.javascript.dialects.TypeScriptJSXLanguageDialect
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.project.Project
import com.intellij.ui.LanguageTextField
import java.awt.Font
import javax.swing.BorderFactory
import javax.swing.border.Border

///Javascript显示组件.
class MyJavaScriptTextField(project: Project, initText: String = "") :
    LanguageTextField(Language.findInstance(TypeScriptJSXLanguageDialect::class.java), project, initText, false) {


    override fun getFont(): Font {
        val font = EditorColorsManager.getInstance().globalScheme.getFont(EditorFontType.PLAIN)
        return font
    }


    override fun createEditor(): EditorEx {
        val ex = super.createEditor()
        ex.setBorder(BorderFactory.createEmptyBorder(0,0,0,0))
        ex.setVerticalScrollbarVisible(true)
        ex.setHorizontalScrollbarVisible(true)
        ex.settings.apply {
            isLineNumbersShown = true
        }
        return ex
    }

    override fun getBorder(): Border {
        return BorderFactory.createEmptyBorder(0, 0, 0, 0)
    }

}