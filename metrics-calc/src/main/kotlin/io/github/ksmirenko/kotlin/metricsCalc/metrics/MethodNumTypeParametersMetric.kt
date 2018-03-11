package io.github.ksmirenko.kotlin.metricsCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.metricsCalc.records.MetricRecord
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodNumTypeParametersMetric : Metric(
        id = MetricRecord.Type.MethodNumTypeParameters,
        csvName = "numTypeParameters",
        description = "Number of type parameters"
) {
    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        override fun visitElement(element: PsiElement?) {
            if (element !is KtNamedFunction) {
                return
            }

            val valueParameterCount = element.typeParameters.size
            val funName = element.fqName.toString()
            appendRecord(funName, valueParameterCount)
        }
    }
}
