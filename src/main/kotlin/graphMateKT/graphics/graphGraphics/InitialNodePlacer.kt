package graphMateKT.graphics.graphGraphics

import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy
import java.util.*


internal class InitialNodePlacer : SmartPlacementStrategy {
    override fun <V, E> place(width: Double, height: Double, smartGraphPanel: SmartGraphPanel<V, E>) {
        val rand = Random()
        for (vertex in smartGraphPanel.smartVertices) {
            val x = rand.nextDouble() * 300 + width / 2 - 150
            val y = rand.nextDouble() * 300 + height / 2 - 150
            System.err.println("Placing vertex ${vertex.underlyingVertex} at ($x, $y)")
            vertex.setPosition(x, y)
        }
    }
}