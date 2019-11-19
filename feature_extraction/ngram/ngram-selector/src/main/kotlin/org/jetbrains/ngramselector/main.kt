package org.jetbrains.ngramselector

import com.xenomachina.argparser.ArgParser

fun main(args : Array<String>) {
    val parser = ArgParser(args)
    val vectorsPath by parser.storing("-i", "--input", help="path to folder with files for n-gram selection")
    val vectorsWithSelectedNgramsPath by parser.storing("-o", "--output", help="path to folder, in which will be written files with selected n-grams")
    val allNgramsPath by parser.storing("--all_ngrams_file", help="path to all n-grams map file")

    Runner.run(vectorsPath, vectorsWithSelectedNgramsPath, allNgramsPath)
}