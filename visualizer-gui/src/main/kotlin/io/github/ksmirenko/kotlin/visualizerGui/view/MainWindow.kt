package io.github.ksmirenko.kotlin.visualizerGui.view

import io.github.ksmirenko.kotlin.visualizerGui.model.Model
import io.github.ksmirenko.kotlin.visualizerGui.model.UserResponse
import javafx.scene.layout.VBox
import tornadofx.*

class MainWindow : View("Code anomaly visualizer GUI") {
    override val root = VBox()

    private val selectDatasetButton: SelectDatasetButton by inject()
    private val anomalyCategoryLabel: AnomalyCategoryLabel by inject()
    private val anomalyIdLabel: AnomalyIdLabel by inject()
    private val anomalyContentView: AnomalySourceView by inject()
    private val buttonRow: LabelButtonRow by inject()

    init {
        root.prefWidth = 800.0

        root += selectDatasetButton.root
        root += anomalyCategoryLabel.root
        root += anomalyIdLabel.root
        root += anomalyContentView.root
        root += buttonRow.root

        root.requestFocus()

        Model.openDefaultDataset()
    }
}

class SelectDatasetButton : View() {
    override val root = button("CHANGE DATASET") {
        vboxConstraints {
            marginTopBottom(R.MARGIN_SMALL)
            marginLeftRight(R.MARGIN_SMALL)
        }
        action {
            val repoRootDirectory = chooseDirectory("Select root folder of the dataset")
            if (repoRootDirectory != null) {
                Model.openDataset(repoRootDirectory)
            }
        }
        isFocusTraversable = false
        addClass("button")
    }
}

class AnomalyCategoryLabel : View() {
    override val root = label {
        vboxConstraints {
            marginLeftRight(R.MARGIN_LEFT)
        }
        addClass("card-title")
        bind(Model.categoryProperty)
    }
}

class AnomalyIdLabel : View() {
    override val root = label {
        vboxConstraints {
            marginLeftRight(R.MARGIN_LEFT)
        }
        addClass("card-subtitle")
        bind(Model.idProperty)
    }
}

class LabelButtonRow : View() {
    override val root = hbox {
        vboxConstraints { marginTopBottom(R.MARGIN_SMALL) }
        button("WANT MORE") {
            hboxConstraints { marginLeftRight(R.MARGIN_SMALL) }
            action { Model.processUserResponse(UserResponse.WANT_MORE) }
            isFocusTraversable = false
            addClass("button-raised")
        }
        button("ENOUGH") {
            hboxConstraints { marginLeftRight(R.MARGIN_SMALL) }
            action { Model.processUserResponse(UserResponse.ENOUGH) }
            isFocusTraversable = false
            addClass("button-raised")
        }
        button("USELESS") {
            hboxConstraints { marginLeftRight(R.MARGIN_SMALL) }
            action { Model.processUserResponse(UserResponse.USELESS) }
            isFocusTraversable = false
            addClass("button-raised")
        }
    }
}
