package io.github.ksmirenko.kotlin.featureCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.*
import kotlin.math.max

class MethodMaxNumBlockChildrenMetric : Metric(
        csvName = "maxNumBlockChildren",
        description = "Макс. число непосредственных потомков узла-блока"
) {
    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var methodNestingDepth = 0
        private var maxBlockChildrenCount = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtBlockExpression -> {
                    maxBlockChildrenCount = max(maxBlockChildrenCount, element.children.size)
                    super.visitElement(element)
                }

                is KtNamedFunction -> visitOuterFunction(element)
                else -> super.visitElement(element)
            }
        }

        private fun visitOuterFunction(function: KtNamedFunction) {
            if (methodNestingDepth == 0) {
                maxBlockChildrenCount = 0
            }

            methodNestingDepth++
            super.visitElement(function)
            methodNestingDepth--

            if (methodNestingDepth == 0) {
                val funName = function.fqName.toString()
                appendRecord(funName, maxBlockChildrenCount)
            }
        }
    }
}
