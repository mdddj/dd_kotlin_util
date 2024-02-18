package shop.itbug.dd_kotlin_util.dialog

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import shop.itbug.dd_kotlin_util.constant.TempFileNames
import shop.itbug.dd_kotlin_util.model.GenerateModuleConfiguration
import shop.itbug.dd_kotlin_util.util.FileUtil
import shop.itbug.dd_kotlin_util.util.PsiUtil
import javax.swing.JComponent

fun Project.showGenerateModuleConfigurationDialog(event: AnActionEvent) {
    GenerateModuleConfigurationDialog(this, event).show()
}

/**
 * 生成module的配置弹窗
 */
class GenerateModuleConfigurationDialog(private val project: Project, event: AnActionEvent) :
    DialogWrapper(project) {


    private val psiDirectory = PsiUtil.findPsiDirectory(event)!!
    private val configuration =
        GenerateModuleConfiguration(packageName = FileUtil.getPackageName(project, psiDirectory))
    private lateinit var contentPanel: DialogPanel


    init {
        super.init()
        title = "生成module"
    }


    override fun createCenterPanel(): JComponent {
        contentPanel = panel {
            row("类名") {
                textField().bindText(configuration::className).validationInfo {
                    val empty = it.text.isEmpty()
                    if (empty) {
                         this.error("")
                    }
                     this.warning("").withOKEnabled()
                }
            }
            row("包名") {
                textField().bindText(configuration::packageName)
            }
        }
        return contentPanel
    }

    override fun doOKAction() {
        contentPanel.apply()
        start()
        super.doOKAction()
    }


    //开始生成文件
    private fun start() {
        TempFileNames.values().forEach {
            val content = FileUtil.loadTemplateFile(it.fileName)
            val ktFileText = it.format(it, content, configuration)
            val ktFile =
                PsiUtil.createKtPsiFile(project, configuration.className + it.fileNameSuffix + ".kt", ktFileText)
            PsiUtil.reformatPsiFile(project, ktFile)
            PsiUtil.addPsiFileToDirectory(ktFile, psiDirectory)
        }
    }
}