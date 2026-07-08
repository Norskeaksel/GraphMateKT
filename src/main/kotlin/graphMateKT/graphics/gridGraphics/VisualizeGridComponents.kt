package graphMateKT.graphics.gridGraphics

import graphMateKT.GridComponents
import graphMateKT.graphClasses.Grid

/** Visualizes the strongly connected components (SCCs) of a Grid.
 *
 * This function colors the tiles belonging to the same component with the same color.
 *
 * <i>Example usage:</i>
 *
 * ```
 * val lines = listOf("ABC, ABC")
 * val grid = Grid(lines)
 * grid.connectGrid{ t ->
 *     grid.getStraigtNeighbours(t).filter { it.data == t.data }
 * }
 * val scc = grid.stronglyConnectedComponents()
 * scc.visualizeComponents() */
fun GridComponents.visualizeGridComponents() {
    val (width, height) = flatten().fold(0 to 0) { (maxX, maxY), t ->
        (t.x + 1).coerceAtLeast(maxX) to (t.y + 1).coerceAtLeast(maxY)
    }
    val grid = Grid(width, height)
    val (nodeVisitationOrder, nodeDistances) = reversed().flatMapIndexed { i, component ->
        component.map { it to i.toDouble() }
    }.unzip()
    grid.visualizeGrid(currentVisitedNodes = nodeVisitationOrder, nodeDistances = nodeDistances)
}