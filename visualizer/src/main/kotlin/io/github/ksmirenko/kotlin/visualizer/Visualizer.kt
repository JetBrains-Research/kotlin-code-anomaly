package io.github.ksmirenko.kotlin.visualizer

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
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
    val outCsv = when (parsedArgs.outCsvPath) {
        null -> null
        else -> CSVFormat.EXCEL.print(File(parsedArgs.outCsvPath), Charsets.UTF_8)
    }
    File(parsedArgs.outFolder).mkdirs()

    var counter = 0
    var confirmedCounter = 0

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
                    // search the function source code
                    val psiFile = PsiGenerator.generate(File(parsedArgs.repoRoot, filepath))
                    functionFinder.reset(signature, filepath)
                    psiFile.accept(functionFinder)

                    val foundFunction = functionFinder.foundFunction
                    if (foundFunction == null) {
                        System.err.print("Couldn't find function with signature $signature in file $filepath!")
                        continue
                    }

                    // write function source code to file
                    val outFileName = if (parsedArgs.isInteractive) {
                        // ask user whether it is a true anomaly
                        print("Type 'a' for true anomaly or 'f' for false anomaly: ")
                        val userAnswer = readLine()
                        when (userAnswer) {
                            "a" -> {
                                // additionally write a CSV entry
                                outCsv?.printRecord(record)
                                outCsv?.flush()
                                confirmedCounter += 1
                                String.format("confirmed_%03d.kt", counter)
                            }
                            "f" -> String.format("false_%03d.kt", counter)
                            else -> String.format("%03d.kt", counter)
                        }
                    } else {
                        String.format("%03d.kt", counter)
                    }
                    println()
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
    outCsv?.close()
    println("Done. Successfully processed $counter files.")
    if (parsedArgs.isInteractive) {
        println("$confirmedCounter/$counter marked as true anomalies.")
    }
}

private class CommandLineArgs(parser: ArgParser) {
    val isInteractive by parser.flagging("--interactive", "-i",
            help = "interactive mode: print every potential anomaly; press 'a' for confirmed anomaly, 'n' for not confirmed anomaly")
    val repoRoot by parser.storing("--repo", "-r",
            help = "path to the 'repository' where Kotlin code is located", argName = "REPO-ROOT")
    val outFolder by parser.storing("-o", help = "path to output folder", argName = "OUT-FOLDER")
    val outCsvPath by parser.storing("--out-csv", argName = "OUT-PATH",
            help = "path to output CSV file for entries associated with true anomalies")
            .default<String?>(null)
    val csvPaths by parser.positionalList("CSV", sizeRange = 1..Int.MAX_VALUE,
            help = "CSV files or folders with anomaly reports")
}
