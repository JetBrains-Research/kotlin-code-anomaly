package io.github.ksmirenko.kotlin.visualizer

import io.github.ksmirenko.kotlin.featureCalc.PsiGenerator
import org.apache.commons.csv.CSVRecord
import java.io.File

class SeekingStrategy(inFolder: String, outFolder: String) : RecordProcessingStrategy(inFolder, outFolder) {
    private val functionFinder = FunctionFinder()

    override fun process(record: CSVRecord): Boolean {
        val id = record.get(0)
        val signature = record.get(1)
        println("\n$id\n$signature")

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

        // write function source code to file
        File(outFolder, "$id.kt").writeText("// $signature\n\n$foundFunction")

        return true
    }
}
