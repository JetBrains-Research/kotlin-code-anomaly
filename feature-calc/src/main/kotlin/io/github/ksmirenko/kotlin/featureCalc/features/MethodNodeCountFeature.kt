package io.github.ksmirenko.kotlin.featureCalc.features

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.featureCalc.records.FeatureRecord
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodNodeCountFeature : Feature(
        id = FeatureRecord.Type.MethodASTNodeCount,
        csvName = "nodeCount",
        description = "AST node count"
) {

    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        private var methodNestingDepth = 0
        private var nodeCount = 0

        override fun visitElement(element: PsiElement?) {
            nodeCount++
            when (element) {
                is KtNamedFunction -> visitKtFunction(element)
                else -> super.visitElement(element)
            }
        }

        private fun visitKtFunction(function: KtNamedFunction) {
            if (methodNestingDepth == 0) {
                nodeCount = 0
            }

            methodNestingDepth++
            super.visitElement(function)
            methodNestingDepth--

            if (methodNestingDepth == 0) {
                val funName = function.fqName.toString()
                appendRecord(funName, nodeCount)
            }
        }
    }
}
