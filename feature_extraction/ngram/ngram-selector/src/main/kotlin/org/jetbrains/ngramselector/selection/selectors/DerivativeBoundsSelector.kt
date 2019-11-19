package org.jetbrains.ngramselector.selection.selectors

import org.jetbrains.ngramselector.helpers.TimeLogger
import org.jetbrains.ngramselector.selection.GramStatisticList

class DerivativeBoundsSelector(private val point: Double, private val deviation: Double): Selector {
    override fun select(ngrams: GramStatisticList): GramStatisticList {
        var ngramFrequencyPrev: Int? = null
        val ngramsSelected: GramStatisticList = mutableListOf()
        val timeLogger = TimeLogger(task_name = "N-grams selection via DerivativeBoundsSelector (point: $point, deviation: $deviation)")

        ngrams.forEach {
            if (ngramFrequencyPrev != null) {
                val pairNgramsDerivative = ngramFrequencyPrev as Int - it.second
                if (pairNgramsDerivative <= point || pairNgramsDerivative >= point + deviation) {
                    ngramsSelected.add(it)
                }
            }
            ngramFrequencyPrev = it.second
        }

        timeLogger.finish()
        println("${ngramsSelected.size} out of ${ngrams.size} n-grams selected (${ngrams.size - ngramsSelected.size} excluded)")
        println("--------------------------------")

        return ngramsSelected
    }
}