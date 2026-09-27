package graphMateKT.graphics.desktopGui

import graphMateKT.Tile
import graphMateKT.graphClasses.Grid
import graphMateKT.graphics.FxLauncher
import graphMateKT.graphics.gridGraphics.visualizeGrid

/** Launches the GraphMateKT desktop GUI application.
 *
 * A javafx application that showcases some of the algorithms and the graph and grid visualization capabilities of the GraphMateKT library.*/
fun launchGraphMateKTGUI() {
    FxLauncher.launch(DesktopGUI())
}

private fun main() {
    launchGraphMateKTGUI()
}
