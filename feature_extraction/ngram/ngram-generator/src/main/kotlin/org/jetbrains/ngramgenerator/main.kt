package org.jetbrains.ngramgenerator

import com.xenomachina.argparser.ArgParser

fun main(args : Array<String>) {
    val parser = ArgParser(args)
    val structuresPath by parser.storing("-i", "--input", help="path to folder with files for n-gram generation")
    val structureVectorsPath by parser.storing("-o", "--output", help="path to folder, in which will be written files with n-grams")
    val structureType by parser.mapping("--tree" to StructureType.TREE, "--list" to StructureType.LIST, help = "structure type (--tree or --list)")

    Runner.run(structureType, structuresPath, structureVectorsPath)
}