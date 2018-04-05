package io.github.ksmirenko.kotlin.featureCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodNumSingleChildBlocksMetric : Metric(
        csvName = "numOneChildBlocks",
        description = "Число блоков c одним непосредственным потомком"
) {
    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var methodNestingDepth = 0
        private var oneChildBlockCount = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtBlockExpression -> {
                    if (element.children.size == 1) {
                        oneChildBlockCount++
                    }
                    super.visitElement(element)
                }

                is KtNamedFunction -> visitOuterFunction(element)
                else -> super.visitElement(element)
            }
        }

        private fun visitOuterFunction(function: KtNamedFunction) {
            if (methodNestingDepth == 0) {
                oneChildBlockCount = 0
            }

            methodNestingDepth++
            super.visitElement(function)
            methodNestingDepth--

            if (methodNestingDepth == 0) {
                val funName = function.fqName.toString()
                appendRecord(funName, oneChildBlockCount)
            }
        }
    }
}
