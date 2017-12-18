package io.github.ksmirenko.kotlin.metricsCalc

import io.github.ksmirenko.kotlin.metricsCalc.calculators.*

//val inDirectory = "metrics-calc/src/main/kotlin/testSrc"
val inDirectory = "repos"
val fileMetricsOutFile = "data/files.csv"
val methodMetricsOutFile = "data/top1k_methods.csv"

fun main(args: Array<String>) {
    @Suppress("RemoveExplicitTypeArguments") // for convenience, as the list may be modified by user
    val calculators = listOf<MetricsCalculator>(
//            PrettyPrinter(),
            MethodMetricsCalculator(methodMetricsOutFile)
    )
    val kotlinFiles = KotlinFileFinder(inDirectory).search()

    calculators.forEach(MetricsCalculator::writeCsvHeader)

    println("Calculating metrics for files:")
    kotlinFiles.forEach {
        val path = it.path
        println(path)
        try {
            val psiFile = PsiGenerator.generate(it)
            calculators.forEach { it.calculate(psiFile, path) }
        } catch (e: Exception) {
            println("\tSkipped, could not compile!")
        }
    }
    println("Done.")
    calculators.forEach(MetricsCalculator::dispose)
}
