package io.github.ksmirenko.kotlin.featureCalc.features

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.featureCalc.records.FeatureRecord
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodNumMethodCallsFeature : Feature(
        id = FeatureRecord.Type.MethodNumMethodCalls,
        csvName = "numMethodCalls",
        description = "Number of method call expressions"
) {

    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var isInsideFunction = false
        private var callExprCount = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtCallExpression -> {
                    callExprCount += 1
                    super.visitElement(element)
                }

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

            callExprCount = 0
            super.visitElement(function)

            val funName = function.fqName.toString()
            appendRecord(funName, callExprCount)
            isInsideFunction = false
        }
    }
}
