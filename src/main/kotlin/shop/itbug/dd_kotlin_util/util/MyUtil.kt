package shop.itbug.dd_kotlin_util.util

import com.google.gson.GsonBuilder
import com.intellij.ide.ClipboardSynchronizer
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ide.CopyPasteManager
import java.awt.datatransfer.StringSelection


object MyUtil {
    fun copyTextToClipboard(text: String) {
        try {
            CopyPasteManager.getInstance().setContents(StringSelection(text))
            ClipboardSynchronizer.getInstance().resetContent()
        } catch (e: Exception) {
            Logger.getInstance(MyUtil::class.java).error("复制剪贴板失败", e)
        }
    }

    fun getPrettyJson(obj: Any): String {
        return GsonBuilder().setPrettyPrinting().create().toJson(obj)
    }
}