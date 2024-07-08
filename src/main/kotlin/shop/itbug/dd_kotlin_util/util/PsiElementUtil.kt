package shop.itbug.dd_kotlin_util.util

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil


inline fun <reified T: PsiElement> PsiElement.filterByType(): List<T> {
    return PsiTreeUtil.findChildrenOfAnyType(this, T::class.java).toList()
}

inline fun <reified T: PsiElement> PsiElement.findByTypeAndText(text: String): T? {
    return filterByType<T>().find { it.text == text }
}

/**
 * 递归查找符合[T]类型的第一个元素
 */
inline fun <reified T: PsiElement> PsiElement.findFirstChild(): T? {
    try {
        return PsiTreeUtil.findChildOfType(this, T::class.java)
    } catch (e: Exception) {
        return null
    }
}

fun PsiElement.findLastLeafChild(): PsiElement? {
    return filterByType<LeafPsiElement>().lastOrNull()
}


/**
 * 查找第一个匹配的[T]父元素
 */
inline fun <reified T: PsiElement> PsiElement.findFirstParentChild(): PsiElement? {
    return PsiTreeUtil.findFirstParent(this){
        return@findFirstParent it is T
    }
}

/**
 * 查找[element]在文件中的引用列表
 */
fun PsiFile.getUseAge(element: PsiElement) : List<PsiReference> {
    return ReferencesSearch.search(element, LocalSearchScope(this)).findAll().toList()
}
