import java.io.InputStream
import java.util.ArrayDeque
import java.util.PriorityQueue
import java.util.StringTokenizer
import kotlin.math.min

/** Edge has a weight w to a destination node v */
internal typealias Edge = Pair<Double, Int>
/** List of edges */
internal typealias Edges = MutableList<Edge>
/** Used to represent a weighted graph */
internal typealias AdjacencyList = MutableList<Edges>
/** Used to represent an unweighted graph */
internal typealias UnweightedAdjacencyList = MutableList<MutableList<Int>>

/** List of list of nodes */
internal typealias Components = List<List<Any>>
/** List of list of integer nodes */
internal typealias IntComponents = List<List<Int>>

/** Replaces the edges with just the destination nodes */
internal fun AdjacencyList.toUnweightedAdjacencyList() = map { edges -> edges.map { it.second }.toMutableList() }.toMutableList()
/** Replaces the destination nodes with edges of weight 1.0 */
internal fun UnweightedAdjacencyList.toWeightedAdjacencyList() =
    map { edges -> edges.map { 1.0 to it }.toMutableList() }.toMutableList()
/** Returns a new, independent, identical AdjacencyList */
internal fun AdjacencyList.deepCopy() = map { it.toMutableList() }.toMutableList()

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
abstract class BaseGraph<T : Any>(size: Int, private val isWeighted: Boolean = true) {
    // PROPERTIES AND INITIALIZATION
    protected val adjacencyList: AdjacencyList = MutableList(size) { mutableListOf() }
    protected var unweightedAdjacencyList: UnweightedAdjacencyList = MutableList(size) { mutableListOf() }

    protected var nodes: MutableList<T?> = MutableList(size) { null }
    private var searchResults: GraphSearchResults? = null
    private var finalPath: List<T>? = null
    private var allDistances: Array<DoubleArray>? = null

    // ABSTRACT FUNCTIONS
    /** Adds the given node to the graph
     * @param node The node to add */
    abstract fun addNode(node: T)
    protected abstract fun addWeightedEdge(node1: T, node2: T, weight: Double)
    protected abstract fun addUnweightedEdge(node1: T, node2: T)
    protected abstract fun node2Id(node: T): Int?
    protected abstract fun id2Node(id: Int): T?

    /** @return the nodes in the graph */
    abstract fun nodes(): List<T>

    // CORE GRAPH OPERATIONS
    /** Adds an edge between two nodes in the graph, and creates the nodes if they don't exist.
     *
     * If the graph is unweighted, the edge is added without a weight. If a weight is provided
     * for an unweighted graph, an error is thrown. If the graph is weighted, a weight must be provided;
     * otherwise, an error is thrown.
     *
     * @param node1 The starting node of the edge.
     * @param node2 The ending node of the edge.
     * @param weight The weight of the edge (required for weighted graphs). Defaults to `null`.
     * @throws IllegalStateException If a weight is provided for an unweighted graph or if no weight
     * is provided for a weighted graph. */
    fun addEdge(node1: T, node2: T, weight: Number? = null) {
        if (!isWeighted) {
            if (weight != null)
                error(
                    "A weight cannot be given to a unweighted graph. Don't provide a weight or make the graph " +
                            "unweighted by setting the parameter isWeighted=true when creating it."
                )
            addUnweightedEdge(node1, node2)
        } else if (weight == null) error(
            "A weight must be provided when adding edges in a weighted graph. " +
                    "To make the graph unweighted, set the parameter isWeighted=false when creating it."
        )
        else addWeightedEdge(node1, node2, weight.toDouble())
    }

    /** @return The total number of nodes in the graph. */
    fun size() = nodes().size

    /** Connects two nodes in the graph, by calling addEdge(node1,node2, weight) and addEdge(node2, node1, weight)
     *
     * @param node1 The first node to connect.
     * @param node2 The second node to connect.
     * @param weight The weight of the connection (required for weighted graphs). Defaults to `null`.
     * @throws IllegalStateException If no weight is provided for a weighted graph. */
    fun connect(node1: T, node2: T, weight: Number? = null) {
        addEdge(node1, node2, weight)
        addEdge(node2, node1, weight)
    }

    /** Removes the edge(s) between two nodes in the graph.
     *
     * @param node1 The first node of the edge to remove.
     * @param node2 The second node of the edge to remove.
     * @throws IllegalStateException If either node is not found in the graph. */
    fun removeEdge(node1: T, node2: T) {
        val u = node2Id(node1) ?: error("Node '$node1' not found in graph")
        val v = node2Id(node2) ?: error("Node '$node2' not found in graph")
        removeEdge(u, v)
    }

    private fun removeEdge(id1: Int, id2: Int) {
        if (isWeighted) adjacencyList[id1].removeAll { it.second == id2 }
        else unweightedAdjacencyList[id1].remove(id2)
    }

    // GRAPH INFORMATION
    /** Retrieves the depth of the graph from the most recent search operation
     *
     * Depth is defined as the deepest level of recursion or the maximum distance from the starting node to
     * any other node during the search traversal if the weights are counted as 1.
     * @return The depth of the graph.
     * @throws IllegalStateException If neither DFS nor BFS has been run yet.*/
    fun depth() =
        searchResults?.depth ?: error("Can't retrieve depth because no search (DFS, BFS, Dijkstra) has been run yet")

    /** Retrieves a list of all visited nodes on the order they were visited during the last search operation (DFS, BFS, Dijkstra).
     *
     * @return A list of visited nodes. or an empty list if no search algorithm (DFS, BFS, Dijkstra) has been run yet. */
    fun currentVisitedNodes(): List<T> =
        searchResults?.currentVisited?.map { id2Node(it)!! }
            ?: emptyList()


    /** Retrieves a (unordered) list of all visited nodes during any non-reset search operation (DFS, BFS, Dijkstra).
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
    fun distanceTo(node: T) =
        if (isWeighted) distanceWeightedTo(node)
        else distanceUnweightedTo(node).toDouble()

    private fun distanceWeightedTo(node: T): Double {
        val id = node2Id(node) ?: error("Node '$node' not found in graph")
        searchResults?.let {
            return it.distances[id]
        }
        error("Haven't computed distance to '$node' because neither BFS nor Dijkstra  has been run yet.")
    }

    private fun distanceUnweightedTo(node: T): Int {
        val id = node2Id(node) ?: error("Node $node not found in graph")
        searchResults?.let {
            return it.unweightedDistances[id]
        }
        error("Haven't computed distance to $node because neither BFS nor Dijkstra has been run yet.")
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
     * If the graph is unweighted, a default weight of `1.0` is assigned to each edge.
     *
     * @param t The node whose edges are to be retrieved.
     * @return A list of pairs representing the edges connected to the node.
     * @throws IllegalStateException If the specified node is not found in the graph. */
    fun edges(t: T): List<Pair<Double, T>> = if (isWeighted) weightedEdges(t)
    else neighbours(t).map { 1.0 to it }

    private fun weightedEdges(t: T): List<Pair<Double, T>> =
        node2Id(t)?.let { adjacencyList[it] }?.map { Pair(it.first, id2Node(it.second)!!) }
            ?: error("Node $t not found in graph")

    /** Retrieves a list the neighboring nodes of the specified node.
     *
     * @param t The node whose neighbors are to be retrieved.
     * @return A list of neighboring nodes connected to the specified node.
     * @throws IllegalStateException If the specified node is not found in the graph. */
    fun neighbours(t: T): List<T> =
        if (isWeighted) weightedEdges(t).map { it.second }
        else node2Id(t)?.let { unweightedAdjacencyList[it] }
            ?.map { id2Node(it)!! }
            ?: error("Node '$t' not found in graph")

    // SEARCH ALGORITHMS

    /** Performs a Breadth-First Search, which finds the shortest path from the starting node to all other nodes,
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
        useWeightedConnectionsIfNeeded("bfs")
        val startNodeIds = startNodes.map { node -> node2Id(node) ?: error("Node '$node' not found in graph") }
        val targetId = target?.let { node2Id(it) } ?: -1
        if (reset) searchResults = null
        searchResults = BFS(unweightedAdjacencyList).bfs(startNodeIds, targetId, searchResults)
        target?.let {
            finalPath = getPath(it)
        }
    }

    /** Overload of fun bfs(startNodes: List<T>, target: T?, reset: Boolean) that accepts a single starting node istead of a list
     * @returnRuns bfs(listOf(startNode), target, reset) */
    fun bfs(startNode: T, target: T? = null, reset: Boolean = true) = bfs(listOf(startNode), target, reset)

    /** Performs a Depth-First Search on the graph which finds all nodes that's reachable from the starting node it.
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
        val startId = node2Id(startNode) ?: error("Node '$startNode' not found in graph")
        if (reset) searchResults = null
        useWeightedConnectionsIfNeeded("dfs")
        searchResults = DFS(unweightedAdjacencyList).dfs(startId, searchResults)
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
        if (nrOfConnections(adjacencyList) == 0) {
            System.err.println("Warning: The adjacently list has no connections, making pathfinding infeasible.")
            if (nrOfConnections(unweightedAdjacencyList) != 0) {
                System.err.println("The graph does have unweighted connections. Executing BFS instead.")
                bfs(startNode, target)
                return
            }
        }
        val startId = node2Id(startNode) ?: error("Node '$startNode' not found in graph")
        searchResults = Dijkstra(adjacencyList).dijkstra(startId, searchResults)
        target?.let {
            finalPath = getPath(it)
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
        if (nrOfConnections(adjacencyList) == 0) {
            System.err.println("Warning: The graph  has no weighted connections, making pathfinding infeasible.")
            if (nrOfConnections(unweightedAdjacencyList) != 0) {
                System.err.println("The graph does have unweighted connections. Using them for Floyd-Warshall.")
                allDistances = FloydWarshall(unweightedAdjacencyList.toWeightedAdjacencyList()).floydWarshall()
            }
        }
        else {
            allDistances = FloydWarshall(adjacencyList).floydWarshall()
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
    } ?: error("FloydWarshall must be run before calling distanceFromUtoV")

    // ADDITIONAL ALGORITHMS
    /** Computes the Minimum Spanning Tree (MST) of the graph using Prim's algorithm.
     *
     * If the graph is unweighted, it is first converted to a weighted graph with default edge weights.
     *
     * @return A pair containing the total weight of the MST and the graph representing the MST.
     * @throws IllegalStateException If the graph is empty or not fully connected. */
    fun minimumSpanningTree(): Pair<Double, Graph> =
        (if (isWeighted) adjacencyList else unweightedAdjacencyList.toWeightedAdjacencyList()).let { graph ->
            return prims(graph).run {
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
        }

    /** Builds an order of nodes so that the first nodes has no outgoing edges, then nodes with edges pointing to these
     * and so on, assuming the graph is a Directed Acyclic Graph (DAG). This is done by running a DFS from each node
     * and ordering the nodes by descending depth (post-order).
     *
     * @return A list of nodes in topological order if the graph was a DAG.
     * Otherwise, returns a list of nodes in undefined order */
    open fun topologicalSort(): List<T> {
        val dfsGraph = if (isWeighted) adjacencyList.toUnweightedAdjacencyList() else unweightedAdjacencyList
        return DFS(dfsGraph).topologicalSort().map { id2Node(it)!! }
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
        useWeightedConnectionsIfNeeded("stronglyConnectedComponents")
        val scc = DFS(unweightedAdjacencyList).stronglyConnectedComponents()
        return scc.map { component -> component.map { id2Node(it)!! } }
    }

    /** Calculates the number of distinct paths from the starting node to the target node in the graph.
     *
     * This function assumes that the graph is a Directed Acyclic Graph (DAG) and uses dynamic programming
     * to count the number of paths efficiently.
     *
     * @param startNode The starting node.
     * @param targetNode The target node.
     * @return The number of distinct paths from the starting node to the target node.
     * @throws IllegalStateException If either the starting node or the target node is not found in the graph. */
    fun nrOfPaths(startNode: T, targetNode: T, mod:Long = Long.MAX_VALUE): Long{
        useWeightedConnectionsIfNeeded("nrOfPaths")
        val startId = node2Id(startNode) ?: error("Node '$startNode' not found in graph")
        val targetId = node2Id(targetNode) ?: error("Node '$targetNode' not found in graph")
        return nrOfPaths(unweightedAdjacencyList, startId, targetId, mod)
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
     * The following functions rely on the `searchResults` property, which is populated by running a search algorithm
     * (DFS, BFS, or Dijkstra). These functions will throw an `IllegalStateException` if no search are run after calling this function:
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
        searchResults = GraphSearchResults(nodes.size)
    }

    private fun useWeightedConnectionsIfNeeded(algorithmName: String) {
        if (nrOfConnections(unweightedAdjacencyList) == 0) {
            unweightedAdjacencyList = adjacencyList.toUnweightedAdjacencyList()
            if (nrOfConnections(unweightedAdjacencyList) == 0) {
                System.err.println("Warning: The graph has no connections, making pathfinding infeasible.")
            } else {
                System.err.println(
                    "Warning: $algorithmName requires an unweighted graph.Building one from the weighted edges."
                )
            }
        }
    }

    protected fun <T> nrOfConnections(twoDList: List<List<T>>) = twoDList.sumOf { it.size }

    // PRINTER FUNCTIONS
    /** Prints the graph's adjacency list to the standard error stream.
     *
     * If the graph is weighted, it prints the weighted adjacency list, where each connection is represented
     * as a pair of the edge weight and the connected node. If the graph is unweighted, it prints the unweighted
     * adjacency list, where each connection is represented by the connected node.
     *
     * @param isWeighted A boolean indicating whether to print the weighted or unweighted adjacency list. */
    fun print(isWeighted: Boolean) =
        if (isWeighted) {
            adjacencyList.forEachIndexed { nodeId, connections ->
                System.err.println("${id2Node(nodeId)} ---> ${connections.map { "(${it.first}, ${id2Node(it.second)})" }}")
            }
        } else {
            unweightedAdjacencyList.forEachIndexed { nodeId, connections ->
                System.err.println("${id2Node(nodeId)} ---> ${connections.map { id2Node(it) }}")
            }
        }
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
 * @param isWeighted Indicates whether it uses weighted or unweighted edges. Traversal algorithms like BFS and DFS
 * operate on unweighted graphs, while minimum cost algorithms like Dijkstra, Floyd Warshall and Prims are based on weighted edges */
class Graph(isWeighted:Boolean=true): BaseGraph<Any>(0, isWeighted) {
    private var nrOfNodes = 0
    private val node2id = mutableMapOf<Any, Int>()
    private val id2Node = mutableMapOf<Int, Any>()

    private fun getOrAddNodeId(node: Any): Int {
        return node2id[node] ?: addNode(node).run { node2id[node]!! }
    }

    override fun addNode(node: Any) {
        if (node2id.containsKey(node)) {
            //System.err.println("Warning: The node already exists, it can't be added again")
            return
        }
        nodes.add(node)
        node2id[node] = nrOfNodes
        id2Node[nrOfNodes++] = node
        adjacencyList.add(mutableListOf())
        unweightedAdjacencyList.add(mutableListOf())
    }

    override fun addWeightedEdge(node1: Any, node2: Any, weight: Double) {
        val id1 = getOrAddNodeId(node1)
        val id2 = getOrAddNodeId(node2)
        adjacencyList[id1].add(weight to id2)
    }

    override fun addUnweightedEdge(node1: Any, node2: Any) {
        val id1 = getOrAddNodeId(node1)
        val id2 = getOrAddNodeId(node2)
        unweightedAdjacencyList[id1].add(id2)
    }

    override fun node2Id(node: Any): Int? = node2id[node]


    override fun id2Node(id: Int): Any? = id2Node[id]


    override fun nodes(): List<Any> = id2Node.values.toList()
    override fun toString(): String {
        return buildString {
            adjacencyList.forEachIndexed { id, edges ->
                val edgeString = edges.joinToString { id2Node(it.second).toString() }
                append("${id2Node(id)} ----> [$edgeString]\n")
            }
        }
    }
}

/** A specialized graph class that represent integer nodes ranging from 0 to size-1.
 *
 * The IntGraph class behaves a lot like the Graph class when used with integers like the example above.
 * However, * it's more performant, because it does not need to maintain an internal mapping between the nodes and their
 * indexes in the adjacency list. The obvious drawback being it only supports integer nodes.
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
 * @param size The number of nodes in the graph. Nodes are represented as integers from 0 to size-1. This cannot be altered later
 * @param isWeighted Indicates whether the graph uses weighted or unweighted edges. */
class IntGraph(size: Int, isWeighted: Boolean = true) : BaseGraph<Int>(size, isWeighted) {
    init {
        repeat(size) {
            nodes[it] = it
        }
    }

    override fun addNode(node: Int) =
        error("IntGraph doesn't support addNode(), because nodes are defined by the IntGraph size")

    override fun addWeightedEdge(node1: Int, node2: Int, weight: Double) {
        adjacencyList[node1].add(weight to node2)
    }

    override fun addUnweightedEdge(node1: Int, node2: Int) {
        unweightedAdjacencyList[node1].add(node2)
    }

    override fun id2Node(id: Int) = id
    override fun node2Id(node: Int) = node
    override fun nodes(): List<Int> = adjacencyList.indices.toList()

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
 * @param initWithDatalessTiles If `true`, initializes the grid with empty tiles.
 * @param isWeighted Indicates whether the grid uses weighted or unweighted edges. */
class Grid(val width: Int, val height: Int, isWeighted: Boolean = false, initWithDatalessTiles: Boolean = true) :
    BaseGraph<Tile>(width * height, isWeighted) {
    /** Construct the grid from a list of strings, where each string represents a row in the grid.
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
    }

    override fun node2Id(node: Tile) = node.x + node.y * width

    override fun id2Node(id: Int) = if (id in 0 until width * height) nodes[id] else null

    override fun addWeightedEdge(node1: Tile, node2: Tile, weight: Double) {
        val u = node2Id(node1)
        val v = node2Id(node2)
        adjacencyList[u].add(Edge(weight, v))
    }

    override fun addUnweightedEdge(node1: Tile, node2: Tile) {
        val u = node2Id(node1)
        val v = node2Id(node2)
        unweightedAdjacencyList[u].add(v)
    }

    override fun nodes(): List<Tile> = nodes.filterNotNull()
    override fun topologicalSort() = DFS(unweightedAdjacencyList).topologicalSort(deleted()).map { id2Node(it)!! }
    override fun stronglyConnectedComponents() = DFS(unweightedAdjacencyList).stronglyConnectedComponents(deleted())
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
        if (nrOfConnections(unweightedAdjacencyList) > 0) {
            System.err.println("Warning: overwriting existing connections in the grid")
            adjacencyList.forEach { it.clear() }
            unweightedAdjacencyList.forEach { it.clear() }
        }
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
    }

    /** Connects all nodes in the grid with their straight neighbours, i.e. top, down, left, right neighbours,
     * if they exist within the grid boundaries and are not deleted.*/
    fun connectGridDefault() {
        connectGrid { getStraightNeighbours(it) }
    }

    /** Print the content of the grid, tile by tile, to the standard error stream*/
    fun print() {
        val padding = nodes().maxOf { it.data.toString().length }
        nodes.forEachIndexed { id, t ->
            if (id > 0 && id % width == 0)
                System.err.println()
            System.err.print(String.format("%-${padding}s", t?.data ?: " "))
        }
        System.err.println()
    }
}


internal class BFS(private val graph: UnweightedAdjacencyList) {
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
        while (queue.isNotEmpty()) {
            val currentId = queue.removeFirst()
            if (r.visited[currentId])
                continue
            r.visited[currentId] = true
            r.currentVisited.add(currentId)

            val currentDistance = r.unweightedDistances[currentId]
            graph[currentId].forEach { v ->
                val newDistance = currentDistance + 1
                if ((!r.visited[v] && newDistance < r.unweightedDistances[v]) || v == targetId) {
                    r.parents[v] = currentId
                    r.depth = newDistance.coerceAtLeast(r.depth)
                    r.unweightedDistances[v] = newDistance
                    if (v == targetId){
                        r.foundTarget = true
                        return r
                    }
                    queue.add(v)
                }
            }
        }
        r.processedOrder = r.currentVisited
        return r
    }
}



internal class DFS(private val graph: UnweightedAdjacencyList) {
    private var r = GraphSearchResults(graph.size)

    fun dfs(
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
            graph[id].forEach { v ->
                r.parents[v] = id
                this.callRecursive(v)
            }
            r.processedOrder.add(id)
            currentDepth-- //Done with this node. Backtracking to previous one.
        }.invoke(start)
        return r
    }

    fun stronglyConnectedComponents(deleted:BooleanArray = BooleanArray(graph.size)): IntComponents {
        val reversedGraph: UnweightedAdjacencyList =
            MutableList<MutableList<Int>>(graph.size) { mutableListOf() }.apply {
                graph.forEachIndexed { u, neighbors ->
                    neighbors.forEach { v ->
                        this[v].add(u)
                    }
                }
            }
        val topologicalOrder = DFS(reversedGraph).topologicalSort(deleted).reversed()
        val stronglyConnectedComponents: IntComponents = topologicalOrder.mapNotNull { id ->
            if (r.visited[id]) null
            else {
                dfs(id, r)
                r.currentVisited
            }
        }
        return stronglyConnectedComponents
    }

    fun topologicalSort(deleted:BooleanArray = BooleanArray(graph.size)): List<Int> {
        for (i in 0 until graph.size) {
            if(deleted[i]) continue
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
            graph[u].forEach { (d, v) ->
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
        graph.forEachIndexed { u, edges ->
            distances[u][u] = 0.0
            edges.forEach { (d, v) ->
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


private fun memoDfs(currentId: Int, target: Int, graph: UnweightedAdjacencyList, memo: LongArray, mod: Long): Long {
    if (currentId == target)
        return 1L
    if (memo[currentId] != -1L)
        return memo[currentId]
    var totalPaths = 0L
     graph[currentId].forEach { neighbour ->
        totalPaths = (totalPaths + memoDfs(neighbour, target, graph, memo, mod)) % mod
    }
    memo[currentId] = totalPaths % mod
    return totalPaths
}
internal fun nrOfPaths(graph: UnweightedAdjacencyList, start: Int, target: Int, mod: Long): Long {
    val memo = LongArray(graph.size) { -1L }
    return memoDfs(start, target, graph, memo, mod)
}


internal fun prims(graph: AdjacencyList): Pair<Double, AdjacencyList> {
    if (graph.size == 0) error("The graph is empty. Cannot do minimumSpanningTree")

    val visited = BooleanArray(graph.size)
    val connections: AdjacencyList = MutableList(graph.size) { mutableListOf() }
    val pq = PriorityQueue<Triple<Double, Int, Int>> { a, b -> a.first.compareTo(b.first) }
    var totalWeight = 0.0

    visited[0] = true
    graph[0].forEach { (weight, to) ->
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

        graph[v].forEach { (weight, next) ->
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