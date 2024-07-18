package shop.itbug.dd_kotlin_util.action

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.OSProcessHandler
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.setEmptyState
import com.intellij.ui.components.fields.ExtendableTextField
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.io.File
import java.util.*
import javax.swing.BorderFactory

private class Input(emptyText: String? = null,onSubmit: (text: String) -> Unit) : ExtendableTextField() {
    init {
        emptyText?.let { setEmptyState(it) }
        this.border = BorderFactory.createEmptyBorder(0, 0, 0, 0)
        this.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent?) {
                if (e?.keyCode == KeyEvent.VK_ENTER) onSubmit(text)
            }
        })
    }
}

data class CovertData(
    val userEnterFilename: String,
    val selectFileName: String,
    val workDirectory: String,
    val event: AnActionEvent,
    val indicator: ProgressIndicator
)

data class CovertConfig(
    var taskTitle: String = "Running",
    var inputEmptyText: String? = null,
    var adText: String? = null,
    var popupTitle: String? = null,
)


abstract class InputFileCovertHandleAction : AnAction() {
    private lateinit var pop: JBPopup

    override fun actionPerformed(e: AnActionEvent) {
        showPopup(e)
    }

    private fun showPopup(event: AnActionEvent) {
        createPopup(event).showInBestPositionFor(event.dataContext)
    }

    private fun createPopup(event: AnActionEvent): JBPopup {
        val config = configuration()
        pop = JBPopupFactory.getInstance().createComponentPopupBuilder(Input(config.inputEmptyText) {
            this.pop.cancel()
            val file = event.getData(CommonDataKeys.VIRTUAL_FILE)!!
            val dirPath = file.parent.path
            val obj = object : Task.Backgroundable(event.project, config.taskTitle,true) {
                override fun run(indicator: ProgressIndicator) {
                    val data = CovertData(it, file.name, dirPath, event, indicator)
                    indicator.text = config.taskTitle
                    runTask(data)
                }
            }
            obj.queue()
        }, null).setRequestFocus(true)
            .setTitle(config.popupTitle?:"")
            .setResizable(false)
            .setMovable(true)
            .setAdText(config.adText)
            .setCancelKeyEnabled(true)
            .createPopup()
        return pop
    }

    abstract fun runTask(data: CovertData)
    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    ///配置
    abstract fun configuration() : CovertConfig

}


///视频转GIF操作
class FFmpegVideoToGifAction : InputFileCovertHandleAction() {
    override fun runTask(data: CovertData) {
        val gcl = GeneralCommandLine("ffmpeg").withWorkDirectory(data.workDirectory)
        gcl.addParameters("-i")
        gcl.addParameters(data.selectFileName)
        gcl.addParameters("${data.userEnterFilename}.gif")
        OSProcessHandler(gcl).startNotify()
    }

    override fun configuration(): CovertConfig = CovertConfig(
        taskTitle = "正在进行转换",
        inputEmptyText = "输入文件名,不需要.gif后缀",
        adText = "此功能需要安装ffmpeg工具",
        popupTitle = "输入文件名"
    )

    override fun update(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        e.presentation.isVisible = e.project != null && file != null && isVideoFile(file.path)
        super.update(e)
    }

    ///判断是否为视频文件
    private fun isVideoFile(filePath: String): Boolean {
        val videoExtensions =
            setOf("mp4", "avi", "mkv", "mov", "wmv", "flv", "webm", "mpeg", "mpg", "3gp", "3g2", "vob", "m4v")
        val file = File(filePath)
        val extension = file.extension.lowercase(Locale.getDefault())
        return videoExtensions.contains(extension)
    }
}


