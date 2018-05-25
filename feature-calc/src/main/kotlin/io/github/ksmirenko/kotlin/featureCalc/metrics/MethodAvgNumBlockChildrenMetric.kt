package io.github.ksmirenko.kotlin.featureCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodAvgNumBlockChildrenMetric : Metric(
        csvName = "avgNumBlockChildren",
        description = "Ср. число непосредственных потомков узла-блока"
) {
    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var methodNestingDepth = 0
        private var blockCount = 0
        private var blockChildrenCountSum = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtBlockExpression -> {
                    blockChildrenCountSum += element.children.size
                    blockCount++
                    super.visitElement(element)
                }

                is KtNamedFunction -> visitOuterFunction(element)
                else -> super.visitElement(element)
            }
        }

        private fun visitOuterFunction(function: KtNamedFunction) {
            if (methodNestingDepth == 0) {
                blockChildrenCountSum = 0
                blockCount = 0
            }

            methodNestingDepth++
            super.visitElement(function)
            methodNestingDepth--

            if (methodNestingDepth == 0) {
                val funName = function.fqName.toString()
                val value = if (blockCount > 0) (1.0 * blockChildrenCountSum / blockCount) else 0.0
                appendRecord(funName, value)
            }
        }
    }
}
