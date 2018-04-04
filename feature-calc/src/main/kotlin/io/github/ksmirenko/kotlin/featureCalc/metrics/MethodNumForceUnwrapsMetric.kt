package io.github.ksmirenko.kotlin.featureCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.github.ksmirenko.kotlin.featureCalc.records.FeatureRecord
import org.jetbrains.kotlin.psi.*

class MethodNumForceUnwrapsMetric : Metric(
        id = FeatureRecord.Type.MethodNumForceUnwraps,
        csvName = "numForceUnwraps",
        description = "Number of force unwraps via the non-null assertion operator"
) {
    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var methodNestingDepth = 0
        private var numForceUnwraps = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is LeafPsiElement -> {
                    if (element.text == "!!") {
                        numForceUnwraps += 1
                        super.visitElement(element)
                    }
                }

                is KtNamedFunction -> visitOuterFunction(element)
                else -> super.visitElement(element)
            }
        }

        private fun visitOuterFunction(function: KtNamedFunction) {
            if (methodNestingDepth == 0) {
                numForceUnwraps = 0
            }

            methodNestingDepth++
            super.visitElement(function)
            methodNestingDepth--

            if (methodNestingDepth == 0) {
                val funName = function.fqName.toString()
                appendRecord(funName, numForceUnwraps)
            }
        }
    }
}
