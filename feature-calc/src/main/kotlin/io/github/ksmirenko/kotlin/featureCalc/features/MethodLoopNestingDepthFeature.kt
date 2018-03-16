package io.github.ksmirenko.kotlin.featureCalc.features

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.featureCalc.records.FeatureRecord
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtLoopExpression
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodLoopNestingDepthFeature : Feature(
        id = FeatureRecord.Type.MethodLoopNestingDepth,
        csvName = "loopNestingDepth",
        description = "Loop nesting depth"
) {

    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var isInsideFunction = false
        private var curLoopDepth = 0
        private var maxLoopDepth = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtLoopExpression -> visitKtLoopExpression(element)

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

            curLoopDepth = 0
            maxLoopDepth = 0
            super.visitElement(function)

            val funName = function.fqName.toString()
            appendRecord(funName, maxLoopDepth)
            isInsideFunction = false
        }

        private fun visitKtLoopExpression(loopExpression: KtLoopExpression) {
            curLoopDepth += 1
            if (curLoopDepth > maxLoopDepth) {
                maxLoopDepth = curLoopDepth
            }
            super.visitElement(loopExpression)
            curLoopDepth -= 1
        }
    }
}
