package io.github.ksmirenko.kotlin.metricsCalc

import io.github.ksmirenko.kotlin.metricsCalc.calculators.*
import io.github.ksmirenko.kotlin.metricsCalc.utils.KotlinFileUtils
import java.io.File

val inDirectory = "repos"
val methodMetricsOutFile = "data/feb18_methods.csv"

fun main(args: Array<String>) {
    @Suppress("RemoveExplicitTypeArguments") // for convenience, as the list may be modified by user
    val calculators = listOf<MetricsCalculator>(
//            PrettyPrinter(),
            MethodMetricsCalculator(methodMetricsOutFile)
    )

    calculators.forEach(MetricsCalculator::writeCsvHeader)

    var fileCount = 0L
    println("Calculating metrics for files:")
    File(inDirectory).walkTopDown().forEach {
        if (KotlinFileUtils.isAcceptableKtFile(it)) {
            val path = it.path
            println(path)
            fileCount += 1
            try {
                val psiFile = PsiGenerator.generate(it)
                calculators.forEach { it.calculate(psiFile, path) }
            } catch (e: Exception) {
                println("\tSkipped, could not compile!")
            }
        }
    }
    println("Done.")
    println("Total $fileCount files.")

    calculators.forEach(MetricsCalculator::dispose)
}
