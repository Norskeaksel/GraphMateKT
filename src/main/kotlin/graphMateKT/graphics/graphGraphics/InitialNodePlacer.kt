package graphMateKT.graphics.graphGraphics

import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy
import java.util.*


class InitialNodePlacer : SmartPlacementStrategy {
    override fun <V, E> place(width: Double, height: Double, smartGraphPanel: SmartGraphPanel<V, E>) {
        val rand = Random()

        for (vertex in smartGraphPanel.smartVertices) {
            val x = rand.nextDouble() * 200 + width / 2 - 100
            val y = rand.nextDouble() * 200 + height / 2 - 100
            System.err.println("Placing vertex at ($x, $y)")
            vertex.setPosition(x, y)
        }
    }
}