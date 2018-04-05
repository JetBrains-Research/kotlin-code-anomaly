package io.github.ksmirenko.kotlin.featureCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.lexer.KtKeywordToken
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodNumDistinctKeywordsMetric : Metric(
        csvName = "numDistinctKeywords",
        description = "Число различных ключевых слов языка"
) {
    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var methodNestingDepth = 0
        private val keywords = mutableSetOf<String>()

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is LeafPsiElement -> {
                    if (element.elementType is KtKeywordToken) {
                        keywords.add(element.text)
                    }
                    super.visitElement(element)
                }
                is KtNamedFunction -> visitOuterFunction(element)

                else -> super.visitElement(element)
            }
        }

        private fun visitOuterFunction(function: KtNamedFunction) {
            if (methodNestingDepth == 0) {
                keywords.clear()
            }

            methodNestingDepth++
            super.visitElement(function)
            methodNestingDepth--

            if (methodNestingDepth == 0) {
                val funName = function.fqName.toString()
                appendRecord(funName, keywords.size)
            }
        }
    }
}
