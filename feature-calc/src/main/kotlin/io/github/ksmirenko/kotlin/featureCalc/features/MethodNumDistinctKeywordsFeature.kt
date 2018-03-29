package io.github.ksmirenko.kotlin.featureCalc.features

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.featureCalc.records.FeatureRecord
import org.jetbrains.kotlin.lexer.KtKeywordToken
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodNumDistinctKeywordsFeature : Feature(
        id = FeatureRecord.Type.MethodNumDistinctKeywords,
        csvName = "numDistinctKeywords",
        description = "Number of distinct keywords"
) {
    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var isInsideFunction = false
        private val keywords = mutableSetOf<String>()

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtKeywordToken -> {
                    keywords.add(element.text)
                }
                is KtNamedFunction -> visitOuterFunction(element)

                else -> super.visitElement(element)
            }
        }

        private fun visitOuterFunction(function: KtNamedFunction) {
            if (isInsideFunction) {
                // skip nested functions
                return
            }
            isInsideFunction = true

            keywords.clear()
            super.visitElement(function)

            val funName = function.fqName.toString()
            appendRecord(funName, keywords.size)
            isInsideFunction = false
        }
    }
}
