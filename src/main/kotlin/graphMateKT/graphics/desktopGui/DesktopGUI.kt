package graphMateKT.graphics.desktopGui

import graphMateKT.graphClasses.Graph
import graphMateKT.graphics.graphGraphics.visualizeGraph
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.layout.VBox
import javafx.stage.Stage

class DesktopGUI : Application() {
    companion object {}

    override fun start(stage: Stage) {
        val inputField = TextArea()
        val vizualizeButton = Button("Vizualize")

        val layout = VBox(10.0, inputField, vizualizeButton)
        val scene = Scene(layout, 300.0, 400.0)
        stage.title = "My GUI"
        stage.scene = scene
        stage.show()

        vizualizeButton.setOnAction { event ->
            val graph = Graph()
            println("Reading graph input")
            val lines = inputField.text.lines()
            lines.forEach { line ->
                val uvw = line.split(" ")
                when (uvw.size) {
                    1 -> graph.addNode(uvw[0]).also { println("Adding node: ${uvw[0]}") }
                    2 -> graph.addEdge(uvw[0], uvw[1]).also { println("Adding edge from ${uvw[0]} to ${uvw[1]}") }
                    3 -> graph.addEdge(uvw[0], uvw[1], uvw[2].toDouble())
                        .also { println("Adding edge from ${uvw[0]} to ${uvw[1]} with weight ${uvw[2]}") }
                }
            }
            println("Graph building complete")
            graph.visualizeGraph()
        }
    }
}
