package io.github.ksmirenko.kotlin.featureCalc.features

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.featureCalc.records.FeatureRecord
import org.jetbrains.kotlin.lexer.KtSingleValueToken
import org.jetbrains.kotlin.psi.*

/**
 * Calculates methods' complexity.
 *
 * Based on the corresponding features from [MetricsReloaded](https://github.com/BasLeijdekkers/MetricsReloaded).
 *
 * Visits the following elements:
 * - loop expressions,
 * - if's,
 * - when conditions,
 * - catch clauses,
 * - &&'s and ||'s
 * and applies the implementation-defined filter to decide whether to count each element.
 */
abstract class MethodComplexityFeature(id: FeatureRecord.Type, csvName: String, description: String)
    : Feature(id, csvName, description) {

    override val visitor: Visitor by lazy { Visitor() }

    protected abstract fun isAccepted(element: PsiElement): Boolean

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var isInsideFunction = false
        private var complexity = 1

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtLoopExpression -> visitKtLoopExpression(element)
                is KtIfExpression -> visitKtIfExpression(element)
                is KtWhenCondition -> visitKtWhenCondition(element)
                is KtCatchClause -> visitKtCatchClause(element)
                is KtBinaryExpression -> visitKtBinaryExpression(element)

                is KtNamedFunction -> visitOuterFunction(element)
                is KtClassOrObject -> {
                }  // skip nested classes
                else -> super.visitElement(element)
            }
        }

        private fun visitOuterFunction(function: KtNamedFunction) {
            if (isInsideFunction) {
                // skip nested functions
                return
            }
            isInsideFunction = true

            complexity = 1
            super.visitElement(function)

            val funName = function.fqName.toString()
            appendRecord(funName, complexity)
            isInsideFunction = false
        }

        private fun visitKtLoopExpression(loopExpression: KtLoopExpression) {
            super.visitElement(loopExpression)
            if (isAccepted(loopExpression)) {
                complexity += 1
            }
        }

        private fun visitKtIfExpression(ifExpression: KtIfExpression) {
            super.visitElement(ifExpression)
            if (isAccepted(ifExpression)) {
                complexity += 1
            }
        }

        private fun visitKtWhenCondition(whenCondition: KtWhenCondition) {
            super.visitElement(whenCondition)
            if (isAccepted(whenCondition)) {
                complexity += 1
            }
        }

        private fun visitKtCatchClause(catchClause: KtCatchClause) {
            super.visitElement(catchClause)
            if (isAccepted(catchClause)) {
                complexity += 1
            }
        }

        private fun visitKtBinaryExpression(binaryExpression: KtBinaryExpression) {
            super.visitElement(binaryExpression)
            if (!isAccepted(binaryExpression)) {
                return
            }
            val operation = (binaryExpression.operationToken as? KtSingleValueToken)?.value ?: return
            if (operation == "&&" || operation == "||") {
                complexity += 1
            }
        }
    }
}
