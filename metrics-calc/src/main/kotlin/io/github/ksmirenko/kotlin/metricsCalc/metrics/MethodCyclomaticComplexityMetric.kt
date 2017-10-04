package io.github.ksmirenko.kotlin.metricsCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.github.ksmirenko.kotlin.metricsCalc.records.MetricRecord
import org.jetbrains.kotlin.psi.*

/**
 * Calculates methods' cyclomatic complexity.
 *
 * Based on the corresponding metrics from [MetricsReloaded project](https://github.com/BasLeijdekkers/MetricsReloaded).
 *
 * In practice, this is 1 + the number of
 * - loop expressions,
 * - if's,
 * - when conditions,
 * - catch clauses,
 * - &&'s and ||'s
 * in the method."
 */
class MethodCyclomaticComplexityMetric : Metric() {
    override val description = "Cyclomatic complexity"

    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var methodNestingDepth = 0
        private var complexity = 1

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtNamedFunction -> visitKtFunction(element)
                is KtLoopExpression -> visitKtLoopExpression(element)
                is KtIfExpression -> visitKtIfExpression(element)
                is KtWhenCondition -> visitKtWhenCondition(element)
                is KtCatchClause -> visitKtCatchClause(element)
                is LeafPsiElement -> visitLeafPsiElement(element)
                else -> super.visitElement(element)
            }
        }

        private fun visitKtFunction(function: KtNamedFunction) {
            if (methodNestingDepth == 0) {
                complexity = 1
            }

            methodNestingDepth++
            super.visitElement(function)
            methodNestingDepth--

            if (methodNestingDepth == 0) {
                val funName = function.fqName.toString()
                appendRecord(MetricRecord(MetricRecord.Type.MethodCyclomaticComplexity, funName, complexity))
            }
        }

        private fun visitKtLoopExpression(loopExpression: KtLoopExpression) {
            super.visitElement(loopExpression)
            complexity += 1
        }

        private fun visitKtIfExpression(ifExpression: KtIfExpression) {
            super.visitElement(ifExpression)
            complexity += 1
        }

        private fun visitKtWhenCondition(whenCondition: KtWhenCondition) {
            super.visitElement(whenCondition)
            complexity += 1
        }

        private fun visitKtCatchClause(catchClause: KtCatchClause) {
            super.visitElement(catchClause)
            complexity += 1
        }

        private fun visitLeafPsiElement(leafElement: LeafPsiElement) {
            super.visitElement(leafElement)
            if (leafElement.text == "&&" || leafElement.text == "||") {
                complexity += 1
            }
        }
    }
}