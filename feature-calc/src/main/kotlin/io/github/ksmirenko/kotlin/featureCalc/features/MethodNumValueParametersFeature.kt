package io.github.ksmirenko.kotlin.featureCalc.features

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.featureCalc.records.FeatureRecord
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodNumValueParametersFeature : Feature(
        id = FeatureRecord.Type.MethodNumValueParameters,
        csvName = "numValueParameters",
        description = "Number of value parameters"
) {
    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        override fun visitElement(element: PsiElement?) {
            if (element !is KtNamedFunction) {
                return
            }

            val valueParameterCount = element.valueParameters.size
            val funName = element.fqName.toString()
            appendRecord(funName, valueParameterCount)
        }
    }
}
