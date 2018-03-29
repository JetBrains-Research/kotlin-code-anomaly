package io.github.ksmirenko.kotlin.featureCalc.features

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import io.github.ksmirenko.kotlin.featureCalc.records.FeatureRecord
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodNumAnnotationsFeature : Feature(
        id = FeatureRecord.Type.MethodNumAnnotations,
        csvName = "numAnnotations",
        description = "Number of annotations"
) {
    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        override fun visitElement(element: PsiElement?) {
            if (element !is KtNamedFunction) {
                return
            }

            val featureValue = element.annotationEntries.size
            val funName = element.fqName.toString()
            appendRecord(funName, featureValue)
        }
    }
}
