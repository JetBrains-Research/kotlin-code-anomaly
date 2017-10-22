package io.github.ksmirenko.kotlin.metricsCalc

import com.intellij.openapi.util.Disposer
import com.intellij.psi.PsiFileFactory
import io.github.ksmirenko.kotlin.metricsCalc.calculators.FileMetricsCalculator
import io.github.ksmirenko.kotlin.metricsCalc.calculators.MethodMetricsCalculator
import io.github.ksmirenko.kotlin.metricsCalc.calculators.MetricsCalculator
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinLanguage

//val inDirectory = "metrics-calc/src/main/kotlin/testSrc"
val inDirectory = "repos"
val fileMetricsOutFile = "data/fileMetrics.csv"
val methodMetricsOutFile = "data/26proj_methods.csv"

fun main(args: Array<String>) {
    val env = prepareEnvironment()

    val calculators = listOf<MetricsCalculator>(MethodMetricsCalculator(methodMetricsOutFile))
    val kotlinFiles = KotlinFileFinder(inDirectory).search()

    try {
        println("Calculating metrics for files:")
        kotlinFiles.forEach {
            println(it.path)
            val psiFile = PsiFileFactory.getInstance(env.project)
                    .createFileFromText(it.name, KotlinLanguage.INSTANCE, it.readText())
            calculators.forEach { it.calculate(psiFile) }
        }
        println("Done.")
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        calculators.forEach(MetricsCalculator::dispose)
    }
}

private fun prepareEnvironment(): KotlinCoreEnvironment {
    val rootDisposable = Disposer.newDisposable()
    val configuration = CompilerConfiguration()
    return KotlinCoreEnvironment
            .createForProduction(rootDisposable, configuration, EnvironmentConfigFiles.JVM_CONFIG_FILES)
}
