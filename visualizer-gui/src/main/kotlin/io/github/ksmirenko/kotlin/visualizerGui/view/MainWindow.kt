package io.github.ksmirenko.kotlin.visualizerGui.view

import io.github.ksmirenko.kotlin.visualizerGui.model.Model
import io.github.ksmirenko.kotlin.visualizerGui.model.UserResponse
import javafx.scene.layout.VBox
import tornadofx.*

class MainWindow : View("Code anomaly visualizer GUI") {
    override val root = VBox()

    //    private val selectDatasetButton: SelectDatasetButton by inject()
    private val anomalyCategoryLabel: AnomalyCategoryLabel by inject()
    private val anomalyIdLabel: AnomalyIdLabel by inject()
    private val anomalyContentView: AnomalySourceView by inject()
    private val buttonRow: LabelButtonRow by inject()

    init {
        root.prefWidth = 800.0

//        root += selectDatasetButton.root
        root += anomalyCategoryLabel.root
        root += anomalyIdLabel.root
        root += anomalyContentView.root
        root += buttonRow.root

        root.requestFocus()

        Model.openDefaultDataset()
    }
}

//class SelectDatasetButton : View() {
//    override val root =
//}

class AnomalyCategoryLabel : View() {
    override val root = borderpane {
        left = label {
            borderpaneConstraints {
                marginTopBottom(R.MARGIN_SMALL)
                marginLeftRight(R.MARGIN_LEFT)
            }
            addClass("card-title")
            bind(Model.categoryProperty)
        }

        right = button("Change dataset") {
            borderpaneConstraints {
                marginTopBottom(R.MARGIN_SMALL)
                marginLeftRight(R.MARGIN_LEFT)
            }
            action {
                val repoRootDirectory = chooseDirectory("Select root folder of the dataset")
                if (repoRootDirectory != null) {
                    Model.openDataset(repoRootDirectory)
                }
            }
            isFocusTraversable = false
            addClass("button-raised")
        }
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
        vboxConstraints {
            marginTopBottom(R.MARGIN_SMALL)
            marginLeft = R.MARGIN_SMALL
        }
        button("Want more") {
            hboxConstraints { marginLeftRight(R.MARGIN_SMALL) }
            action { Model.processUserResponse(UserResponse.WANT_MORE) }
            isFocusTraversable = false
            addClass("button-raised")
        }
        button("Enough, next class") {
            hboxConstraints { marginLeftRight(R.MARGIN_SMALL) }
            action { Model.processUserResponse(UserResponse.ENOUGH) }
            isFocusTraversable = false
            addClass("button-raised")
        }
        button("Useless, next class") {
            hboxConstraints { marginLeftRight(R.MARGIN_SMALL) }
            action { Model.processUserResponse(UserResponse.USELESS) }
            isFocusTraversable = false
            addClass("button-raised")
        }
    }
}
