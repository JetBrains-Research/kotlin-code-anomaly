package io.github.ksmirenko.kotlin.metricsCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.metricsCalc.records.MetricRecord
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodNumValueParametersMetric : Metric() {
    override val headerName = "numValueParameters"
    override val description = "Number of value parameters"

    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var methodNestingDepth = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtNamedFunction -> visitKtFunction(element)
                else -> super.visitElement(element)
            }
        }

        private fun visitKtFunction(function: KtNamedFunction) {
            methodNestingDepth++
            super.visitElement(function)
            methodNestingDepth--

            if (methodNestingDepth == 0) {
                val valueParameterCount = function.valueParameters.size
                val funName = function.fqName.toString()
                appendRecord(MetricRecord(MetricRecord.Type.MethodNumValueParameters, funName, valueParameterCount))
            }
        }
    }
}
