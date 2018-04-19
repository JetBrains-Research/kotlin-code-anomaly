package io.github.ksmirenko.kotlin.visualizerGui.model

import javafx.beans.property.SimpleStringProperty
import java.io.File

object Model {
    var anomalySequence: Sequence<Anomaly>? = null

    val categoryProperty = SimpleStringProperty()
    val idProperty = SimpleStringProperty()
    val sourceCodeProperty = SimpleStringProperty()

    fun openDataset(repoRootDirectory: File) {
        println(repoRootDirectory.absolutePath)
    }

    fun processUserResponse(response: UserResponse) {
        println("User said: $response")
        categoryProperty.set("Category: ${dummyAnomaly.categoryName}")
        idProperty.set("#${dummyAnomaly.id}")
        sourceCodeProperty.set(dummyAnomaly.source)
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
