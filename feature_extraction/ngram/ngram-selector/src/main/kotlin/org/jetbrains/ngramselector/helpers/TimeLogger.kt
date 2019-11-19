package org.jetbrains.ngramselector.helpers

import java.util.*
import java.util.concurrent.TimeUnit


class TimeLogger(private val task_name: String) {
    private val startTime = Date().time

    init {
        println("$task_name started")
    }

    fun finish(fullFinish: Boolean = false) {
        val time = Date().time - startTime
        val seconds = TimeUnit.MILLISECONDS.toSeconds(time)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(time)
        val hours = TimeUnit.MILLISECONDS.toHours(time)

        if (fullFinish) {
            println("--------------------------------")
        }
        println("$task_name finished. Time: $hours:$minutes:$seconds")
        if (fullFinish) {
            println("--------------------------------")
        }
    }
}