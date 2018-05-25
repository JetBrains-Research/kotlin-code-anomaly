package io.github.ksmirenko.kotlin.visualizer

import io.github.ksmirenko.kotlin.featureCalc.PsiGenerator
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import java.io.File
import java.io.FileReader

class SeekingStrategy(inFolder: String, outFolder: String, importantFeaturesPath: String?)
    : RecordProcessingStrategy(inFolder, outFolder) {
    private val functionFinder = FunctionFinder()
    private val importantRecords: MutableList<CSVRecord>?
    private val outCsv = CSVFormat.EXCEL.print(File(outFolder, "summary.csv"), Charsets.UTF_8)

    init {
        if (importantFeaturesPath != null) {
            importantRecords = CSVFormat.EXCEL.parse(FileReader(importantFeaturesPath)).records
            importantRecords.retainAll { it[1] != "" }
        } else {
            importantRecords = null
        }
    }

    override fun process(record: CSVRecord): Boolean {
        val id = record[0]
        val signature = record[1]
        println("\n$id\n$signature")

        val featuresString = if (importantRecords != null) {
            val curImportantFeatures = importantRecords.find { it[0] == id }
            if (curImportantFeatures == null) {
                // if we should filter only records with info about important features, and this record doesn't have
                // such info, then skip this record
                println("\tskipped because has no related important feaures!")
                return true
            }
            curImportantFeatures.joinToString(separator = " ", prefix = "\n\n") { it }
        } else {
            ""
        }

        // extract Kotlin file name
        val filepath = signature.substringBefore(".kt:", missingDelimiterValue = "") + ".kt"
        if (filepath == ".kt") {
            System.err.print("Couldn't resolve file name!")
            return false
        }

        try {
            // search the function source code
            val psiFile = PsiGenerator.generate(File(inFolder, filepath))
            functionFinder.reset(signature, filepath)
            psiFile.accept(functionFinder)
        } catch (e: Exception) {
            System.err.println("Couldn't process file $filepath")
            e.printStackTrace()
            return false
        }

        val foundFunction = functionFinder.foundFunction
        if (foundFunction == null) {
            System.err.print("Couldn't find function with signature $signature in file $filepath!")
            return false
        }

        // write output: a file with extracted function and a CSV record
        File(outFolder, "$id.kt").writeText(foundFunction.toString())
        outCsv.printRecord(record)
        outCsv.flush()

        return true
    }

    override fun close() {
        outCsv.close()
    }
}
