package graphMateKT.graphics.gridGraphics

import graphMateKT.graphClasses.Grid
import graphMateKT.Tile
import graphMateKT.graphics.launchVisualization

/** Visualizes the grid and optionally its traversal process using a graphical interface.
 *
 * Distances are visualized by color gradients, with closer nodes appearing in warmer colors (red), and farther nodes in
 * cooler colors (blue). Deleted nodes are represented as white tiles, while unvisited nodes are shown in black.
 * Does **not** require css files, unlike the other graph visualization functions.
 *
 * <i>Example usage:</i>
 * ```
val grid = Grid(2, 2, true)
grid.visualizeGrid(
currentVisitedNodes = listOf(Tile(0, 0), Tile(1, 0), Tile(1, 1), Tile(0, 1)),
finalPath = listOf(Tile(0, 0), Tile(1, 0), Tile(1, 1)),
nodeDistances = listOf(0.0, 1.0, 2.0, 3.0),
screenTitle = "GraphMateKT visualizeGrid example usage",
animationTicTimeOverride = 1000.0,
closeOnEnd = true,
startPaused = true,
screenWidthMultiplier = 0.5
)
 * ```
 *
 * @param currentVisitedNodes A list of tiles representing the nodes visited in order during the traversal.
 * @param finalPath A list of tiles representing the final path in the grid.
 * @param nodeDistances A list of distances to each visited node.
 * @param screenTitle The title of the visualization window.
 * @param animationTicTimeOverride Overrides the default animation speed in milliseconds.
 * @param closeOnEnd If `true`, closes the visualization window when the animation ends.
 * @param startPaused If `true`, starts the visualization in a paused state.
 * @param screenWidthMultiplier Changes the width and height of the GUI window by a factor.
 * @throws IllegalStateException If the grid is improperly configured for visualization. */
fun Grid.visualizeGrid(
    currentVisitedNodes: List<Tile> = currentVisitedNodes(),
    finalPath: List<Tile> = finalPath(),
    nodeDistances: List<Double> = currentVisitedNodes.map { distanceTo(it) },
    screenTitle: String = "Grid visualizer (Click or space to pause and resume)",
    animationTicTimeOverride: Double? = null,
    closeOnEnd: Boolean = false,
    startPaused: Boolean = false,
    screenWidthMultiplier: Double = 1.0,
) {
    GridGraphics.grid = this
    GridGraphics.currentVisitedNodes = currentVisitedNodes
    GridGraphics.finalPath = finalPath
    GridGraphics.nodeDistances = nodeDistances
    GridGraphics.screenTitle = screenTitle
    GridGraphics.animationKeyFrameOverride = animationTicTimeOverride
    GridGraphics.startPaused = startPaused
    GridGraphics.closeOnEnd = closeOnEnd
    GridGraphics.screenWidthMultiplier = screenWidthMultiplier
    launchVisualization(GridGraphics())
}
