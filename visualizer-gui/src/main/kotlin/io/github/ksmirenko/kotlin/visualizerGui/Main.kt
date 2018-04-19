package io.github.ksmirenko.kotlin.visualizerGui

import io.github.ksmirenko.kotlin.visualizerGui.view.MainWindow
import javafx.application.Application
import tornadofx.App

class VisualizerApp : App(MainWindow::class)

fun main(args: Array<String>) = Application.launch(VisualizerApp::class.java)
