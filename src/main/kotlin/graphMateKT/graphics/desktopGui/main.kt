package graphMateKT.graphics.desktopGui

import graphMateKT.graphics.launchVisualization

/** Launches the GraphMateKT desktop GUI application.
 *
 * A javafx application that showcases some of the algorithms and the graph and grid visualisation capabilities of the GraphMateKT library.*/
fun launchGraphMateKTGUI() {
    launchVisualization(DesktopGUI())
}

internal fun main() {
    launchGraphMateKTGUI()
}
