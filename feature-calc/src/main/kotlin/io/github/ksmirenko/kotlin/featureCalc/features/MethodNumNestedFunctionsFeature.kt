package io.github.ksmirenko.kotlin.featureCalc.features

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.featureCalc.records.FeatureRecord
import org.jetbrains.kotlin.psi.*

class MethodNumNestedFunctionsFeature : Feature(
        id = FeatureRecord.Type.MethodNumNestedFunctions,
        csvName = "numNestedFuns",
        description = "Number of nested functions, including not indirectly nested, but excluding local classes"
) {
    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var funNestingDepth = 0
        private var numNestedFuns = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtNamedFunction -> visitKtFunction(element)
                is KtClassOrObject -> {
                }  // skip nested classes
                else -> super.visitElement(element)
            }
        }

        private fun visitKtFunction(function: KtNamedFunction) {
            if (funNestingDepth == 0) {
                numNestedFuns = 0
            } else {
                numNestedFuns += 1
            }

            funNestingDepth++
            super.visitElement(function)
            funNestingDepth--

            if (funNestingDepth == 0) {
                val funName = function.fqName.toString()
                appendRecord(funName, numNestedFuns)
            }
        }
    }
}
