package io.github.ksmirenko.kotlin.visualizerGui.view

import javafx.scene.layout.VBox
import tornadofx.View
import tornadofx.button
import tornadofx.label
import tornadofx.vboxConstraints

class MainWindow : View("Code anomaly visualizer GUI") {
    override val root = VBox()

    private val selectDatasetButton: SelectDatasetButton by inject()
    private val anomalyContentView: AnomalyContentView by inject()

    init {
        root.prefWidth = 400.0
        root.prefHeight = 300.0

        root.add(selectDatasetButton.root)
        root.add(anomalyContentView.root)
    }
}

class SelectDatasetButton : View() {
    override val root = button("Select dataset")
}

class AnomalyContentView : View() {
    override val root = label("Anomaly source code will be here") {
        vboxConstraints {
            marginTopBottom(20.0)
        }
    }
}
