package io.github.ksmirenko.kotlin.visualizerGui.view

import io.github.ksmirenko.kotlin.visualizerGui.model.Model
import tornadofx.View
import tornadofx.onChange
import tornadofx.vboxConstraints
import tornadofx.webview

class AnomalySourceView : View() {
    override val root = webview {
        vboxConstraints {
            marginTopBottom(R.MARGIN_SMALL)
            marginLeftRight(R.MARGIN_SMALL)
        }
    }

    init {
        Model.sourceCodeProperty.onChange { newSourceCode ->
            if (newSourceCode != null) {
                root.engine.loadContent(buildAnomalyHtml(newSourceCode))
            } else {
                root.engine.loadContent("")
            }
        }

        root.engine.reload()
    }

    companion object {
        private fun resourceLink(path: String) = "${AnomalySourceView::class.java.getResource(path)}"

        private fun buildAnomalyHtml(sourceCode: String) = """
<html>
    <head>
        <link rel="stylesheet" href="${AnomalySourceView.resourceLink("/css/highlight/default.css")}">
        <script src="${AnomalySourceView.resourceLink("/js/highlight.pack.js")}"></script>
        <script>hljs.initHighlightingOnLoad();</script>
    </head>
    <body>
        <pre><code class="kotlin">$sourceCode</code></pre>
    </body>
</html>
"""
    }
}
