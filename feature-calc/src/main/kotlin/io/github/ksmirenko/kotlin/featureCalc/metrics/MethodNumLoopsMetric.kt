package io.github.ksmirenko.kotlin.featureCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.*

class MethodNumLoopsMetric : Metric(
        csvName = "numLoopStatements",
        description = "Number of loop statements"
) {
    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var methodNestingDepth = 0
        private var loopCount = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtDoWhileExpression -> {
                    loopCount += 1
                    super.visitElement(element)
                }
                is KtForExpression -> {
                    loopCount += 1
                    super.visitElement(element)
                }
                is KtWhileExpression -> {
                    loopCount += 1
                    super.visitElement(element)
                }

                is KtNamedFunction -> visitOuterFunction(element)
                else -> super.visitElement(element)
            }
        }

        private fun visitOuterFunction(function: KtNamedFunction) {
            if (methodNestingDepth == 0) {
                loopCount = 0
            }

            methodNestingDepth++
            super.visitElement(function)
            methodNestingDepth--

            if (methodNestingDepth == 0) {
                val funName = function.fqName.toString()
                appendRecord(funName, loopCount)
            }
        }
    }
}
