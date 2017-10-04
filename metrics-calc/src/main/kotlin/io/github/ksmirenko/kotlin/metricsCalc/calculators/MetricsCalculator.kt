package io.github.ksmirenko.kotlin.metricsCalc.calculators

import com.intellij.psi.PsiFile
import java.io.File

abstract class MetricsCalculator(outFileName: String) {
    protected val writer = File(outFileName).writer()

    abstract fun calculate(psiFile: PsiFile)

    fun dispose() {
        writer.close()
    }
}