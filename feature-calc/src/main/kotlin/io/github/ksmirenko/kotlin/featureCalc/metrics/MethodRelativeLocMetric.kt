package io.github.ksmirenko.kotlin.featureCalc.metrics

import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiElement
import com.sixrr.stockmetrics.utils.LineUtil
import io.github.ksmirenko.kotlin.featureCalc.records.FeatureRecord
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodRelativeLocMetric : Metric(
        id = FeatureRecord.Type.MethodRelativeLoc,
        csvName = "relativeLoc",
        description = "Relative lines of code"
) {
    override val visitor: Visitor by lazy { Visitor() }

    inner class Visitor : JavaRecursiveElementVisitor() {
        override fun visitElement(element: PsiElement?) {
            if (element !is KtNamedFunction) {
                return
            }

            val funLinesCount = LineUtil.countLines(element)
            // parent is assumed to be class, object or file
            val parentLinesCount = LineUtil.countLines(element.parent)
            val funName = element.fqName.toString()
            appendRecord(funName, 1.0 * funLinesCount / parentLinesCount)
        }
    }
}
