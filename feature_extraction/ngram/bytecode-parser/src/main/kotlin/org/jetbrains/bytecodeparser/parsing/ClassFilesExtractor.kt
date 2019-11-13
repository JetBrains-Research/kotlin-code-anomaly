package org.jetbrains.bytecodeparser.parsing

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipFile

class ClassFilesExtractor(private val jarsDirectory: String) {
    private val buf = ByteArray(1024)
    private val classesDir = "classes"

    private fun copyFromJar(path: File, zipFile: ZipFile, zipEntry: ZipEntry) {
        File(path.parent).mkdirs()
        val fileOutputStream = FileOutputStream(path)
        val fileInputStream: InputStream = zipFile.getInputStream(zipEntry)
        var n: Int = fileInputStream.read(buf, 0, 1024)
        while (n > -1) {
            fileOutputStream.write(buf, 0, n)
            n = fileInputStream.read(buf, 0, 1024)
        }
        fileInputStream.close()
        fileOutputStream.close()
    }

    fun extract(file: File, username: String, repo: String): MutableList<File>? {
        try {
            val zipArchive = ZipFile(file)
            val pathFolder = "$jarsDirectory/$username/$repo/$classesDir"
            val classFiles: MutableList<File> = mutableListOf()

            zipArchive.use { zipFile ->
                val zipEntries = zipFile.entries()
                while (zipEntries.hasMoreElements()) {
                    val zipEntry = zipEntries.nextElement()
                    val path = "$pathFolder/${zipEntry.name}"
                    val currentFile = File(path)

                    if (currentFile.extension == "class") {
                        copyFromJar(currentFile, zipArchive, zipEntry)
                        classFiles.add(currentFile)
                    }
                }
            }

            zipArchive.close()

            return classFiles
        } catch (e: ZipException) {
            println("ERROR: $e")
            return null
        }
    }
}