package io.github.ksmirenko.kotlin.metricsCalc.calculators

import com.intellij.psi.PsiFile
import java.io.File
import java.io.OutputStream
import java.io.OutputStreamWriter

abstract class MetricsCalculator(outFileName: String?) {
    protected val writer: OutputStreamWriter

    init {
        writer = when {
            outFileName != null -> File(outFileName).writer()
            else -> OutputStreamWriter(NullOutputStream())
        }
    }

    abstract fun writeCsvHeader()

    abstract fun calculate(psiFile: PsiFile, path: String? = null)

    fun dispose() {
        writer.close()
    }

    private class NullOutputStream : OutputStream() {
        override fun write(b: Int) {
        }
    }
}
