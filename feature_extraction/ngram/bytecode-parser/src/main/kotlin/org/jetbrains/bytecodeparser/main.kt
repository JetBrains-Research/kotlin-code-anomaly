package org.jetbrains.bytecodeparser

import com.xenomachina.argparser.ArgParser

fun main(args : Array<String>) {
    val parser = ArgParser(args)
    val directory by parser.storing("-i", "--input", help="path to folder with jar (if stage is parsing) or class (if stage is grouping) files")
    val stage by parser.mapping("--parsing" to Stage.PARSING, "--grouping" to Stage.GROUPING, help = "stage (--parsing or --grouping)")

    Runner.run(stage, directory)
}