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
 * val lines = listOf("ABC", "ABC")
 * val grid = Grid(lines)
 * grid.connectWithRule { t ->
 *     grid.getStraightNeighbours(t).filter { it.data == t.data }
 * }
 * val scc = grid.stronglyConnectedComponents()
 * scc.visualizeGridComponents()
 * ``` */
fun GridComponents.visualizeGridComponents(animationTicTimeOverride: Double? = null) {
    val (width, height) = flatten().fold(0 to 0) { (maxX, maxY), t ->
        (t.x + 1).coerceAtLeast(maxX) to (t.y + 1).coerceAtLeast(maxY)
    }
    val grid = Grid(width, height)
    val (nodeVisitationOrder, nodeDistances) = reversed().flatMapIndexed { i, component ->
        component.map { it to i.toDouble() }
    }.unzip()
    grid.visualizeGrid(
        currentlyVisitedNodes = nodeVisitationOrder,
        nodeDistances = nodeDistances,
        animationTicTimeOverride = animationTicTimeOverride
    )
}