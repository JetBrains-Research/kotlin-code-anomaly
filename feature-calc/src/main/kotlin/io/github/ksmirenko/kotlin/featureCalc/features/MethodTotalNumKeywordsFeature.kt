package io.github.ksmirenko.kotlin.featureCalc.features

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.github.ksmirenko.kotlin.featureCalc.records.FeatureRecord
import org.jetbrains.kotlin.lexer.KtKeywordToken
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodTotalNumKeywordsFeature : Feature(
        id = FeatureRecord.Type.MethodTotalNumKeywords,
        csvName = "numKeywords",
        description = "Total number of keywords"
) {
    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var methodNestingDepth = 0
        private var keywordCount = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is LeafPsiElement -> {
                    if (element.elementType is KtKeywordToken) {
                        keywordCount++
                    }
                    super.visitElement(element)
                }
                is KtNamedFunction -> visitOuterFunction(element)

                else -> super.visitElement(element)
            }
        }

        private fun visitOuterFunction(function: KtNamedFunction) {
            if (methodNestingDepth == 0) {
                keywordCount = 0
            }

            methodNestingDepth++
            super.visitElement(function)
            methodNestingDepth--

            if (methodNestingDepth == 0) {
                val funName = function.fqName.toString()
                appendRecord(funName, keywordCount)
            }
        }
    }
}
