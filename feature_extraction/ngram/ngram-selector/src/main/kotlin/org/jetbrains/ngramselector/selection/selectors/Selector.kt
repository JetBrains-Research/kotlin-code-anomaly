package org.jetbrains.ngramselector.selection.selectors

import org.jetbrains.ngramselector.selection.GramStatisticList

interface Selector {
    fun select(ngrams: GramStatisticList): GramStatisticList
}