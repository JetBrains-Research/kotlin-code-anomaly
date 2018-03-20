package io.github.ksmirenko.kotlin.featureCalc.features

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.featureCalc.records.FeatureRecord
import org.jetbrains.kotlin.psi.*

open class MethodSpecificNodeCountFeature<T>(
        id: FeatureRecord.Type,
        csvName: String,
        description: String,
        private val desiredNodeType: Class<T>
) : Feature(id, csvName, description) {

    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var methodNestingDepth = 0
        private var desiredNodeCount = 0

        override fun visitElement(element: PsiElement?) {
            when {
                element is KtNamedFunction -> visitKtFunction(element)
                desiredNodeType.isInstance(element) -> {
                    desiredNodeCount++
                    super.visitElement(element)
                }
                else -> super.visitElement(element)
            }
        }

        private fun visitKtFunction(function: KtNamedFunction) {
            if (methodNestingDepth == 0) {
                desiredNodeCount = 0
            }

            methodNestingDepth++
            if (desiredNodeType.isInstance(function)) {
                // we also take KtNamedFunction that satisfy the condition into account
                desiredNodeCount++
            }

            super.visitElement(function)
            methodNestingDepth--

            if (methodNestingDepth == 0) {
                val funName = function.fqName.toString()
                appendRecord(funName, desiredNodeCount)
            }
        }
    }
}
