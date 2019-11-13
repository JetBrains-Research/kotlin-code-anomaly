package io.github.ksmirenko.kotlin.featureCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtWhenEntry
import org.jetbrains.kotlin.psi.KtWhenExpression
import java.util.*

class MethodAvgNumWhenEntriesMetric : Metric(
        csvName = "avgNumWhenEntries",
        description = "Ср. число веток в одном when-выражении"
) {
    override val visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var methodNestingDepth = 0
        private var sumBranchesCount = 0
        private var whenCount = 0
        private var counterStack = Stack<Int>()
        private var counter = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtWhenExpression -> {
                    counterStack.push(counter)
                    counter = 0
                    super.visitElement(element)
                    sumBranchesCount += counter
                    whenCount++
                    counter = counterStack.pop()
                }
                is KtWhenEntry -> {
                    counter += 1
                    super.visitElement(element)
                }

                is KtNamedFunction -> visitOuterFunction(element)
                else -> super.visitElement(element)
            }
        }

        private fun visitOuterFunction(function: KtNamedFunction) {
            if (methodNestingDepth == 0) {
                sumBranchesCount = 0
                whenCount = 0
            }

            methodNestingDepth++
            super.visitElement(function)
            methodNestingDepth--

            if (methodNestingDepth == 0) {
                val funName = function.fqName.toString()
                val value = if (whenCount > 0) (1.0 * sumBranchesCount / whenCount) else 0.0
                appendRecord(funName, value)
            }
        }
    }
}
