package io.github.ksmirenko.kotlin.visualizer

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import io.github.ksmirenko.kotlin.metricsCalc.PsiGenerator
import org.apache.commons.csv.CSVFormat
import java.io.File
import java.io.FileReader

fun main(args: Array<String>) = mainBody {
    val argParser = ArgParser(args)
    val parsedArgs = argParser.parseInto(::CommandLineArgs)
    argParser.force()

    val functionFinder = FunctionFinder(shouldPrintToConsole = parsedArgs.isInteractive)
    var counter = 0

    parsedArgs.csvPaths.forEach {
        for (csvFile in File(it).walkTopDown()) {
            if (csvFile.isDirectory || csvFile.extension != "csv") {
                continue
            }

            val reader = FileReader(csvFile)
            val records = CSVFormat.EXCEL.parse(reader)
            for (record in records) {
                val signature = record.get(0)
                println(signature)

                // extract Kotlin file name
                val filepath = signature.substringBefore(".kt:", missingDelimiterValue = "") + ".kt"
                if (filepath == ".kt") {
                    System.err.print("Couldn't resolve file name!")
                    continue
                }

                try {
                    val psiFile = PsiGenerator.generate(File(parsedArgs.repoRoot, filepath))
                    functionFinder.reset(signature, filepath)
                    psiFile.accept(functionFinder)

                    val foundFunction = functionFinder.foundFunction
                    if (foundFunction == null) {
                        System.err.print("Couldn't find function with signature $signature in file $filepath!")
                        continue
                    }

                    val outFileName = if (parsedArgs.isInteractive) {
                        print("Type (a)nomaly/(n)on-anomaly: ")
                        val userAnswer = readLine()
                        when (userAnswer) {
                            "a" -> String.format("confirmed_%03d.kt", counter)
                            "n" -> String.format("false_%03d.kt", counter)
                            else -> String.format("%03d.kt", counter)
                        }
                    } else {
                        String.format("%03d.kt", counter)
                    }
                    File(parsedArgs.outFolder, outFileName).writeText("// $signature\n\n$foundFunction")

                    counter += 1
                } catch (e: Exception) {
                    System.err.print("Couldn't process file $filepath")
                    e.printStackTrace()
                }
            }
            reader.close()
        }
    }
    println("Done. Successfully processed $counter files.")
}


private class CommandLineArgs(parser: ArgParser) {
    val isInteractive by parser.flagging("--interactive", "-i",
            help = "interactive mode: print every potential anomaly; press 'a' for confirmed anomaly, 'n' for not confirmed anomaly")
    val repoRoot by parser.storing("--repo", "-r",
            help = "path to the 'repository' where Kotlin code is located", argName = "REPO-ROOT")
    val outFolder by parser.storing("-o", help = "path to output folder", argName = "OUT-FOLDER")
    val csvPaths by parser.positionalList("CSV", sizeRange = 1..Int.MAX_VALUE,
            help = "CSV files or folders with anomaly reports")
}
