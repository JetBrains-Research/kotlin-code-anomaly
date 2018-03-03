package io.github.ksmirenko.kotlin.metricsCalc

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody
import io.github.ksmirenko.kotlin.metricsCalc.calculators.MethodMetricsCalculator
import io.github.ksmirenko.kotlin.metricsCalc.calculators.MetricsCalculator
import io.github.ksmirenko.kotlin.metricsCalc.calculators.PrettyPrinter
import io.github.ksmirenko.kotlin.metricsCalc.utils.KotlinFileUtils
import java.io.File

fun main(args: Array<String>) = mainBody {
    val parsedArgs = ArgParser(args).parseInto(::CommandLineArgs)

    // Configure calculators based on command line arguments
    val calculators = arrayListOf<MetricsCalculator>()
    if (parsedArgs.shouldPrettyPrint) {
        calculators.add(PrettyPrinter())
    }
    val methodOutputFile = parsedArgs.methodOutputFile
    if (methodOutputFile != null) {
        calculators.add(MethodMetricsCalculator(methodOutputFile))
    }
    calculators.forEach(MetricsCalculator::writeCsvHeader)

    var fileCount = 0L
    File(parsedArgs.input).walkTopDown().forEach {
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
    println("Done. Processed $fileCount files.")

    calculators.forEach(MetricsCalculator::dispose)
}

private class CommandLineArgs(parser: ArgParser) {
    val shouldPrettyPrint by parser.flagging("-p", "--pretty-print",
            help = "pretty-print PSI of every Kotlin file")
    val input by parser.storing("-i",
            help = "path to input file or folder")
    val methodOutputFile by parser.storing("-m", help = "path to output CSV file with method metrics")
            .default<String?>(null)
}
