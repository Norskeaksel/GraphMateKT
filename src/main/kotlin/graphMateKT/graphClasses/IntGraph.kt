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
class IntGraph(private val size: Int, private val nrOfEdges: Int, debugTimeUse: Boolean = false) :
    BaseGraph<Int>(debugTimeUse) {

    private val nodes = IntArray(size) { it }
    private val from = IntArray(nrOfEdges)
    private val to = IntArray(nrOfEdges)
    private val weights = DoubleArray(nrOfEdges) { 1.0 }
    private val nrOfEdgesFrom = IntArray(size)
    private var adjacencyListIsFinalized = false

    override fun finalizeAdjacencyListIfNeeded() {
        if (adjacencyListIsFinalized)
            return
        val starts = IntArray(size)
        val ends = IntArray(size)
        val flattenedAdjacencyList = IntArray(edgesCount)
        val flattenWeights = DoubleArray(edgesCount)
        var sum = 0
        repeat(size) { i ->
            starts[i] = sum
            sum += nrOfEdgesFrom[i]
            ends[i] = sum
        }
        repeat(edgesCount) { i ->
            val u = from[i]
            val v = to[i]
            val offset = --nrOfEdgesFrom[u]
            val idx = starts[u] + offset
            flattenedAdjacencyList[idx] = v
            flattenWeights[idx] = weights[i]
        }
        adjacencyList = FlattenedAdjacencyList(flattenedAdjacencyList, starts, ends, flattenWeights)
        adjacencyListIsFinalized = true
    }

    override fun addNode(node: Int) =
        error("IntGraph doesn't support addNode(), because nodes are set on initialization.")

    override fun addEdge(node1: Int, node2: Int, weight: Double) {
        require(edgesCount < nrOfEdges) { "Can't add a ${edgesCount + 1}th edge, becaues it exceedes nrOfEdges=${nrOfEdges()}" }
        from[edgesCount] = node1
        to[edgesCount] = node2
        weights[edgesCount] = weight
        nrOfEdgesFrom[node1]++
        edgesCount++
        adjacencyListIsFinalized = false
    }

    override fun addEdge(node1: Int, node2: Int) {
        require(edgesCount < nrOfEdges) { "Can't add a ${edgesCount + 1}th edge, becaues it exceedes nrOfEdges=${nrOfEdges()}" }
        from[edgesCount] = node1
        to[edgesCount] = node2
        nrOfEdgesFrom[node1]++
        edgesCount++
        adjacencyListIsFinalized = false
    }

    override fun id2Node(id: Int) = id
    override fun node2Id(node: Int) = node
    override fun nodes() = nodes.toList()
    override fun stronglyConnectedComponents(): IntComponents {
        finalizeAdjacencyListIfNeeded()
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
