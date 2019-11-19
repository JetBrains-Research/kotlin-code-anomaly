package org.jetbrains.ngramselector

import com.fasterxml.jackson.core.type.TypeReference
import org.jetbrains.ngramselector.helpers.TimeLogger
import org.jetbrains.ngramselector.io.FileWriter
import org.jetbrains.ngramselector.io.JsonFilesReader
import org.jetbrains.ngramselector.selection.GramsSet
import org.jetbrains.ngramselector.selection.GramsStatistic
import org.jetbrains.ngramselector.selection.NgramSelector
import org.jetbrains.ngramselector.selection.selectors.DerivativeBoundsSelector
import org.jetbrains.ngramselector.selection.selectors.EndsSelectorSide
import org.jetbrains.ngramselector.selection.selectors.EndsSelectorTypes
import org.jetbrains.ngramselector.selection.selectors.EndsSelectors
import java.io.File

object Runner {
    private const val NGRAMS_SELECTED_PATH = "./selected_ngrams.json"

    fun ngramSelect(allNgramsPath: String): GramsSet {
        val timeLogger = TimeLogger(task_name = "N-grams selection")

        val gramsStatisticReference = object: TypeReference<GramsStatistic>() {}

        val allNgrams = JsonFilesReader.readFile<GramsStatistic>(File(allNgramsPath), gramsStatisticReference)
        val allNgramsList = NgramSelector.statisticToSortedList(allNgrams)

        val headSelector = EndsSelectors(side = EndsSelectorSide.HEAD, type = EndsSelectorTypes.VALUE, bound = 100000)
        val tailSelector = EndsSelectors(side = EndsSelectorSide.TAIL, type = EndsSelectorTypes.VALUE, bound = 250)
        val derivativeBoundsSelector = DerivativeBoundsSelector(point = Math.tan(Math.PI / 4) - 0.1, deviation = 2.0)

        val ngramsSelected = NgramSelector.run(allNgramsList, listOf(derivativeBoundsSelector, headSelector, tailSelector))

        FileWriter.write(this.NGRAMS_SELECTED_PATH, ngramsSelected)

        timeLogger.finish(fullFinish = true)
        println("${ngramsSelected.size} out of ${allNgramsList.size} n-grams selected (${allNgramsList.size - ngramsSelected.size} excluded)")

        return ngramsSelected
    }

    fun ngramSelectByFiles(vectorsPath: String, vectorsWithSelectedNgramsPath: String, ngramsSelected: GramsSet) {
        val timeLogger = TimeLogger(task_name = "N-grams by files selection")
        val cstNodeReference = object: TypeReference<GramsStatistic>() {}
        var meanSelected = 0
        var meanPercentSelected = 0
        var filesCounter = 0

        JsonFilesReader<GramsStatistic>(vectorsPath, "json", cstNodeReference).run(true) { content: GramsStatistic, file: File ->
            val ngramsInCurrentFileSelected: GramsStatistic = mutableMapOf()

            content.map {
                if (ngramsSelected.contains(it.key)) {
                    ngramsInCurrentFileSelected[it.key] = it.value
                }
            }

            FileWriter.write(file, vectorsPath, vectorsWithSelectedNgramsPath, ngramsInCurrentFileSelected)

            println("$file: ${ngramsInCurrentFileSelected.size} out of ${content.size} n-grams selected (${content.size - ngramsInCurrentFileSelected.size} excluded)")
            meanSelected += ngramsInCurrentFileSelected.size
            if (content.isNotEmpty()) {
                meanPercentSelected += ngramsInCurrentFileSelected.size / content.size
            }
            filesCounter++
        }

        val meanAllSelected = meanSelected / filesCounter
        val meanAllPercentSelected = meanPercentSelected / filesCounter

        timeLogger.finish(fullFinish = true)
        println("$filesCounter processed, mean n-grams excluded — $meanAllSelected ($meanAllPercentSelected%)")
    }

    fun run(vectorsPath: String, vectorsWithSelectedNgramsPath: String, allNgramsPath: String) {
        val ngramsSelected = this.ngramSelect(allNgramsPath)

        this.ngramSelectByFiles(vectorsPath, vectorsWithSelectedNgramsPath, ngramsSelected)
    }
}