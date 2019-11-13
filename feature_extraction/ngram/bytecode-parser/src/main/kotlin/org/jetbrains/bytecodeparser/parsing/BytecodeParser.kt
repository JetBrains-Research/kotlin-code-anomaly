package org.jetbrains.bytecodeparser.parsing

import java.io.File
import com.google.gson.Gson
import org.apache.bcel.classfile.ClassParser
import org.apache.bcel.generic.InstructionList

typealias Instructions = MutableMap<String, MutableList<String>>

class BytecodeParser {
    companion object {
        const val JAR_FILE_EXT = "jar"
        const val JSON_FILE_EXT = "json"
    }

    private fun write(file: File, packageName: String, instructions: Instructions) {
        val packageNamePrefix = if (packageName.isEmpty()) "" else "$packageName."
        val outputFile = File("${file.parent}/$packageNamePrefix${file.name}.$JSON_FILE_EXT")

        outputFile.writeText(Gson().toJson(instructions))
    }

    fun getPackageName(file: File): String {
        val classParsed = ClassParser(file.absolutePath).parse()

        return classParsed.packageName
    }

    fun parse(file: File, isPrint: Boolean) {
        val classParsed = ClassParser(file.absolutePath).parse()
        val instructions: Instructions = mutableMapOf()

        classParsed.methods.forEach {
            if (it.code == null) {
                if (isPrint) {
                    println("\"$it\" method skip (no bytecode)")
                }
                return@forEach
            }
            val methodName = it.name
            instructions[methodName] = mutableListOf()
            InstructionList(it.code.code).forEach { instructions[methodName]!!.add(it.instruction.name) }
            if (isPrint) {
                println("\"$it\" method bytecode was written")
            }
        }

        if (isPrint) {
            println("PARSED: $file")
        }
        write(file, classParsed.packageName, instructions)
    }
}