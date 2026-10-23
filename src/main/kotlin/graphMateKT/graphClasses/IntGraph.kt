package graphMateKT.graphClasses

import graphMateKT.IntComponents
import graphMateKT.debug
import graphMateKT.graphAlgorithms.DFS
import kotlin.system.measureTimeMillis

/** A specialized graph class that represent integer nodes ranging from 0 to size-1.
 *
 * The IntGraph class behaves a lot like the Graph class when used with integers.
 * However, it's more performant, because it does not need to maintain an internal mapping between the nodes and their
 * indexes in the adjacency list. The obvious drawback being it only supports integer nodes.
 * It also requires the number of nodes to be defined at initialization.
 *
 * <i>Example usage:</i>
 *
 * ```
 * val graph = IntGraph(3) // Creates a graph with nodes 0, 1 and 2
 * graph.addEdge(0, 1, 5.0)
 * graph.addEdge(0, 2, 2.0)
 * graph.addEdge(2, 1, 1.0)
 * graph.shortestPathsFrom(0, 1)
 * // NOTE: visualizeGraph() requires the smartgraph.css and smartgraph.properties files to be added to the root of your project.
 * graph.visualizeGraph() // Find the needed files here: https://github.com/Norskeaksel/GraphMateKT
 * ```
 *
 * @param size The number of nodes in the graph. Nodes are represented as integers from 0 to size-1.
 * @param debugTimeUse optional flag that prints the time use of run algorithms to standard error
 * @param isSparse If `true`, search results are stored in maps instead of arrays. More performant when searches only reach a
 * small part of a large graph. Defaults to `false`.*/
class IntGraph(private val size: Int, debugTimeUse: Boolean = false, isSparse: Boolean = false) :
    BaseGraph<Int>(size, debugTimeUse, isSparse) {

    private val nodes by lazy { IntArray(size) { it } }
    private val nrOfEdgesFrom by lazy { IntArray(size) }

    /** IntGraph doesn't support addNode(), because nodes are set on initialization.
     * @throws IllegalStateException if called.*/
    override fun addNode(node: Int) =
        error("IntGraph doesn't support addNode(), because nodes are set on initialization.")

    /** Adds a directed edge from node1 to node2.
     *
     * NOTE: For performance, the nodes are not validated. Both must be in 0 until size, otherwise the behavior is
     * undefined.
     *
     * @param node1 The starting node of the edge.
     * @param node2 The ending node of the edge.
     * @param weight The weight of the edge, for example used to calculate distances with shortestPathsFrom. Defaults to 1.0. */
    override fun addEdge(node1: Int, node2: Int, weight: Double) {
        adjacencyListIsFinalized = false
        edges.addEdge(node1, node2, weight)
        edgesCount++
        nrOfEdgesFrom[node1]++
    }

    override fun id2NodeOrNull(id: Int) = id
    override fun id2Node(id: Int) = id
    override fun node2IdOrNull(node: Int) = node
    override fun node2Id(node: Int) = node
    override fun nodes() = nodes.toList()
    override fun stronglyConnectedComponents(): IntComponents {
        finalizeAdjacencyListIfNeeded(true)
        val scc: IntComponents
        val time = measureTimeMillis {
            scc = DFS(adjacencyList).stronglyConnectedComponents()
        }
        if (debugTimeUse) {
            debug("stronglyConnectedComponents took $time ms.")
        }
        return scc
    }
}
