package io.github.ksmirenko.kotlin.featureCalc

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody
import io.github.ksmirenko.kotlin.featureCalc.calculators.MethodFeatureCalculator
import io.github.ksmirenko.kotlin.featureCalc.calculators.FeatureCalculator
import io.github.ksmirenko.kotlin.featureCalc.calculators.PrettyPrinter
import io.github.ksmirenko.kotlin.featureCalc.utils.KotlinFileUtils
import java.io.BufferedWriter
import java.io.File

fun main(args: Array<String>) = mainBody {
    val parsedArgs = ArgParser(args).parseInto(::CommandLineArgs)

    if (parsedArgs.descriptionOnly) {
        MethodFeatureCalculator(null).printFeatureDescriptions()
        return@mainBody
    }

    // Configure calculators based on command line arguments
    val calculators = arrayListOf<FeatureCalculator>()
    if (parsedArgs.shouldPrettyPrint) {
        calculators.add(PrettyPrinter())
    }
    val methodOutputFile = parsedArgs.methodOutputFile
    if (methodOutputFile != null) {
        calculators.add(MethodFeatureCalculator(methodOutputFile))
    }
    if (parsedArgs.shouldWriteCsvHeader) {
        calculators.forEach(FeatureCalculator::writeCsvHeader)
    }

    val errorLogPath = parsedArgs.failLogFile
    val errorLog = if (errorLogPath != null) BufferedWriter(File(errorLogPath).writer()) else null

    var processedFilesCount = 0
    var skippedFilesCount = 0
    for (file in File(parsedArgs.input).walkTopDown()) {
        if (!KotlinFileUtils.isAcceptableKtFile(file)) {
            continue
        }

        // skip files if needed
        if (skippedFilesCount < parsedArgs.skipFiles) {
            skippedFilesCount += 1
            continue
        }
        // stop after Nth file if needed
        @Suppress("ConvertTwoComparisonsToRangeCheck") // for readability
        if (parsedArgs.ktFileLimit > 0 && processedFilesCount >= parsedArgs.ktFileLimit) {
            break
        }

        // process the file
        val path = file.path
        println(path)
        try {
            val psiFile = PsiGenerator.generate(file)
            calculators.forEach { it.calculate(psiFile, path) }
        } catch (e: Exception) {
            println("\tSkipped, could not compile!")
            errorLog?.write(path + "\n")
        }
        processedFilesCount += 1
    }
    println("Done. Processed $processedFilesCount files.")

    calculators.forEach(FeatureCalculator::dispose)
    errorLog?.close()
}

private class CommandLineArgs(parser: ArgParser) {
    val descriptionOnly by parser.flagging("--description", "-d",
            help = "print method feature descriptions into console and exit")
    val shouldPrettyPrint by parser.flagging("-p", "--pretty-print",
            help = "pretty-print PSI of every Kotlin file")
    val input by parser.storing("-i",
            help = "path to input file or folder", argName = "INPUT")
    val methodOutputFile by parser.storing("-m",
            help = "path to output CSV file with method features", argName = "METHOD-OUTPUT")
            .default<String?>(null)
    val failLogFile by parser.storing("--error-log",
            help = "path to output log with not compilable files", argName = "ERROR-LOG")
            .default<String?>(null)
    val shouldWriteCsvHeader by parser.flagging("--header",
            help = "write CSV headers")
    val ktFileLimit by parser.storing("--file-limit",
            help = "stop after N Kotlin files (no limit by default)", argName = "LIMIT") { toInt() }
            .default(-1)
    val skipFiles by parser.storing("--skip-files",
            help = "skip first M Kotlin files (no limit by default)", argName = "SKIP") { toInt() }
            .default(0)
}
