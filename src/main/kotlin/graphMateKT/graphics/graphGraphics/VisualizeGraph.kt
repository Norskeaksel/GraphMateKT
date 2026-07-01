package graphMateKT.graphics.graphGraphics

import graphMateKT.graphClasses.BaseGraph
import graphMateKT.graphics.FxLauncher

/** Visualizes the graph with Bruno Silva's JavaFXSmartGraph library.
 *
 * The function visualizes nodes with edges using a force-directed algorithm. Then it animates the traversal of the graph,
 * highlighting visited nodes and the final path from the start node to the target node if a search has been performed.
 *
 * <i>Example usage:</i>
 *
 * ```
    val graph = Graph(false)
    graph.connect("A", "B")
    graph.connect("A", "C")
    graph.visualizeGraph(
        isBidirectional = true,
        finalPath = listOf("A", "B"),
        screenTitle = "GraphMateKT visualizeGraph example usage",
        animationTicTimeOverride = 2000.0,
        closeOnEnd = true,
        startPaused = true,
)
 * ```
 * **NOTE:** to use this visualizeGraph() function, the files smartgraph.css and smartgraph.properties must be **added manually** to the root of your project,
 * as described in Bruno Silva's JavaFXSmartGraph repository: https://github.com/brunomnsilva/JavaFXSmartGraph

 * @param isBidirectional If `true`, visualizes the graph as bidirectional, otherwise as directed.
 * @param finalPath A list of nodes that can override the final path, and be animated instead. If not provided, the graph's own final path is used.
 * @param screenTitle The title of the visualization window.
 * @param animationTicTimeOverride Overrides the default animation speed in milliseconds.
 * @param closeOnEnd If `true`, closes the visualization window when the animation ends.
 * @param startPaused If `true`, starts the visualization in a paused state.
 * @throws IllegalStateException If the graph is improperly configured for visualization. */
fun <T : Any> BaseGraph<T>.visualizeGraph(
    isBidirectional: Boolean = false,
    finalPath:List<T> = finalPath() ?: emptyList(),
    screenTitle: String = "Graph visualizer (Click space to pause and resume)",
    animationTicTimeOverride: Double? = null,
    closeOnEnd: Boolean = false,
    startPaused: Boolean = false,
) {
    if (isBidirectional) {
        @Suppress("UNCHECKED_CAST")
        BidirectionalGraphGraphics.graph = this as BaseGraph<Any>
        BidirectionalGraphGraphics.finalPath = finalPath
        BidirectionalGraphGraphics.screenTitle = screenTitle
        BidirectionalGraphGraphics.animationTicTimeOverride = animationTicTimeOverride
        BidirectionalGraphGraphics.startPaused = startPaused
        BidirectionalGraphGraphics.closeOnEnd = closeOnEnd
        FxLauncher.launch(BidirectionalGraphGraphics())
    } else {
        @Suppress("UNCHECKED_CAST")
        GraphGraphics.graph = this as BaseGraph<Any>
        GraphGraphics.finalPath = finalPath
        GraphGraphics.screenTitle = screenTitle
        GraphGraphics.animationTicTimeOverride = animationTicTimeOverride
        GraphGraphics.startPaused = startPaused
        GraphGraphics.closeOnEnd = closeOnEnd
        FxLauncher.launch(GraphGraphics())
    }
}
