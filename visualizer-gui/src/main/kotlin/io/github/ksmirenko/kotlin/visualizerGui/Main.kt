package io.github.ksmirenko.kotlin.visualizerGui

import io.github.ksmirenko.kotlin.visualizerGui.view.MainWindow
import javafx.application.Application
import tornadofx.App
import tornadofx.importStylesheet

class VisualizerApp : App(MainWindow::class) {
    init {
        importStylesheet("/css/material-fx-v0_3.css")
        importStylesheet("/css/materialfx-toggleswitch.css")
    }
}

fun main(args: Array<String>) = Application.launch(VisualizerApp::class.java)
