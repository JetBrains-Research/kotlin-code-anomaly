package io.github.ksmirenko.kotlin.visualizer

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import org.apache.commons.csv.CSVFormat
import java.io.File
import java.io.FileReader

fun main(args: Array<String>) = mainBody {
    val parsedArgs = ArgParser(args).parseInto(::CommandLineArgs)
    parsedArgs.csvPaths.forEach {
        for (csvFile in File(it).walkTopDown()) {
            if (csvFile.isDirectory || csvFile.extension != "csv") {
                continue
            }

            val reader = FileReader(csvFile)
            val records = CSVFormat.EXCEL.parse(reader)
            for (record in records) {
                val signature = record.get(0)
                println("\t$signature")
            }
            reader.close()
        }
    }
    println("Done.")
}


private class CommandLineArgs(parser: ArgParser) {
    val csvPaths by parser.positionalList("CSV", sizeRange = 1..Int.MAX_VALUE,
            help = "CSV files or folders with anomaly reports")
}
