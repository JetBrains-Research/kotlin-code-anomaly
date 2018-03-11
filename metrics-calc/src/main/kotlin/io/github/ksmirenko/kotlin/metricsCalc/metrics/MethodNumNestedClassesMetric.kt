package io.github.ksmirenko.kotlin.metricsCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.metricsCalc.records.MetricRecord
import org.jetbrains.kotlin.psi.*

class MethodNumNestedClassesMetric : Metric(
        id = MetricRecord.Type.MethodNumNestedClasses,
        csvName = "numNestedClasses",
        description = "Number of nested classes, including indirectly nested (through a function too)"
) {
    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var funNestingDepth = 0
        private var numNestedClasses = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtNamedFunction -> visitKtFunction(element)
                is KtClassOrObject -> {
                    numNestedClasses += 1
                    super.visitElement(element)
                }
                else -> super.visitElement(element)
            }
        }

        private fun visitKtFunction(function: KtNamedFunction) {
            if (funNestingDepth == 0) {
                numNestedClasses = 0
            }

            funNestingDepth++
            super.visitElement(function)
            funNestingDepth--

            if (funNestingDepth == 0) {
                val funName = function.fqName.toString()
                appendRecord(funName, numNestedClasses)
            }
        }
    }
}
