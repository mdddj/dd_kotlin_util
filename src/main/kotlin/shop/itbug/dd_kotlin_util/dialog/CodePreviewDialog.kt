package shop.itbug.dd_kotlin_util.dialog

import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.LanguageTextField
import com.intellij.util.ui.components.BorderLayoutPanel
import shop.itbug.dd_kotlin_util.component.MyJavaScriptTextField
import shop.itbug.dd_kotlin_util.util.MyUtil
import javax.swing.JComponent

fun Project.showCode(codeString: String, language: Language = PlainTextLanguage.INSTANCE) {
    CodePreviewDialog(this, codeString, language).show()
}

class CodePreviewDialog(
    project: Project,
    private val codeString: String,
    language: Language = PlainTextLanguage.INSTANCE
) : DialogWrapper(project) {
    private val editor = LanguageTextField(language, project, codeString, false)

    init {
        super.init()
        title = "代码预览"
        setOKButtonText("复制代码")
        setCancelButtonText("取消")
        editor.editor?.settings?.isLineNumbersShown = true
    }

    override fun createCenterPanel(): JComponent {
        return BorderLayoutPanel().apply {
            addToCenter(editor)
        }
    }

    override fun doOKAction() {
        MyUtil.copyTextToClipboard(codeString)
        super.doOKAction()
    }

}

class MyTypeScriptCodeShow(val project: Project, private val codeString: String) : DialogWrapper(project) {


    init {
        super.init()
        super.setOKButtonText("复制代码")
        super.setCancelButtonText("取消")
        super.setTitle("Kotlin生成模型")
    }
    override fun createCenterPanel(): JComponent {
        return MyJavaScriptTextField(project,codeString)
    }

    override fun doOKAction() {
        MyUtil.copyTextToClipboard(codeString)
        super.doOKAction()
    }


}