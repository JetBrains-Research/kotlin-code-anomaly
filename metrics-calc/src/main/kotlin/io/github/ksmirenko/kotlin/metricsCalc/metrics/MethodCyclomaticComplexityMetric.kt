package io.github.ksmirenko.kotlin.metricsCalc.metrics

import com.intellij.psi.PsiElement

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
class MethodCyclomaticComplexityMetric : MethodComplexityMetric() {
    override val headerName = "cyclomaticComplexity"
    override val description = "Cyclomatic complexity"

    override fun isAccepted(element: PsiElement): Boolean {
        // count all complexity-related nodes
        return true
    }
}
