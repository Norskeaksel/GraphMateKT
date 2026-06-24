import kotlin.system.measureTimeMillis
import java.util.*
import kotlin.math.min
import java.io.InputStream

/** Edge has a weight w to a destination node v */
typealias Edge = Pair<Double, Int>
/** Mutable list of edges */
typealias Edges = MutableList<Edge>
/** List of list of nodes */
typealias Components = List<List<Any>>
/** List of list of integer nodes */
typealias IntComponents = List<List<Int>>

/** Represents a node in the Grid graph with x and y coordinates and optional data, which can be considered the node value
 *
 * @property x The x-coordinate of the tile.
 * @property y The y-coordinate of the tile.
 * @property data Optional data associated with the tile, which can be considered a node of any type */
data class Tile(val x: Int, val y: Int, var data: Any? = null){
    /** Checks if the `data` property of the `Tile` is a `Char` and whether that `Char` represents a digit.
     *
     * @return `true` if `data` is a `Char` and is a digit, otherwise `false`. */
    fun dataIsDigit() = data is Char && (data as Char).isDigit()

    fun xPlusYTimesWidth(width:Int) = x + y * width
}

internal data class Not(val node: Any)
internal operator fun Any.not() = Not(this)

internal data class TrieNode(val children:MutableMap<Char, TrieNode> = mutableMapOf(), var isTerminal:Boolean = false)


/** And abstract class that's used by the Graph, IntGraph and Grid classes for common functionality */
abstract class BaseGraph<T : Any>(protected val debugTimeUse: Boolean = false) {
    // PROPERTIES AND INITIALIZATION
    protected lateinit var adjacencyList: AdjacencyList
    protected var edgesCount = 0
    private var searchResults: GraphSearchResults? = null
    private var finalPath: List<T>? = null
    private var allDistances: Array<DoubleArray>? = null

    // ABSTRACT FUNCTIONS
    /** @return a list of the nodes in the graph. */
    abstract fun nodes(): List<T>

    /** Adds the given node to the graph
     * @param node The node to add */
    abstract fun addNode(node: T)

    /** Adds an unweighted edge between two nodes in the graph, and creates the nodes if they don't exist.
     *
     * @param node1 The starting node of the edge.
     * @param node2 The ending node of the edge. */
    abstract fun addEdge(node1: T, node2: T)

    /** Adds an edge between two nodes in the graph, and creates the nodes if they don't exist.
     *
     * @param node1 The starting node of the edge.
     * @param node2 The ending node of the edge.
     * @param weight The weight of the edge, for example used to calculate distances with dijkstra. */
    abstract fun addEdge(node1: T, node2: T, weight: Double)

    protected abstract fun node2Id(node: T): Int?
    protected abstract fun id2Node(id: Int): T?
    protected abstract fun finalizeAdjacencyListIfNeeded()

    /** Overloaded function that calls addEdge with weight converted to a double. */
    fun addEdge(node1: T, node2: T, weight: Int) {
        addEdge(node1, node2, weight.toDouble())
    }

    // CORE GRAPH OPERATIONS
    /** Connects two nodes in the graph, by calling addEdge(node1,node2) and addEdge(node2, node1)
     *
     * @param node1 The first node to connect.
     * @param node2 The second node to connect. */
    fun connect(node1: T, node2: T) {
        addEdge(node1, node2)
        addEdge(node2, node1)
    }

    /** Connects two nodes in the graph, by calling addEdge(node1,node2, weight) and addEdge(node2, node1, weight)
     *
     * @param node1 The first node to connect.
     * @param node2 The second node to connect.
     * @param weight The weight of the connection. */
    fun connect(node1: T, node2: T, weight: Double) {
        addEdge(node1, node2, weight)
        addEdge(node2, node1, weight)
    }

    /** Overloaded function that calls connect with weight converted to a double. */
    fun connect(node1: T, node2: T, weight: Int) {
        addEdge(node1, node2, weight)
        addEdge(node2, node1, weight)
    }

    // GRAPH INFORMATION
    /** @return The total number of nodes in the graph. */
    fun size() = nodes().size

    /** @return The total number of edges in the graph. */
    fun nrOfEdges() = edgesCount

    /** Retrieves the depth of the graph from the most recent search operation
     *
     * Depth is defined as the deepest level of recursion or the maximum distance from the starting node to
     * any other node during the search traversal if the weights are counted as 1.
     * @return The depth of the graph.
     * @throws IllegalStateException If neither DFS nor BFS has been run yet.*/
    fun depth() =
        searchResults?.depth ?: error("Can't retrieve depth because neither DFS nor BFS has been run yet.")

    /** Retrieves a list of all visited nodes on the order they were visited during the last search operation (DFS, BFS, Dijkstra).
     *
     * @return A list of visited nodes. Or an empty list if no search algorithm (DFS, BFS, Dijkstra) has been run yet. */
    fun currentVisitedNodes(): List<T> =
        searchResults?.currentVisited?.map { id2Node(it)!! }
            ?: emptyList()


    /** Retrieves a (unordered) list of all visited nodes. Or an empty list if no search algorithm (DFS, BFS, Dijkstra) has been run yet.
     *
     * @return A list of visited nodes or an empty list if no search algorithm (DFS, BFS, Dijkstra) has been run yet. */
    fun visitedNodes() = searchResults?.run { visited.indices.mapNotNull { if (visited[it]) id2Node(it) else null } }
        ?: emptyList()

    /** Retrieves the shortest path from the start to target node path during the most recent search operation
     * (DFS, BFS, Dijkstra)
     *
     * @return A list of nodes representing the final path or an empty list if no search algorithm (DFS, BFS, Dijkstra) has been run yet. */
    fun finalPath(): List<T> = finalPath ?: emptyList()

    /** Checks if the target node was found during the most recent search operation (BFS, Dijkstra).
     *
     * @return `true` if the target node was found, `false` otherwise. */
    fun foundTarget() = searchResults?.foundTarget ?: false

    /** Retrieves the distance to the specified node from the starting node of the most recent search operation (BFS, Dijkstra).
     *
     * If the graph is weighted, the weighted distance is returned. If the graph is unweighted, the distance
     * is calculated as the number of edges.
     *
     * @param node The target node whose distance is to be retrieved.
     * @return The distance to the specified node.
     * @throws IllegalStateException If neither BFS nor Dijkstra has been executed yet. */
    fun distanceTo(node: T): Double {
        val id = node2Id(node) ?: error("Node '$node' not found in graph")
        searchResults?.let {
            return it.distances[id]
        }
        error("Haven't computed distance to '$node' because neither BFS nor Dijkstra  has been run yet.")
    }

    /** Retrieves the maximum distance from the starting node to any other node of the most recent search operation (BFS, Dijkstra).
     *
     * If no search has been performed or a node cannot be reached, the function returns `Double.MAX_VALUE`.
     * @return The maximum distance to any node. */
    fun maxDistance() = searchResults?.distances?.maxOrNull() ?: Double.MAX_VALUE

    /** Retrieves the node that is the farthest from the starting node in the most recent search operation (BFS, Dijkstra).
     *
     * @return The node that is the farthest from the starting node.
     * @throws IllegalStateException If no search algorithm (BFS, Dijkstra) has been executed yet. */
    fun furthestNode(): T =
        searchResults?.let { r -> id2Node(r.distances.indices.first { r.distances[it] == maxDistance() })!! }
            ?: error("Haven't computed furthest node because no search algorithm (dfs, bfs, dijkstra) has been run yet.")

    /** Retrieves a list of edges connected to the specified node.
     *
     * Each edge is represented as a pair, where the first element is the weight of the edge
     * (as a `Double`), and the second element is the connected node.
     *
     * @param t The node whose edges are to be retrieved.
     * @return A list of pairs representing the edges connected to the node.
     * @throws IllegalStateException If the specified node is not found in the graph. */
    fun edges(t: T): List<Pair<Double, T>> = finalizeAdjacencyListIfNeeded().run {
        node2Id(t)?.let { adjacencyList.edges(it) }?.map { Pair(it.first, id2Node(it.second)!!) }
            ?: error("Node $t not found in graph")
    }

    /** Retrieves a list the neighboring nodes of the specified node.
     *
     * WARNING: should be replaced by the forEachNeighbour function if performance is critical, to avoid copying overhead.
     *
     * @param t The node whose neighbors are to be retrieved.
     * @return A list of neighboring nodes connected to the specified node.
     * @throws IllegalStateException If the specified node is not found in the graph. */
    fun neighbours(t: T): List<T> = finalizeAdjacencyListIfNeeded().run {
        node2Id(t)?.let { adjacencyList.neighbours(it) }
            ?.map { id2Node(it)!! }
            ?: error("Node '$t' not found in graph")
    }

    /** Executes a given function on all the neighbours of a given node.
     *
     * More performant then retrieving a copied list of nodes and calling forEach on them
     *
     * @param t The node whose neighbours we want to process
     * @param action The function to be called on each neighbour
     * @throws IllegalStateException If the specified node is not found in the graph. */
    fun forEachNeighbour(t: T, action: (T) -> Unit) = finalizeAdjacencyListIfNeeded().run {
        node2Id(t)?.let { u ->
            adjacencyList.forEachNeighbour(u) { v ->
                id2Node(v)?.let(action)
            }
        } ?: error("Node '$t' not found in graph")
    }

    // SEARCH ALGORITHMS

    /** Performs a Breadth-First Search, which finds the shortest path from the starting node(s) to all other nodes,
     * assuming the graph is unweighted (all edges have a weight of 1.0)
     * It stores results that can be retrieved with the following functions:
     *
     * - `depth()`
     * - `currentVisitedNodes()`
     * - `visitedNodes()`
     * - `finalPath()`
     * - `foundTarget()`
     * - `distanceTo(node: T)`
     * - `maxDistance()`
     * - `furthestNode()`
     *
     * @param startNodes A list of starting nodes for the BFS traversal.
     * @param target An optional target node. If specified, the search will stop once the target is found,
     * flag the target as found so that foundTarget() returns true, and store the path to the target node for use in visualization.
     * @param reset A boolean indicating whether to reset the previous search results. If set to false, previously visited nodes will not be visited again.
     * @throws IllegalStateException If any of the starting nodes or the target node is not found in the graph. */
    fun bfs(startNodes: List<T>, target: T? = null, reset: Boolean = true) {
        finalizeAdjacencyListIfNeeded()
        val time = measureTimeMillis {
            val startNodeIds = startNodes.map { node -> node2Id(node) ?: error("Node '$node' not found in graph") }
            val targetId = target?.let { node2Id(it) } ?: -1
            if (reset) searchResults = null
            searchResults = BFS(adjacencyList).bfs(startNodeIds, targetId, searchResults)
            target?.let {
                finalPath = getPath(it)
            }
        }
        if (debugTimeUse) {
            debug("bfs took $time ms.")
        }
    }

    /** Overload of fun bfs(startNodes: List<T>, target: T?, reset: Boolean) that accepts a single starting node instead of a list
     * @returnRuns bfs(listOf(startNode), target, reset) */
    fun bfs(startNode: T, target: T? = null, reset: Boolean = true) = bfs(listOf(startNode), target, reset)

    /** Performs a Depth-First Search, which finds all nodes that's reachable from the starting node.
     * It stores results that can be retrieved with the following functions:
     *
     * - `depth()`
     * - `currentVisitedNodes()`
     * - `visitedNodes()`
     *
     * @param startNode The starting node for the DFS traversal.
     * @param reset A boolean indicating whether to reset the previous search results. If set to false, previously visited nodes will not be visited again.
     * @throws IllegalStateException If the starting node is not found in the graph. */
    fun dfs(startNode: T, reset: Boolean = true) {
        finalizeAdjacencyListIfNeeded()
        val time = measureTimeMillis {
            val startId = node2Id(startNode) ?: error("Node '$startNode' not found in graph")
            if (reset) searchResults = null
            searchResults = DFS(adjacencyList).dfs(startId, searchResults)
        }
        if (debugTimeUse) {
            debug("dfs took $time ms.")
        }
    }

    /** Performs Dijkstra's algorithm, which finds the shortest path from the starting node to all other nodes. It
     *  stores results that can be retrieved with the following functions:
     *
     * - `depth()`
     * - `currentVisitedNodes()`
     * - `visitedNodes()`
     * - `finalPath()`
     * - `foundTarget()`
     * - `distanceTo(node: T)`
     * - `maxDistance()`
     * - `furthestNode()`
     *
     * If the graph is unweighted or has no weighted connections, a warning is issued, and BFS is executed instead.
     *
     * @param startNode The starting node for Dijkstra's algorithm.
     * @param target An optional target node. If specified, the algorithm will store the shortest path to the
     * target node for use in visualization and flag the target as found so that foundTarget() returns true
     * @throws IllegalStateException If the starting node or the target node is not found in the graph. */
    fun dijkstra(startNode: T, target: T? = null) {
        finalizeAdjacencyListIfNeeded()
        val time = measureTimeMillis {
            if (edgesCount == 0) {
                System.err.println("Warning: The adjacently list has no connections, making pathfinding infeasible.")
            }
            val startId = node2Id(startNode) ?: error("Node '$startNode' not found in graph")
            searchResults = Dijkstra(adjacencyList).dijkstra(startId, searchResults)
            target?.let {
                finalPath = getPath(it)
            }
        }
        if (debugTimeUse) {
            debug("dijkstra took $time ms.")
        }
    }

    /** Executes the Floyd-Warshall algorithm on the graph to compute the shortest paths between all pairs of nodes.
     *
     * This function calculates the shortest path distances for every pair of nodes in the graph and stores the results
     * in the `allDistances` property. The algorithm works for both weighted and unweighted graphs, but it assumes that
     * the graph does not contain negative weight cycles.
     *
     * The results can be retrieved using the `distanceFromUtoV(u: T, v: T)` function, which provides the shortest distance
     * between any two nodes.
     *
     * @throws IllegalStateException If the graph contains nodes but no edges, making pathfinding infeasible. */
    fun floydWarshall() {
        finalizeAdjacencyListIfNeeded()
        val time = measureTimeMillis {
            if (edgesCount == 0) {
                System.err.println("Warning: The graph has no edges, making pathfinding infeasible.")
            } else {
                allDistances = FloydWarshall(adjacencyList).floydWarshall()
            }
        }
        if (debugTimeUse) {
            debug("floydWarshall took $time ms.")
        }
    }

    /** Retrieves the shortest distance between two nodes in the graph.
     *
     * This function uses the results of the Floyd-Warshall algorithm to determine the shortest path distance
     * between the specified nodes.
     *
     * @param u The starting node.
     * @param v The target node.
     * @return The shortest distance between the two nodes.
     * @throws IllegalStateException If the Floyd-Warshall algorithm has not been executed before calling this function. */
    fun distanceFromUtoV(u: T, v: T) = allDistances?.let {
        it[node2Id(u)!!][node2Id(v)!!]
    } ?: error("FloydWarshall must be run sucsessfully before calling distanceFromUtoV")

    // ADDITIONAL ALGORITHMS
    /** Computes the Minimum Spanning Tree (MST) of the graph using Prim's algorithm.
     *
     * If the graph is unweighted, it is first converted to a weighted graph with default edge weights.
     *
     * @return A pair containing the total weight of the MST and the graph representing the MST.
     * @throws IllegalStateException If the graph is empty or not fully connected. */
    fun minimumSpanningTree(): Pair<Double, Graph> {
        finalizeAdjacencyListIfNeeded()
        val timeStart = System.currentTimeMillis()
        val (totalWeight, mst) = prims(adjacencyList).run {
            first to second.let { adjacencyList ->
                val mstGraph = Graph()
                adjacencyList.forEachIndexed { id, edges ->
                    edges.forEach { (w, v) ->
                        mstGraph.connect(id2Node(id)!!, id2Node(v)!!, w)
                    }
                }
                mstGraph
            }
        }
        val timeStop = System.currentTimeMillis()
        if (debugTimeUse) {
            debug("minimumSpanningTree took ${timeStop - timeStart} ms.")
        }
        return totalWeight to mst
    }

    /** Builds an order of nodes so that the first nodes has no outgoing edges, then nodes with edges pointing to these
     * and so on, assuming the graph is a Directed Acyclic Graph (DAG). This is done by running a DFS from each node
     * and ordering the nodes by descending depth (post-order).
     *
     * @return A list of nodes in topological order if the graph was a DAG.
     * Otherwise, returns a list of nodes in undefined order */
    open fun topologicalSort(): List<T> {
        finalizeAdjacencyListIfNeeded()
        val topologicalSorting: List<T>
        val time = measureTimeMillis {
            topologicalSorting = DFS(adjacencyList).topologicalSort().map { id2Node(it)!! }
        }
        if (debugTimeUse) {
            debug("topologicalSort took $time ms.")
        }
        return topologicalSorting
    }

    /** Identifies groups where each node is reachable from every other node in the group.
     *
     * It does this by creating a reversed graph by reversing the direction of all edges in the graph.
     * Then, it performs a topological sort on the reversed graph to determine the order of processing nodes.
     * Now it can run a depth-first search (DFS) on the original graph in the order determined by the topological sort.
     * Nodes visited during each DFS traversal belong to the same strongly connected component.
     *
     * @return A list of strongly connected components, where each component is a list of nodes. */
    open fun stronglyConnectedComponents(): List<List<T>> {
        finalizeAdjacencyListIfNeeded()
        val scc: List<List<T>>
        val time = measureTimeMillis {
            val sccIds = DFS(adjacencyList).stronglyConnectedComponents()
            scc = sccIds.map { component -> component.map { id2Node(it)!! } }
        }
        if (debugTimeUse) {
            debug("stronglyConnectedComponents took $time ms.")
        }
        return scc
    }

    /** Calculates the number of distinct paths from the starting node to the target node in the graph.
     *
     * This function assumes that the graph is a Directed Acyclic Graph (DAG) and uses dynamic programming
     * to count the number of paths efficiently.
     *
     * @param startNode The starting node.
     * @param targetNode The target node.
     * @param mod Makes the function return the nrOfPaths modulo mod. Defaults to Long.MAX_VALUE
     * @return The number of distinct paths from the starting node to the target node.
     * @throws IllegalStateException If either the starting node or the target node is not found in the graph. */
    fun nrOfPaths(startNode: T, targetNode: T, mod: Long = Long.MAX_VALUE): Long {
        finalizeAdjacencyListIfNeeded()
        val nrOfPaths: Long
        val time = measureTimeMillis {
            val startId = node2Id(startNode) ?: error("Node '$startNode' not found in graph")
            val targetId = node2Id(targetNode) ?: error("Node '$targetNode' not found in graph")
            nrOfPaths = nrOfPaths(adjacencyList, startId, targetId, mod)
        }
        if (debugTimeUse) {
            debug("nrOfPaths took $time ms.")
        }
        return nrOfPaths
    }

// PATH UTILITIES
    /** Retrieves the path from the starting node to the specified target node based on the most recent search results.
     *
     * @param target The target node for which the path is to be retrieved.
     * @return A list of nodes representing the path from the start to the target node.
     * @throws IllegalStateException If no search algorithm (DFS, BFS, Dijkstra) has been executed yet. */
    fun getPath(target: T): List<T> {
        val targetId = node2Id(target)
        val pathIds = searchResults?.let { getPath(targetId, it.parents) }
            ?: error("Can't getPath because no search has (DFS, BFS, Dijkstra) been run yet")
        val path = pathIds.mapNotNull { id2Node(it) }
        return path
    }

    private fun getPath(destination: Int?, parents: IntArray): List<Int> {
        val path = mutableListOf<Int>()
        destination?.let { dest ->
            var current = dest
            while (parents[current] != -1 && parents[current] != destination) {
                path.add(current)
                current = parents[current]
            }
            path.add(current)
        }
        return path.reversed()
    }

// HELPER FUNCTIONS
    /** Clears the search results stored in the graph.
     *
     * The following functions rely on the private `searchResults` property, which is populated by running a search algorithm
     * (DFS, BFS, or Dijkstra). These functions will throw an `IllegalStateException` if no search is run after calling this function:
     *
     * - `depth()`:
     * - `currentVisitedNodes()`:
     * - `visitedNodes()`:
     * - `finalPath()`:
     * - `foundTarget()`:
     * - `distanceTo(node: T)`:
     * - `maxDistance()`:
     * - `furthestNode()`: */
    fun resetSearchResults() {
        searchResults = GraphSearchResults(nodes().size)
    }

// PRINTER FUNCTIONS
    /** Prints the graph's adjacency list to the standard error stream. */
    open fun print() = nodes().forEach {
        System.err.println("$it ---> ${edges(it)}")
    }

    /** Returns a string representation of the graph, showing which nodes each node is connected to. */
    override fun toString(): String {
        return buildString {
            nodes().forEach { node ->
                val edges = edges(node)
                val edgeString = edges.joinToString { it.second.toString() }
                append("$node ----> [$edgeString]\n")
            }
        }
    }

    /** Prints which nodes each node is connected to. */
    fun printConnections() = System.err.println(toString())
}


/** A general graph class that represents nodes of any datatype.
 *
 * Any new node is given an ID upon creation, which is used to build an adjacency list. The class maintains internal
 * maps between ID's and nodes and vice versa.
 *
 * <i>Example usage:</i>
 *
 * ```
 * val graph = Graph()
 * graph.addEdge("A", "B", 5.0)
 * graph.addEdge("A", "C", 2.0)
 * graph.addEdge("C", "B", 1.0)
 * graph.dijkstra("A", "B")
 * // NOTE: visualizeGraph() requires the smartgraph.css and smartgraph.properties files to be added to the root of your project.
 * graph.visualizeGraph() // Find the needed files here: https://github.com/Norskeaksel/GraphMateKT
 * ```
 *
 * @param debugTimeUse If true, the time taken by each graph algorithm is printed to the standard error stream. Defaults to false.
 */
class Graph(debugTimeUse: Boolean = false) : BaseGraph<Any>(debugTimeUse) {
    private var nrOfNodes = 0
    private val node2id = mutableMapOf<Any, Int>()
    private val id2Node = mutableMapOf<Int, Any>()
    private val localAdjacencyList = mutableListOf<Edges>()
    private var adjacencyListIsFinalized = true

    private fun getOrAddNodeId(node: Any): Int {
        return node2id[node] ?: addNode(node).run { node2id[node]!! }
    }

    override fun addNode(node: Any) {
        if (node2id.containsKey(node)) {
            //System.err.println("Warning: The node already exists, it can't be added again")
            return
        }
        node2id[node] = nrOfNodes
        id2Node[nrOfNodes++] = node
        localAdjacencyList.add(mutableListOf())
        adjacencyListIsFinalized = false
    }

    override fun addEdge(node1: Any, node2: Any, weight: Double) {
        val id1 = getOrAddNodeId(node1)
        val id2 = getOrAddNodeId(node2)
        localAdjacencyList[id1].add(weight to id2)
        edgesCount++
        adjacencyListIsFinalized = false
    }

    override fun addEdge(node1: Any, node2: Any) {
        addEdge(node1, node2, 1.0)
    }

    override fun node2Id(node: Any): Int? = node2id[node]
    override fun id2Node(id: Int): Any? = id2Node[id]
    override fun nodes(): List<Any> = id2Node.values.toList()
    override fun finalizeAdjacencyListIfNeeded() {
        if (adjacencyListIsFinalized) return
        adjacencyList = NestedAdjacencyList(localAdjacencyList)
        adjacencyListIsFinalized = true
    }
}


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


/** A general graph class that represents a 2D grid structure of nodes.
 *
 * It uses the data class:
 * `Tile(val x: Int, val y: Int, var data: Any? = null)`
 * to represent nodes of any datatype, where each node also has x and y coordinates.
 *
 * The grid can be created in two ways:
 * - By specifying a `width` and `height`, optionally initializing it with tiles that has `data=null`.
 * - By passing a list of strings, where each string represents a row, and all strings must have the same length.
 *
 * The Grid class supports the same algorithms as the Graph class. Additionally, it
 * provides methods for connecting nodes in the grid without explicitly adding edges:
 * - `.connectGridDefault()` connects each node to its neighbors in the up, down, left, and right directions, if they exist.
 * - `.connectGrid(::yourCustomFunction)` (or `.connectGrid { yourLambda }`) allows custom connections, where the function
 *   takes a `Tile` and returns a `List<Tile>` to connect to.
 *
 * <i>Example usage:</i>
 *
 * ```
 * val grid = Grid(100,100)
 * grid.connectGridDefault()
 * grid.bfs(Tile(50,50))
 * grid.visualizeGrid()
 * ```
 * <i>String constructor and custom connections example usage:</i>
 *
 * ```
 * val stringList = listOf(
 * "0#4",
 * "123",
 * "234"
 * )
 * val grid = Grid(stringList)
 * grid.deleteNodesWithData('#')
 * grid.connectGrid { t ->
 *     grid.getStraightNeighbours(t).filter { it.x >= t.x || it.y > t.y } // connect right and down
 * }
 * grid.bfs(Tile(0, 0, 'S'))
 * grid.visualizeGrid()
 * ```
 *
 * @param width The width of the grid (number of columns).
 * @param height The height of the grid (number of rows).
 * @param initWithDatalessTiles If `true`, initializes the grid with empty tiles. */
class Grid(val width: Int, val height: Int, initWithDatalessTiles: Boolean = true, debugTimeUse: Boolean = false) :
    BaseGraph<Tile>(debugTimeUse) {
    private val nodes = MutableList<Tile?>(width * height) { null }
    private val localAdjacencyList = MutableList<Edges>(width * height) { mutableListOf() }
    private var adjacencyListIsFinalized = false

    /** Construct the grid from a list of strings, where each string represents a row in the grid, and each character, a node.
     *
     * Sets the grid height to the list size and the width to the length of the first string.
     * Requires that all strings have the same length. If some cells should be deleted,
     * `deleteNodeAtXY(x,y)` or `deleteNodesWithData(data)` can be used after the grid is created.
     *
     * @param stringGrid A list of strings representing the grid
     * */
    constructor(stringGrid: List<String>) : this(stringGrid[0].length, stringGrid.size) {
        require(stringGrid.any { it.length == width })
        { "All lines in the string grid must have the same length" }
        stringGrid.forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                val t = Tile(x, y, c)
                val id = node2Id(t)
                nodes[id] = t
            }
        }
    }

    init {
        if (initWithDatalessTiles) {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    addNode(Tile(x, y))
                }
            }
        }
    }


    override fun addNode(node: Tile) {
        val id = node2Id(node)
        nodes[id] = node
        adjacencyListIsFinalized = false
    }

    override fun node2Id(node: Tile) = node.x + node.y * width

    override fun id2Node(id: Int) = if (id in 0 until width * height) nodes[id] else null
    override fun finalizeAdjacencyListIfNeeded() {
        if (adjacencyListIsFinalized) return
        adjacencyList = NestedAdjacencyList(localAdjacencyList)
        adjacencyListIsFinalized = true
    }

    override fun addEdge(node1: Tile, node2: Tile, weight: Double) {
        val u = node2Id(node1)
        val v = node2Id(node2)
        localAdjacencyList[u].add(Edge(weight, v))
        adjacencyListIsFinalized = false
    }

    override fun addEdge(node1: Tile, node2: Tile) {
        addEdge(node1, node2, 1.0)
    }

    override fun nodes(): List<Tile> = nodes.filterNotNull()
    override fun topologicalSort() =
        finalizeAdjacencyListIfNeeded().run { DFS(adjacencyList).topologicalSort(deleted()).map { id2Node(it)!! } }

    override fun stronglyConnectedComponents() =
        finalizeAdjacencyListIfNeeded().run { DFS(adjacencyList).stronglyConnectedComponents(deleted()) }
            .map { component -> component.mapNotNull { id2Node(it) } }

    private fun deleted() = BooleanArray(nodes.size) { nodes[it] == null }

    private fun xyInRange(x: Int, y: Int) = x in 0 until width && y in 0 until height
    private fun xy2Id(x: Int, y: Int) =
        if (xyInRange(x, y)) (x + y * width).let { if (indexHasNode(it)) it else null } else null

    /** Retrieves the `Tile` node at the specified (x, y) coordinates, if it exists.
     *
     * @param x The x-coordinate of the node.
     * @param y The y-coordinate of the node.
     * @return The `Tile` node at the given coordinates, or `null` if no node exists at the specified location. */
    fun xy2Node(x: Int, y: Int) = xy2Id(x, y)?.let { id2Node(it) }
    private fun indexHasNode(index: Int) = nodes.getOrNull(index) != null
    private fun deleteNodeId(id: Int) {
        nodes[id] = null
    }

    /** Deletes the node at the specified (x, y) coordinates in the grid.
     *
     * If the coordinates are outside the grid, a warning is printed, and no action is taken.
     *
     * @param x The x-coordinate of the node to delete.
     * @param y The y-coordinate of the node to delete. */
    fun deleteNodeAtXY(x: Int, y: Int) {
        val id = xy2Id(x, y) ?: run {
            System.err.println("Warning, coordinates ($x, $y) are outside the grid")
            return
        }
        deleteNodeId(id)
    }

    /** Deletes all nodes in the grid that have the specified data. Deleted nodes are not considered neighbours of nodes.
     * @param data The data value to match for deletion. */
    fun deleteNodesWithData(data: Any?) {
        nodes.indices.forEach { i ->
            if (nodes[i]?.data == data) {
                deleteNodeId(i)
            }
        }
    }

    /** Retrieves the straight (orthogonal) neighbors of the given tile.
     *
     * The neighbors are the tiles directly above, to the left, to the right and below the given tile, in that order,
     * if they exist within the grid boundaries and are not deleted.
     *
     * @param t The tile for which to retrieve the straight neighbors.
     * @return A list of straight neighbors of the given tile, or an empty list if no neighbors exist. */
    fun getStraightNeighbours(t: Tile) =
        listOfNotNull(
            xy2Node(t.x, t.y - 1),
            xy2Node(t.x - 1, t.y),
            xy2Node(t.x + 1, t.y),
            xy2Node(t.x, t.y + 1),
        )

    /** Retrieves the diagonal neighbors of the given tile.
     *
     * The diagonal neighbors are the tiles located at the top-left, top-right, bottom-left, and bottom-right
     * relative to the given tile, if they exist within the grid boundaries and are not deleted.
     *
     * @param t The tile for which to retrieve the diagonal neighbors.
     * @return A list of diagonal neighbors of the given tile, or an empty list if no neighbors exist. */
    fun getDiagonalNeighbours(t: Tile) = listOfNotNull(
        xy2Node(t.x - 1, t.y - 1),
        xy2Node(t.x + 1, t.y - 1),
        xy2Node(t.x - 1, t.y + 1),
        xy2Node(t.x + 1, t.y + 1),
    )

    /** Retrieves all neighbors (both straight and diagonal) of the given tile.
     *
     * The neighbors include tiles directly above, below, to the left, to the right,
     * as well as diagonally adjacent tiles (top-left, top-right, bottom-left, bottom-right),
     * if they exist within the grid boundaries and are not deleted.
     *
     * @param t The tile for which to retrieve all neighbors.
     * @return A list of all neighbors of the given tile, or an empty list if no neighbors exist. */
    fun getAllNeighbours(t: Tile) = getStraightNeighbours(t) + getDiagonalNeighbours(t)

    /** Connects all nodes in the grid with their neighbors, using a user-defined function to determine the neighbors.
     *
     * This function iterates through all nodes in the grid and connects each node to its neighbors as determined
     * by the `getNeighbours` function. The connections can be either unidirectional or bidirectional, based on the
     * `bidirectional` parameter.
     *
     * <i>Example usage:<i>
     * ```
     * val grid = Grid(100,100, true)
     * grid.connectGrid { t ->
     *     grid.getStraightNeighbours(t) + grid.getDiagonalNeighbours(t)
     * }
     * grid.bfs(Tile(50,50))
     * grid.visualizeGrid()
     * ```
     *
     * @param bidirectional If `true`, connections between nodes are bidirectional. If `false`, connections are unidirectional.
     * Defaults to false because many connection pattens are inherently unidirectional, and we want to avoid duplicate edges.
     * @param getNeighbours A function that takes a `Tile` as input and returns a list of neighboring `Tile` objects to connect to.
     */
    fun connectGrid(bidirectional: Boolean = false, getNeighbours: (t: Tile) -> List<Tile>) {
        nodes().forEach { t ->
            val neighbours = getNeighbours(t)
            neighbours.forEach {
                if (bidirectional) {
                    connect(t, it)
                } else {
                    addEdge(t, it)
                }
            }
        }
        finalizeAdjacencyListIfNeeded()
        adjacencyListIsFinalized = true
    }

    /** Connects all nodes in the grid with their straight neighbours, i.e. top, down, left, right neighbours,
     * if they exist within the grid boundaries and are not deleted.*/
    fun connectGridDefault() {
        connectGrid { getStraightNeighbours(it) }
    }

    /** Print the content of the grid, tile by tile, to the standard error stream*/
    override fun print() {
        val padding = nodes().maxOf { it.data.toString().length }
        nodes.forEachIndexed { id, t ->
            if (id > 0 && id % width == 0)
                System.err.println()
            System.err.print(String.format("%-${padding}s", t?.data ?: " "))
        }
        System.err.println()
    }
}


interface AdjacencyList {

    fun nodes(): IntArray
    fun neighbours(node:Int ): IntArray
    fun edges(node: Int): Edges
    fun forEachNeighbour(node: Int, action: (Int) -> Unit)
    fun forEachEdge(node: Int, action: (Double, Int) -> Unit)
    fun deepCopy(): AdjacencyList
    fun reversed(): AdjacencyList
    val size: Int
}



internal class FlattenedAdjacencyList(
    private val flattenedAdjacencyList: IntArray,
    private val starts: IntArray,
    private val ends: IntArray,
    private val flattenedWeights: DoubleArray,
) : AdjacencyList {

    override fun nodes() = IntArray(size) { it }
    override fun neighbours(node: Int): IntArray {
        val start = starts[node]
        val end = ends[node]
        return IntArray(end - start) { flattenedAdjacencyList[start + it] }
    }

    override fun edges(node: Int): Edges {
        val start = starts[node]
        val end = ends[node]
        return MutableList(end - start) { Edge(flattenedWeights[start + it], flattenedAdjacencyList[start + it]) }
    }

    override fun forEachNeighbour(node: Int, action: (Int) -> Unit) {
        val start = starts[node]
        val end = ends[node]
        for (i in start until end) {
            action(flattenedAdjacencyList[i])
        }
    }

    override fun forEachEdge(node: Int, action: (Double, Int) -> Unit) {
        val start = starts[node]
        val end = ends[node]
        for (i in start until end) {
            action(flattenedWeights[i], flattenedAdjacencyList[i])
        }
    }

    override fun deepCopy(): AdjacencyList = FlattenedAdjacencyList(
        flattenedAdjacencyList.copyOf(),
        starts.copyOf(),
        ends.copyOf(),
        flattenedWeights.copyOf(),
    )

    override val size get() = starts.size
    override fun reversed(): AdjacencyList {
        val revStarts = IntArray(size)
        val revEnds = IntArray(size)
        flattenedAdjacencyList.forEach { revEnds[it]++ }
        for (i in 1 until size) revStarts[i] = revStarts[i - 1] + revEnds[i - 1]
        for (i in 0 until size) revEnds[i] += revStarts[i]

        val revAdjacencyList = IntArray(flattenedAdjacencyList.size)
        val revWeights = DoubleArray(flattenedWeights.size)
        val next = revStarts.copyOf()
        repeat(size) { u ->
            for (idx in starts[u] until ends[u]) {
                val v = flattenedAdjacencyList[idx]
                val at = next[v]++
                revAdjacencyList[at] = u
                revWeights[at] = flattenedWeights[idx]
            }
        }
        return FlattenedAdjacencyList(revAdjacencyList, revStarts, revEnds, revWeights)
    }
}


internal class NestedAdjacencyList(private val adjacencyList: MutableList<Edges>) : AdjacencyList {

    override fun nodes() = IntArray(adjacencyList.size) { it }
    override fun neighbours(node: Int): IntArray = adjacencyList[node].let { neighbours ->
        IntArray(neighbours.size) { i -> neighbours[i].second }
    }

    override fun edges(node: Int): Edges = adjacencyList[node]

    override fun forEachNeighbour(node: Int, action: (Int) -> Unit) {
        adjacencyList[node].forEach { (_, v) ->
            action(v)
        }
    }

    override fun forEachEdge(node: Int, action: (Double, Int) -> Unit) {
        adjacencyList[node].forEach { (w, v) ->
            action(w, v)
        }
    }

    override fun deepCopy() = NestedAdjacencyList(adjacencyList.map { it.toMutableList() }.toMutableList())
    override val size get() = adjacencyList.size
    override fun reversed(): AdjacencyList {
        val reversedAdjacencyList = MutableList<Edges>(adjacencyList.size) { mutableListOf() }
        adjacencyList.forEachIndexed { u, edges ->
            edges.forEach { (w, v) ->
                reversedAdjacencyList[v].add(Edge(w, u))
            }
        }
        return NestedAdjacencyList(reversedAdjacencyList)
    }
}



internal class BFS(private val graph: AdjacencyList) {
    fun bfs(
        startIds: List<Int>,
        targetId: Int = -1,
        previousSearchResult: GraphSearchResults? = null,
    ): GraphSearchResults {
        val r = previousSearchResult ?: GraphSearchResults(graph.size)
        r.currentVisited.clear()
        val queue = ArrayDeque<Int>()
        startIds.forEach {
            queue.add(it)
            r.unweightedDistances[it] = 0
        }
        while (queue.isNotEmpty() && !r.foundTarget) {
            val currentId = queue.removeFirst()
            if (r.visited[currentId])
                continue
            r.visited[currentId] = true
            r.currentVisited.add(currentId)

            val currentDistance = r.unweightedDistances[currentId]
            graph.forEachNeighbour(currentId) { v ->
                val newDistance = currentDistance + 1
                if ((!r.visited[v] && newDistance < r.unweightedDistances[v]) || v == targetId) {
                    r.parents[v] = currentId
                    r.depth = newDistance.coerceAtLeast(r.depth)
                    r.unweightedDistances[v] = newDistance
                    if (v == targetId) {
                        r.foundTarget = true
                    }
                    queue.add(v)
                }
            }
        }
        r.processedOrder = r.currentVisited
        return r
    }
}



internal class DFS(private val graph: AdjacencyList) {
    private var r = GraphSearchResults(graph.size)

    /*fun dfsDeep( // Does not work with forEachNeighbour
        start: Int,
        initialSearchResults: GraphSearchResults? = null,
    ): GraphSearchResults {
        r = initialSearchResults ?: GraphSearchResults(graph.size)
        r.currentVisited = mutableListOf()
        var currentDepth = 0
        DeepRecursiveFunction<Int, Unit> { id ->
            if (r.visited[id]) return@DeepRecursiveFunction
            r.visited[id] = true
            r.currentVisited.add(id)
            r.depth = (++currentDepth).coerceAtLeast(r.depth)
            graph.forEachNeighbour(id) { v ->
                r.parents[v] = id
                this.callRecursive(v)
            }
            r.processedOrder.add(id)
            currentDepth-- //Done with this node. Backtracking to previous one.
        }.invoke(start)
        return r
    } */

    fun dfs(
        start: Int,
        initialSearchResults: GraphSearchResults? = null,
    ): GraphSearchResults {
        r = initialSearchResults ?: GraphSearchResults(graph.size)
        r.currentVisited = mutableListOf()
        var currentDepth = 0

        fun visit(id: Int) {
            if (r.visited[id]) return
            r.visited[id] = true
            r.currentVisited.add(id)
            r.depth = (++currentDepth).coerceAtLeast(r.depth)
            graph.forEachNeighbour(id) { v ->
                r.parents[v] = id
                visit(v)
            }
            r.processedOrder.add(id)
            currentDepth-- // Done with this node. Backtracking to previous one.
        }

        visit(start)
        return r
    }

    fun stronglyConnectedComponents(deleted: BooleanArray = BooleanArray(graph.size)): IntComponents {
        val topologicalOrder = DFS(graph.reversed()).topologicalSort(deleted).reversed()
        val stronglyConnectedComponents: IntComponents = topologicalOrder.mapNotNull { id ->
            if (r.visited[id]) null
            else {
                dfs(id, r)
                r.currentVisited
            }
        }
        return stronglyConnectedComponents
    }

    fun topologicalSort(deleted: BooleanArray = BooleanArray(graph.size)): List<Int> {
        for (i in 0 until graph.size) {
            if (deleted[i]) continue
            dfs(i, r)
        }
        return r.processedOrder//.reversed() //Reversed depending on the order
    }
}


internal class Dijkstra(private val graph: AdjacencyList) {
    private var r = GraphSearchResults(graph.size)
    fun dijkstra(start: Int, previousSearchResults: GraphSearchResults? = null): GraphSearchResults {
        r = previousSearchResults ?: GraphSearchResults(graph.size)
        r.weightedDistances[start] = 0.0
        val pq = PriorityQueue<Edge> { a, b -> a.first.compareTo(b.first) }
        pq.add(Edge(0.0, start))
        while (pq.isNotEmpty()) {
            val u = pq.poll().second
            if (r.visited[u]) continue
            r.visited[u] = true
            r.currentVisited.add(u)
            graph.forEachEdge(u){ d, v ->
                val newDistance = r.weightedDistances[u] + d
                if (newDistance < r.weightedDistances[v]) {
                    r.weightedDistances[v] = newDistance
                    r.parents[v] = u
                    if (!r.visited[v]) {
                        pq.add(Edge(newDistance, v))
                    }
                }
            }
        }
        r.processedOrder = r.currentVisited
        return r
    }
}


internal class FloydWarshall(val graph: AdjacencyList) {
    val n = graph.size
    private val distances = Array(n) { DoubleArray(n) { Double.POSITIVE_INFINITY } }

    init {
        graph.nodes().forEachIndexed { u, node ->
            distances[u][u] = 0.0
            graph.forEachEdge(node){ d, v ->
                distances[u][v] = d
            }
        }
    }

    fun floydWarshall(): Array<DoubleArray> {
        repeat(n) { k ->
            repeat(n) { i ->
                repeat(n) { j ->
                    distances[i][j] = min(distances[i][j], distances[i][k] + distances[k][j])
                }
            }
        }
        return distances
    }
    fun printDistances() {
        distances.forEachIndexed { i, row ->
            System.err.println("$i: ${row.joinToString(", ")}")
        }
    }
}

internal data class GraphSearchResults(private val graphSize: Int) {
    val visited = BooleanArray(graphSize)
    val unweightedDistances: IntArray = IntArray(graphSize) { Int.MAX_VALUE }
    val weightedDistances: DoubleArray = DoubleArray(graphSize) { Double.POSITIVE_INFINITY }
    val parents: IntArray = IntArray(graphSize) { -1 }
    var depth: Int = 0
    var currentVisited = mutableListOf<Int>()
    var processedOrder = mutableListOf<Int>()
    var foundTarget = false
    val distances:DoubleArray
    get() = if (weightedDistances.any { it != Double.POSITIVE_INFINITY }) {
        weightedDistances
    } else {
        unweightedDistances.map(Int::toDouble).toDoubleArray()
    }
}


private fun memoDfs(currentId: Int, target: Int, graph: AdjacencyList, memo: LongArray, mod: Long): Long {
    if (currentId == target)
        return 1L
    if (memo[currentId] != -1L)
        return memo[currentId]
    var totalPaths = 0L
     graph.forEachNeighbour(currentId){ neighbour ->
        totalPaths = (totalPaths + memoDfs(neighbour, target, graph, memo, mod)) % mod
    }
    memo[currentId] = totalPaths % mod
    return totalPaths
}
internal fun nrOfPaths(graph: AdjacencyList, start: Int, target: Int, mod: Long): Long {
    val memo = LongArray(graph.size) { -1L }
    return memoDfs(start, target, graph, memo, mod)
}


internal fun prims(graph: AdjacencyList): Pair<Double, MutableList<Edges>> {
    if (graph.size == 0) error("The graph is empty. Cannot do minimumSpanningTree")

    val visited = BooleanArray(graph.size)
    val connections = MutableList<Edges>(graph.size) { mutableListOf() }
    val pq = PriorityQueue<Triple<Double, Int, Int>> { a, b -> a.first.compareTo(b.first) }
    var totalWeight = 0.0

    visited[0] = true
    graph.forEachEdge(0) { weight, to ->
        pq.add(Triple(weight, 0, to))
    }
    var c = 0
    while (c < graph.size - 1) {
        if (pq.isEmpty()) error("The graph is not fully connected. Cannot do minimumSpanningTree")
        val (w, u, v) = pq.poll()
        if (visited[v]) continue
        visited[v] = true
        c++
        totalWeight += w

        connections[u].add(Edge(w, v))
        connections[v].add(Edge(w, u))

        graph.forEachEdge(v) { weight, next ->
            if (!visited[next]) {
                pq.add(Triple(weight, v, next))
            }
        }
    }

    return Pair(totalWeight, connections)
}


@JvmField
internal var INPUT: InputStream = System.`in` // Override this in tests to read from files instead

@JvmField
internal var _reader = INPUT.bufferedReader()
internal fun readLine(): String? = _reader.readLine()
internal fun readLines(): List<String> = generateSequence { readLine() }.toList()
internal fun readString() = _reader.readLine()

@JvmField
internal var _tokenizer: StringTokenizer = StringTokenizer("")
internal fun read(): String {
    while (_tokenizer.hasMoreTokens().not()) _tokenizer = StringTokenizer(_reader.readLine() ?: return "", " ")
    return _tokenizer.nextToken()
}

internal fun readInt() = read().toInt()
internal fun readDouble() = read().toDouble()
internal fun readLong() = read().toLong()
internal fun readStrings(n: Int) = List(n) { read() }
internal fun readLines(n: Int) = List(n) { readString() }
internal fun readInts(n: Int) = List(n) { read().toInt() }
internal fun readIntArray(n: Int) = IntArray(n) { read().toInt() }
internal fun readDoubles(n: Int) = List(n) { read().toDouble() }
internal fun readDoubleArray(n: Int) = DoubleArray(n) { read().toDouble() }
internal fun readLongs(n: Int) = List(n) { read().toLong() }
internal fun readLongArray(n: Int) = LongArray(n) { read().toLong() }
internal fun debug(x: Any) = System.err.println("DEBUG: $x")
