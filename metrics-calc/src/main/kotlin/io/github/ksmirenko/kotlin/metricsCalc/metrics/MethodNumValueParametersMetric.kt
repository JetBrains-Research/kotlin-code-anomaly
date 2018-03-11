package io.github.ksmirenko.kotlin.metricsCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.metricsCalc.records.MetricRecord
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodNumValueParametersMetric : Metric(
        id = MetricRecord.Type.MethodNumValueParameters,
        csvName = "numValueParameters",
        description = "Number of value parameters"
) {
    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        override fun visitElement(element: PsiElement?) {
            if (element !is KtNamedFunction) {
                return
            }

            val valueParameterCount = element.valueParameters.size
            val funName = element.fqName.toString()
            appendRecord(funName, valueParameterCount)
        }
    }
}
