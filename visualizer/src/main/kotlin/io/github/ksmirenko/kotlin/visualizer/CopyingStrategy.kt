package io.github.ksmirenko.kotlin.visualizer

import org.apache.commons.csv.CSVRecord
import java.io.File

class CopyingStrategy(inFolder: String, outFolder: String) : RecordProcessingStrategy(inFolder, outFolder) {
    private var nFiles = 0

    override fun process(record: CSVRecord): Boolean {
        val id = record.get(0)
        val signature = record.get(1)
        println("\n$id\n$signature")

        val filename = "$id.kt"
        return try {
            val inFile = File(inFolder, filename)
            val outFile = File(outFolder, filename)
            inFile.copyTo(outFile)
            nFiles++
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun printFooter() {
        println("Done. Copied $nFiles files.")
    }
}
