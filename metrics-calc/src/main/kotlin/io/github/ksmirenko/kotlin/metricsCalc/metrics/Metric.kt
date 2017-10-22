package io.github.ksmirenko.kotlin.metricsCalc.metrics

import com.intellij.psi.PsiElementVisitor
import io.github.ksmirenko.kotlin.metricsCalc.records.MetricRecord

abstract class Metric {
    private val _records = ArrayList<MetricRecord>()

    abstract val headerName: String
    abstract val description: String
    abstract val visitor: PsiElementVisitor

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
}