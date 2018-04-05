package io.github.ksmirenko.kotlin.featureCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodIsSuspendMetric : Metric(
        csvName = "isSuspend",
        description = "Whether the function has suspend modifier"
) {
    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        override fun visitElement(element: PsiElement?) {
            if (element !is KtNamedFunction) {
                return
            }

            val featureValue = if (element.hasModifier(KtTokens.SUSPEND_KEYWORD)) 1 else 0
            val funName = element.fqName.toString()
            appendRecord(funName, featureValue)
        }
    }
}
