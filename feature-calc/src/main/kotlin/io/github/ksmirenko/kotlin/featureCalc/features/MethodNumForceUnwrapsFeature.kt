package io.github.ksmirenko.kotlin.featureCalc.features

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.github.ksmirenko.kotlin.featureCalc.records.FeatureRecord
import org.jetbrains.kotlin.psi.*

class MethodNumForceUnwrapsFeature : Feature(
        id = FeatureRecord.Type.MethodNumForceUnwraps,
        csvName = "numForceUnwraps",
        description = "Number of force unwraps via the non-null assertion operator"
) {
    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var isInsideFunction = false
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

            numForceUnwraps = 0
            super.visitElement(function)

            val funName = function.fqName.toString()
            appendRecord(funName, numForceUnwraps)
            isInsideFunction = false
        }
    }
}
