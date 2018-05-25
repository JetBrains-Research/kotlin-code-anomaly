package io.github.ksmirenko.kotlin.featureCalc.metrics

import com.intellij.psi.PsiElementVisitor
import io.github.ksmirenko.kotlin.featureCalc.records.FeatureRecord

abstract class Metric(
        val csvName: String,
        val description: String
) {
    abstract val visitor: PsiElementVisitor

    var lastRecord: FeatureRecord? = null
        private set

    fun appendRecord(record: FeatureRecord) {
        lastRecord = record
    }

    fun appendRecord(entityIdentifier: String, value: Double) {
        appendRecord(FeatureRecord(entityIdentifier, value))
    }

    fun appendRecord(entityIdentifier: String, value: Int) {
        appendRecord(FeatureRecord(entityIdentifier, value))
    }
}
