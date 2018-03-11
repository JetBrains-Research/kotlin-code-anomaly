package io.github.ksmirenko.kotlin.metricsCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.metricsCalc.records.MetricRecord
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtIfExpression

/**
 * Calculates methods' design complexity.
 *
 * Based on the corresponding metrics from [MetricsReloaded](https://github.com/BasLeijdekkers/MetricsReloaded).
 *
 * The design complexity is related to how interlinked a methods control flow is with calls to other methods.
 * Design complexity ranges from 1 to the cyclomatic complexity of the method.
 */
class MethodDesignComplexityMetric : MethodComplexityMetric(
        id = MetricRecord.Type.MethodDesignComplexity,
        csvName = "designComplexity",
        description = "Design complexity"
) {
    private val callVisitor = CallVisitor()

    override fun isAccepted(element: PsiElement): Boolean {
        return when (element) {
            is KtIfExpression -> {
                containsMethodCall(element.then) || containsMethodCall(element.`else`)
            }
            else -> {
                containsMethodCall(element)
            }
        }
    }

    private fun containsMethodCall(element: PsiElement?): Boolean {
        if (element == null) {
            return false
        }
        callVisitor.reset()
        element.accept(callVisitor)
        return callVisitor.containsCall
    }

    inner class CallVisitor : JavaRecursiveElementVisitor() {
        var containsCall = false
            private set

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtCallExpression -> {
                    this.containsCall = true
                }
                else -> super.visitElement(element)
            }
        }

        fun reset() {
            containsCall = false
        }
    }
}
