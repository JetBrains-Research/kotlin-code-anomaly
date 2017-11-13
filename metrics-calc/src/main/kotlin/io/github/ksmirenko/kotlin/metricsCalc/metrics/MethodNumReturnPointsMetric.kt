package io.github.ksmirenko.kotlin.metricsCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.metricsCalc.records.MetricRecord
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression

class MethodNumReturnPointsMetric : Metric() {
    override val headerName = "numReturns"
    override val description = "Number of return points"

    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var returnsCount = 0
        private var methodNestingDepth = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtNamedFunction -> visitKtFunction(element)
                is KtReturnExpression -> {
                    returnsCount += 1 // TODO: check whether it is a return from the function
                }
                else -> super.visitElement(element)
            }
        }

        private fun visitKtFunction(function: KtNamedFunction) {
            if (methodNestingDepth == 0) {
                returnsCount = 0
            }

            methodNestingDepth++
            super.visitElement(function)
            methodNestingDepth--

            if (methodNestingDepth == 0) {
                val funName = function.fqName.toString()
                appendRecord(MetricRecord(MetricRecord.Type.MethodNumReturns, funName, returnsCount))
            }
        }
    }
}
