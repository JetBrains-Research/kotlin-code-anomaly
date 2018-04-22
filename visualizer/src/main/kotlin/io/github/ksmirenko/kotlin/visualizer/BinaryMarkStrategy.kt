package io.github.ksmirenko.kotlin.visualizer

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import java.io.File
import java.io.FileNotFoundException

class BinaryMarkStrategy(inFolder: String, outFolder: String) : RecordProcessingStrategy(inFolder, outFolder) {
    private val outCsv = CSVFormat.EXCEL.print(File(outFolder, "bin_mark_full.csv"), Charsets.UTF_8)
    private val outCsvUseful = CSVFormat.EXCEL.print(File(outFolder, "bin_mark_confirmed.csv"), Charsets.UTF_8)
    private var usefulCount = 0

    override fun process(record: CSVRecord): Boolean {
        val id = record.get(0)
        val signature = record.get(1)
        println("\n\n\n$id\n$signature\n\n\n")

        val filename = "$id.kt"
        val funText: String
        try {
            funText = File(inFolder, filename).readText()
        } catch (e: FileNotFoundException) {
            println("File $filename not found!")
            return false
        }
        println(funText)

        var userAnswer: String?
        do {
            print("Type 'a' for useful or 'f' for useless: ")
            userAnswer = readLine()
        } while (userAnswer != "a" && userAnswer != "f")

        val label = when (userAnswer) {
            "a" -> {
                outCsvUseful.printRecord(record)
                outCsvUseful.flush()
                usefulCount++
                Label.USEFUL
            }
            "f" -> Label.USELESS
            else -> throw IllegalStateException("Unknown label: $userAnswer")
        }
        val recordWithLabel = record.toMutableList().apply { add(label.value.toString()) }
        outCsv.printRecord(recordWithLabel)
        outCsv.flush()

        return true
    }

    override fun close() {
        outCsv.close()
        outCsvUseful.close()
    }

    override fun printFooter() {
        println("$usefulCount potential anomalies marked as useful.")
    }
}
