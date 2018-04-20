package io.github.ksmirenko.kotlin.visualizerGui.model

import javafx.beans.property.SimpleStringProperty
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import tornadofx.information
import tornadofx.warning
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.experimental.buildSequence

object Model {
    val categoryProperty = SimpleStringProperty()
    val idProperty = SimpleStringProperty()
    val sourceCodeProperty = SimpleStringProperty()

    private var anomalyIterator: Iterator<Anomaly>? = null
    private var curAnomaly: Anomaly? = null
    private var outFileName: String? = null
    private var outCsv: CSVPrinter? = null

    fun openDataset(repoRoot: File) {
        val dirs = repoRoot.listFiles() ?: return

        val anomalySequence = buildSequence {
            for (dir in dirs) {
                if (!dir.isDirectory) {
                    continue
                }

                val categoryName = dir.name
                for (file in dir.listFiles()) {
                    if (file.extension != "kt") {
                        continue
                    }

                    val source = file.readText()
                    yield(Anomaly(categoryName, file.nameWithoutExtension, source))
                }
            }
        }

        val iterator = anomalySequence.iterator()
        anomalyIterator = iterator
        if (!iterator.hasNext()) {
            warning("Error",
                    "No anomalies found or dataset format was incorrect. Please select another dataset.")
            return
        }
        loadAnomaly(iterator.next())

        outCsv?.close()
        val timestamp = SimpleDateFormat("MM-dd-HH-mm").format(Date())
        outFileName = "marked_$timestamp.csv"
        outCsv = CSVFormat.EXCEL.print(File(outFileName), Charsets.UTF_8)
    }

    fun processUserResponse(response: UserResponse) {
        if (anomalyIterator == null) {
            // no data loaded
            return
        }
        assert(curAnomaly != null)

        storeMark(curAnomaly!!, response)

        val markForRestOfCategory = when (response) {
            UserResponse.WANT_MORE -> null
            else -> response
        }
        nextAnomaly(markForRestOfCategory)
    }

    private fun loadAnomaly(anomaly: Anomaly) {
        curAnomaly = anomaly
        categoryProperty.set("Category: ${anomaly.categoryName}")
        idProperty.set("#${anomaly.id}")
        sourceCodeProperty.set(anomaly.source)
    }

    private fun nextAnomaly(markForRestOfCategory: UserResponse? = null) {
        assert(anomalyIterator != null)
        assert(curAnomaly != null)

        val anomalyIteratorSnapshot = anomalyIterator!!
        if (!anomalyIteratorSnapshot.hasNext()) {
            finish()
            return
        }

        val curCategory: String? = curAnomaly!!.categoryName
        var next = anomalyIteratorSnapshot.next()
        if (markForRestOfCategory == null) {
            loadAnomaly(next)
        } else {
            while (curCategory == next.categoryName) {
                storeMark(next, markForRestOfCategory)
                if (!anomalyIteratorSnapshot.hasNext()) {
                    finish()
                    return
                }
                next = anomalyIteratorSnapshot.next()
            }
            loadAnomaly(next)
        }
    }

    private fun storeMark(anomaly: Anomaly, mark: UserResponse) {
        outCsv!!.printRecord(anomaly.categoryName, anomaly.id, mark.csvValue)
    }

    private fun finish() {
        categoryProperty.set("")
        idProperty.set("")
        sourceCodeProperty.set("")
        outCsv!!.close()

        information("Finished dataset!",
                "You are done! Please send $outFileName to the code anomaly researchers.")

        anomalyIterator = null
        curAnomaly = null
        outFileName = null
        outCsv = null
    }

    private val dummyAnomaly = Anomaly(
            "Many loops",
            "123",
            """fun foo() {
                |   for (i in 1..5) {
                |       for (j in 1..5) {
                |           println("Hello there!")
                |       }
                |   }
                |}
            """.trimMargin()
    )
}

data class Anomaly(
        val categoryName: String,
        val id: String,
        val source: String
)

enum class UserResponse(val csvValue: Int) {
    WANT_MORE(3),
    ENOUGH(4),
    USELESS(5)
}
