package io.github.ksmirenko.kotlin.metricsCalc.metrics

import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.metricsCalc.records.MetricRecord

/**
 * Calculates methods' cyclomatic complexity.
 *
 * Based on the corresponding metrics from [MetricsReloaded](https://github.com/BasLeijdekkers/MetricsReloaded).
 *
 * In practice, this is 1 + the number of
 * - loop expressions,
 * - if's,
 * - when conditions,
 * - catch clauses,
 * - &&'s and ||'s
 * in the method.
 */
class MethodCyclomaticComplexityMetric : MethodComplexityMetric(
        id = MetricRecord.Type.MethodCyclomaticComplexity,
        csvName = "cyclomaticComplexity",
        description = "Cyclomatic complexity"
) {

    override fun isAccepted(element: PsiElement): Boolean {
        // count all complexity-related nodes
        return true
    }
}
