package io.github.ksmirenko.kotlin.metricsCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.metricsCalc.records.MetricRecord
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression

class MethodNumReturnPointsMetric : Metric() {
    override val headerName = "numReturns"
    override val description = "Number of return points"

    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var returnsCount = 0
        private var scopeNestingDepth = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtNamedFunction -> visitKtFunction(element)
                is KtReturnExpression -> {
                    if (scopeNestingDepth <= 1) {
                        returnsCount += 1
                    }
                }
                is KtLambdaExpression -> {
                    skip(element)
                }
                else -> super.visitElement(element)
            }
        }

        private fun visitKtFunction(function: KtNamedFunction) {
            if (scopeNestingDepth == 0) {
                returnsCount = 0
            }

            scopeNestingDepth++
            super.visitElement(function)
            scopeNestingDepth--

            if (scopeNestingDepth == 0) {
                val funName = function.fqName.toString()
                appendRecord(MetricRecord(MetricRecord.Type.MethodNumReturns, funName, returnsCount))
            }
        }

        private fun skip(element: PsiElement) {
            scopeNestingDepth += 1
            super.visitElement(element)
            scopeNestingDepth -= 1
        }
    }
}
