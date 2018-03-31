package io.github.ksmirenko.kotlin.visualizer

import org.apache.commons.csv.CSVRecord
import java.io.Closeable

abstract class RecordProcessingStrategy(
        protected val inFolder: String,
        protected val outFolder: String
) : Closeable {
    abstract fun process(record: CSVRecord): Boolean

    override fun close() {
    }

    open fun printFooter() {
    }
}
