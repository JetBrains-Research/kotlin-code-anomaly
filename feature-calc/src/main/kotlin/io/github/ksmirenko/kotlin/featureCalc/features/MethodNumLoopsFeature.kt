package io.github.ksmirenko.kotlin.featureCalc.features

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.featureCalc.records.FeatureRecord
import org.jetbrains.kotlin.psi.*

class MethodNumLoopsFeature : Feature(
        id = FeatureRecord.Type.MethodNumLoops,
        csvName = "numLoopStatements",
        description = "Number of loop statements"
) {
    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var isInsideFunction = false
        private var loopCount = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtDoWhileExpression -> {
                    loopCount += 1
                    super.visitElement(element)
                }
                is KtForExpression -> {
                    loopCount += 1
                    super.visitElement(element)
                }
                is KtWhileExpression -> {
                    loopCount += 1
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

            loopCount = 0
            super.visitElement(function)

            val funName = function.fqName.toString()
            appendRecord(funName, loopCount)
            isInsideFunction = false
        }
    }
}
