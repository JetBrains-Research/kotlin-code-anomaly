package io.github.ksmirenko.kotlin.featureCalc.features

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.featureCalc.records.FeatureRecord
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtWhenEntry
import org.jetbrains.kotlin.psi.KtWhenExpression
import java.util.*
import kotlin.math.max

class MethodMaxNumWhenEntriesFeature : Feature(
        id = FeatureRecord.Type.MethodMaxNumWhenEntries,
        csvName = "maxNumWhenEntries",
        description = "Max number of when-entries in a single when expression"
) {
    override val visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var methodNestingDepth = 0
        private var maxNumWhens = 0
        private var counterStack = Stack<Int>()
        private var counter = 0

        override fun visitElement(element: PsiElement?) {
            when (element) {
                is KtWhenExpression -> {
                    counterStack.push(counter)
                    counter = 0
                    super.visitElement(element)
                    maxNumWhens = max(maxNumWhens, counter)
                    counter = counterStack.pop()
                }
                is KtWhenEntry -> {
                    counter += 1
                    super.visitElement(element)
                }

                is KtNamedFunction -> visitOuterFunction(element)
                else -> super.visitElement(element)
            }
        }

        private fun visitOuterFunction(function: KtNamedFunction) {
            if (methodNestingDepth == 0) {
                maxNumWhens = 0
            }

            methodNestingDepth++
            super.visitElement(function)
            methodNestingDepth--

            if (methodNestingDepth == 0) {
                val funName = function.fqName.toString()
                appendRecord(funName, maxNumWhens)
            }
        }
    }
}
