package shop.itbug.dd_kotlin_util.util

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil


inline fun <reified T: PsiElement> PsiElement.filterByType(): List<T> {
    return PsiTreeUtil.findChildrenOfAnyType(this, T::class.java).toList()
}

inline fun <reified T: PsiElement> PsiElement.findByTypeAndText(text: String): T? {
    return filterByType<T>().find { it.text == text }
}

fun PsiElement.findLastLeafChild(): PsiElement? {
    return filterByType<LeafPsiElement>().lastOrNull()
}
