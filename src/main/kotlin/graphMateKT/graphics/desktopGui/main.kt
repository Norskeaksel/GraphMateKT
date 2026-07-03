package graphMateKT.graphics.desktopGui

import graphMateKT.graphics.FxLauncher

/** Launches the GraphMateKT desktop GUI application.
 *
 * A javafx application that showcases some of the algorithms and the graph and grid visualisation capabilities of the GraphMateKT library.*/
fun launchGraphMateKTGUI() {
    FxLauncher.launch(DesktopGUI())
}

internal fun main() {
    launchGraphMateKTGUI()
}
