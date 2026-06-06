package graphMateKT.graphGraphics

import graphMateKT.Components
import graphMateKT.IntComponents
import graphMateKT.graphClasses.Graph

/** Visualizes the strongly connected components (SCCs) of a graph.
 *
 * This function creates a new graph where each component is represented as a cycle,
 * connecting its nodes in a circular manner. Then the graph is visualized.
 *
 * <i>Example usage:</i>
 *
 * ```
 * val graph = Graph(false)
 * graph.addEdge("A", "B")
 * graph.addEdge("B", "C")
 * graph.addEdge("C", "B")
 * val scc = graph.stronglyConnectedComponents()
 * scc.visualizeComponents() */
fun Components.visualizeComponents() {
    val sccGraph = Graph()
    forEach { component ->
        component.indices.forEach { i ->
            component.let { c ->
                sccGraph.addEdge(c[i], c[(i + 1) % c.size])
            }
        }
    }
    sccGraph.visualizeGraph()
}

/** Visualizes the strongly connected components (SCCs) of a graph with integer nodes.
 *
 * This function is a specialized version of `visualizeComponents` for integer-based components.
 *
 * <i>Example usage:</i>
 *
 * ```
 * val graph = IntGraph(3, false)
 * graph.addEdge(0, 1)
 * graph.addEdge(1, 2)
 * graph.addEdge(2, 1)
 * val scc = graph.stronglyConnectedComponents()
 * scc.visualizeIntComponents() */
fun IntComponents.visualizeIntComponents() = (this as Components).visualizeComponents()