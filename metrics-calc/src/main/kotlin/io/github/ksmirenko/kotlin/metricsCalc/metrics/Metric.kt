package io.github.ksmirenko.kotlin.metricsCalc.metrics

import com.intellij.psi.PsiElementVisitor
import io.github.ksmirenko.kotlin.metricsCalc.records.MetricRecord

abstract class Metric(
        protected val id: MetricRecord.Type,
        val csvName: String,
        protected val description: String
) {
    abstract val visitor: PsiElementVisitor

    private val _records = ArrayList<MetricRecord>()

    val records: List<MetricRecord>
        get() = _records

    var lastRecord: MetricRecord? = null
        private set

    fun appendRecord(record: MetricRecord) {
        synchronized(_records) {
            _records.add(record)
        }
        lastRecord = record
    }

    fun appendRecord(entityIdentifier: String, value: Double) {
        appendRecord(MetricRecord(id, entityIdentifier, value))
    }

    fun appendRecord(entityIdentifier: String, value: Int) {
        appendRecord(MetricRecord(id, entityIdentifier, value))
    }
}
