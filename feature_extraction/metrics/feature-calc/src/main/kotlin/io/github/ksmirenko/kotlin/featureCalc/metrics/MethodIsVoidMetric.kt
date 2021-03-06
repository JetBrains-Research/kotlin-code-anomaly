package io.github.ksmirenko.kotlin.featureCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodIsVoidMetric : Metric(
        csvName = "isVoid",
        description = "Возвращает ли void"
) {
    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        override fun visitElement(element: PsiElement?) {
            if (element !is KtNamedFunction) {
                return
            }

            val featureValue = if (element.hasDeclaredReturnType()) 0 else 1
            val funName = element.fqName.toString()
            appendRecord(funName, featureValue)
        }
    }
}
