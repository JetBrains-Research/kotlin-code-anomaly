package io.github.ksmirenko.kotlin.featureCalc.records

data class FeatureRecord(
        val entityIdentifier: String,
        val value: Double
) {
    constructor(entityIdentifier: String, value: Int) : this(entityIdentifier, value.toDouble())

    fun serialize(breakLine: Boolean = false): String {
        return "$entityIdentifier,$value${if (breakLine) "\n" else ""}"
    }

    companion object {
        fun deserialize(string: String): FeatureRecord? {
            val splitString = string.split(',', limit = 2)
            if (splitString.size < 2) {
                return null
            }
            val entityId = splitString[0]
            val value = splitString[1].toDoubleOrNull() ?: return null
            return FeatureRecord(entityId, value)
        }
    }
}
