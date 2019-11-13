package org.jetbrains.ngramgenerator.structures

class Tree: AbstractNode() {
    override val type: String = ""
    val children: ArrayList<out Tree>? = null
    val chars: String = ""
}