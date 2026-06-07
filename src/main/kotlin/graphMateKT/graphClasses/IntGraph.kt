package graphMateKT.graphClasses

import graphMateKT.IntComponents
import graphMateKT.debug
import graphMateKT.graphAlgorithms.DFS
import kotlin.system.measureTimeMillis

/** A specialized graph class that represent integer nodes ranging from 0 to size-1.
 *
 * The IntGraph class behaves a lot like the Graph class when used with integers like the example above.
 * However, * it's more performant, because it does not need to maintain an internal mapping between the nodes and their
 * indexes in the adjacency list. The obvious drawback being it only supports integer nodes.
 * It also requires the number of nodes and edges to be defined at initialization.
 *
 * <i>Example usage:</i>
 *
 * ```
 * val intGraph = IntGraph(3) // Creates a graph with nodes 0, 1 and 2
 * graph.addEdge(0, 1, 5.0)
 * graph.addEdge(0, 2, 2.0)
 * graph.addEdge(2, 1, 1.0)
 * graph.dijkstra(0, 1)
 * // NOTE: visualizeGraph() requires the smartgraph.css and smartgraph.properties files to be added to the root of your project.
 * graph.visualizeGraph() // Find the needed files here: https://github.com/Norskeaksel/GraphMateKT
 * ```
 *
 * @param size The number of nodes in the graph. Nodes are represented as integers from 0 to size-1. This cannot be altered later.
 * @param nrOfEdges The number of edges in the graph. This cannot be altered later. */
class IntGraph(size: Int, nrOfEdges: Int, private val debugTimeUse: Boolean = false) : BaseGraph<Int>(debugTimeUse) {

    private val nodes = IntArray(size) { it }
    private val from = IntArray(nrOfEdges)
    private val to = IntArray(nrOfEdges)
    private val weights = DoubleArray(nrOfEdges) { 1.0 }


    override fun addNode(node: Int) =
        error("IntGraph doesn't support addNode(), because nodes are defined by the IntGraph size")

    override fun addEdge(node1: Int, node2: Int, weight: Double) {
        adjacencyList[node1].add(weight to node2)
    }

    override fun addUnweightedEdge(node1: Int, node2: Int) {
        unweightedAdjacencyList[node1].add(node2)
    }

    override fun id2Node(id: Int) = id
    override fun node2Id(node: Int) = node
    override fun nodes() = nodes
    override fun neighbours(t: Int) = if (isWeighted) adjacencyList[t].map { it.second } else unweightedAdjacencyList[t]
    override fun edges(t: Int) = if (isWeighted) adjacencyList[t] else neighbours(t).map { 1.0 to it }
    override fun stronglyConnectedComponents(): IntComponents {
        val scc: IntComponents
        val time = measureTimeMillis {
            useWeightedConnectionsIfNeeded("stronglyConnectedComponents")
            scc = DFS(unweightedAdjacencyList).stronglyConnectedComponents()
        }
        if (debugTimeUse) {
            debug("stronglyConnectedComponents took $time ms.")
        }
        return scc
    }
}
