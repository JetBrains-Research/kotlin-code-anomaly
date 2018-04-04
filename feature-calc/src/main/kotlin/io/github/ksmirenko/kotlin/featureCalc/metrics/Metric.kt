package io.github.ksmirenko.kotlin.featureCalc.metrics

import com.intellij.psi.PsiElementVisitor
import io.github.ksmirenko.kotlin.featureCalc.records.FeatureRecord

abstract class Metric(
        protected val id: FeatureRecord.Type,
        val csvName: String,
        val description: String
) {
    abstract val visitor: PsiElementVisitor

    private val _records = ArrayList<FeatureRecord>()

    val records: List<FeatureRecord>
        get() = _records

    var lastRecord: FeatureRecord? = null
        private set

    fun appendRecord(record: FeatureRecord) {
        synchronized(_records) {
            _records.add(record)
        }
        lastRecord = record
    }

    fun appendRecord(entityIdentifier: String, value: Double) {
        appendRecord(FeatureRecord(id, entityIdentifier, value))
    }

    fun appendRecord(entityIdentifier: String, value: Int) {
        appendRecord(FeatureRecord(id, entityIdentifier, value))
    }
}
