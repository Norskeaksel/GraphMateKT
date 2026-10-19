import kotlin.system.measureTimeMillis
import java.util.*
import kotlin.math.min
import java.io.InputStream

/** Edge has a weight w to a destination node v */
internal typealias Edge = Pair<Double, Int>
/** List of list of nodes */
typealias Components = List<List<Any>>
/** List of list of integer nodes */
typealias IntComponents = List<List<Int>>
/** List of list of Tile nodes */
typealias GridComponents = List<List<Tile>>

private const val INITIAL_CAPACITY = 10

internal class IntArrayList {
    private var intArray: IntArray = IntArray(INITIAL_CAPACITY)
    var size: Int = 0
        private set

    fun add(value: Int) {
        if (size >= intArray.size) {
            intArray = intArray.copyOf((size + 1) * 2)
        }
        intArray[size] = value
        size++
    }

    operator fun get(i: Int) = intArray[i]
    fun intArray(): IntArray {
        if (intArray.size > size) {
            intArray = intArray.copyOf(size)
        }
        return intArray
    }

    fun asList() = intArray.copyOf(size).asList()

    fun clear() {
        intArray = IntArray(INITIAL_CAPACITY)
        size = 0
    }

    fun reversed() = IntArrayList().apply {
        for (i in size - 1 downTo 0) {
            add(intArray[i])
        }
    }
}

internal class DoubleArrayList {
    private var doubleArray: DoubleArray = DoubleArray(INITIAL_CAPACITY)
    var size: Int = 0
        private set

    fun add(value: Double) {
        if (size >= doubleArray.size) {
            doubleArray = doubleArray.copyOf((size + 1) * 2)
        }
        doubleArray[size] = value
        size++
    }

    operator fun get(i: Int) = doubleArray[i]
    fun doubleArray(): DoubleArray {
        if (doubleArray.size > size) {
            doubleArray = doubleArray.copyOf(size)
        }
        return doubleArray
    }
}

internal class UnboxedEdges {
    lateinit var from: IntArrayList
    lateinit var to: IntArrayList
    lateinit var weights: DoubleArrayList
    val size get() = from.size

    init {
        clear()
    }

    fun addEdge(u: Int, v: Int, weight: Double) {
        from.add(u)
        to.add(v)
        weights.add(weight)
    }

    fun addEdge(u: Int, v: Int) {
        addEdge(u, v, 1.0)
    }

    fun clear() {
        from = IntArrayList()
        to = IntArrayList()
        weights = DoubleArrayList()
    }

    fun boxedEdges() = List(size) { i -> Triple(from[i], to[i], weights[i]) }

    fun deepCopy(): UnboxedEdges {
        val copy = UnboxedEdges()
        for (i in 0 until size) {
            copy.addEdge(from[i], to[i], weights[i])
        }
        return copy
    }

    fun reversed(): UnboxedEdges {
        val reversed = UnboxedEdges()
        for (i in 0 until size) {
            reversed.addEdge(to[i], from[i], weights[i])
        }
        return reversed
    }
}

/** Represents a node in the Grid graph with x and y coordinates and optional data, which can be considered the node value
 *
 * @property x The x-coordinate of the tile.
 * @property y The y-coordinate of the tile.
 * @property data Optional data associated with the tile, which can be considered a node of any type */
data class Tile(val x: Int, val y: Int, val data: Any? = null) {
    /** Checks if the `data` property of the `Tile` is a `Char` and whether that `Char` represents a digit.
     *
     * @return `true` if `data` is a `Char` and is a digit, otherwise `false`. */
    fun dataIsDigit() = data is Char && data.isDigit()

    /** Calculates `x + y * width`
     *
     * Each tile in a 2D Grid with `n = width * height` elements can be uniquely represented as an integer from 0 to n - 1.
     * ID'ing tiles like this can be useful for associating additional information with them using arrays, instead of
     * less performant maps.
     * @param width The width of the grid the tile belongs to.
     * @return The unique integer id of the tile. */
    fun idGivenWidth(width: Int) = x + y * width
}

internal data class Not(val node: Any)

internal operator fun Any.not() = Not(this)

internal data class TrieNode(val children: MutableMap<Char, TrieNode> = mutableMapOf(), var isTerminal: Boolean = false)

internal class InvalidEdgeException(val uId: Int, val vId: Int) : Exception()


/** An abstract class that's used by the Graph, IntGraph and Grid classes for common functionality */
abstract class BaseGraph<T : Any>(
    initialNrOfNodes: Int,
    protected val debugTimeUse: Boolean,
    protected var isSparse: Boolean
) {
    // PROPERTIES AND INITIALIZATION
    internal var adjacencyList: AdjacencyList = AdjacencyListNested(0, UnboxedEdges())
    internal val edges = UnboxedEdges()
    protected var adjacencyListIsFinalized = false
    protected var edgesCount = 0
    protected var nrOfNodes = initialNrOfNodes

    protected var finalPath: List<T>? = null
    private var searchResults: GraphSearchResults? = null
    private var allDistances: Array<DoubleArray>? = null

    // ABSTRACT FUNCTIONS
    /** @return a list of the nodes in the graph. */
    abstract fun nodes(): List<T>


    /** Adds the given node to the graph
     * @param node The node to add */
    abstract fun addNode(node: T)

    /** Adds an edge between two nodes in the graph, and creates the nodes if they don't exist.
     *
     * @param node1 The starting node of the edge.
     * @param node2 The ending node of the edge.
     * @param weight The weight of the edge, for example used to calculate distances with shortestPathsFrom. Defaults to 1.0. */
    abstract fun addEdge(node1: T, node2: T, weight: Double = 1.0)

    /** @return a list of the Edges in the graph, where the edges are triples with: fromNode, toNode, weight */
    fun edges() = finalizeAdjacencyListIfNeeded(true).run {
        adjacencyList.edges().map { Triple(id2Node(it.first), id2Node(it.second), it.third) }
    }


    fun clearEdges() {
        edges.clear()
        edgesCount = 0
        adjacencyList = AdjacencyListNested(nrOfNodes, edges)
    }

    protected abstract fun node2IdOrNull(node: T): Int?
    protected abstract fun node2Id(node: T): Int
    protected abstract fun id2NodeOrNull(id: Int): T?
    protected abstract fun id2Node(id: Int): T


    /** Defines a rule for how each node is connected to its neighbors.
     *
     * This will configure edges and nodes to be lazily/dynamically added to the graph, based on the user defined
     * function `getNeighbours`. This is useful when the nodes or edges of the graph are not known/defined in advance,
     * or when the graph's state space is too large to be stored in memory effectively. The tradeoff is that searches
     * (bfs, dfs) will be less performant per iteration, compared to if the graph was fully defined in advance.
     *
     * DISCLAIMER: This will overwrite all existing edges (but not nodes) in the graph. If invoked from the Graph() class, it
     * will make the graph be considered sparse and create new nodes during traversal. If invoked from the IntGraph() or
     * Grid() class, it's on the user to make sure all reachable nodes are defined in the graph. Otherwise, the behavior
     * will be undefined. If edges are added after connectWithRule is invoked, all nodes 'defined' in the graph will be
     * connected according to the rule. Then the rule will be dropped and not considered in future searches.
     *
     * <i>Example usage (if invoked from a grid):<i>
     * ```
     * val grid = Grid(100,100, true)
     * grid.connectWithRule { t ->
     *     grid.getStraightNeighbours(t) + grid.getDiagonalNeighbours(t)
     * }
     * grid.bfs(Tile(50,50))
     * grid.visualizeGrid()
     * ```
     *
     * @param getNeighbours A user defined function that takes a `Node` as input and returns a list of neighboring `Nodes`
     */
    open fun connectWithRule(getNeighbours: (node: T) -> List<T>) {
        val time = measureTimeMillis {
            clearEdges()
            adjacencyList = AdjacecnyListDynamic(nrOfNodes, forEachNeighbourId(getNeighbours))
            adjacencyListIsFinalized = true
        }
        if (debugTimeUse) {
            debug("connectWithRule took $time ms.")
        }
    }

    protected fun finalizeAdjacencyListIfNeeded(fullGraphIsNeeded: Boolean) {
        if (adjacencyListIsFinalized && (!fullGraphIsNeeded || !adjacencyList.isDynamic))
            return
        if (adjacencyList.isDynamic) {
            if (isSparse && searchResults == null) {
                System.err.println("Warning: Trying to materialize edges on a sparse unexplored graph. (No search algorithm has been run yet). This might cause some defined edges to be lost.")
            }
            nodes().forEach { u ->
                val uId = node2Id(u)
                adjacencyList.forEachNeighbour(uId) { vId ->
                    edges.addEdge(uId, vId)
                    edgesCount++
                }
            }
        }
        adjacencyList = AdjacencyListFlattened(nrOfNodes, edges)
        adjacencyListIsFinalized = true
    }

    private fun forEachNeighbourId(getNeighbours: (node: T) -> List<T>): (Int, (Int) -> Unit) -> Unit {
        return { id, action ->
            val neighbours = getNeighbours(id2Node(id))
            for (i in neighbours.indices) {
                action(node2Id(neighbours[i]))
            }
        }
    }

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
     * any other node during the search traversal if the weights are counted as 1 and the starting depth is 0.
     * @return The depth of the graph.
     * @throws IllegalStateException If neither DFS nor BFS has been run yet.*/
    fun depth() =
        searchResults?.depth ?: error("Can't retrieve depth because the most recent search was not a DFS or BFS.")

    /** @return A list of visited nodes, ordered by when they were visited,
     *  or an empty list if no search algorithm (DFS, BFS, shortestPathsFrom) has been run yet. */
    fun currentVisitedNodes(): List<T> =
        searchResults?.currentVisited?.asList()?.mapNotNull { id2NodeOrNull(it) }
            ?: emptyList()


    /** @return A (unordered) list of visited nodes or an empty list if no search algorithm (DFS, BFS, shortestPathsFrom) has been run yet. */
    fun visitedNodes(): List<T> =
        searchResults?.run {
            if (isSparse)
                visitedSparse.map { id2Node(it) }
            else buildList(visited.size) {
                for (i in visited.indices)
                    if (visited[i])
                        add(id2Node(i))
            }
        }
            ?: emptyList()

    /** Retrieves the shortest path from the start to target node (if a target was specified) during the most recent
     *  search operation (DFS, BFS, shortestPathsFrom).
     *
     * @return A list of nodes representing the final path or null if no search algorithm (DFS, BFS, shortestPathsFrom) has been run yet or no path was found.
     * Note: Starting at a node, does not count as a path to that node */
    fun finalPath(): List<T>? = finalPath

    /** Checks if the target node was found during the most recent search operation (BFS, shortestPathsFrom).
     *
     * @return `true` if the target node was found, `false` otherwise. */
    fun foundTarget() = searchResults?.foundTarget ?: false

    /** Retrieves the distance to the specified node from the starting node of the most recent search operation (BFS, shortestPathsFrom).
     *
     * @param node The target node whose distance is to be retrieved.
     * @return The distance to the specified node, or Double.POSITIVE_INFINITY if no path to the node has been found. */
    fun distanceTo(node: T) = node2IdOrNull(node)?.let { id ->
        searchResults?.run {
            if (isSparse)
                distancesSparse[id]
            else
                distances[id]
        }
    } ?: Double.POSITIVE_INFINITY

    /** Retrieves the maximum distance from the starting node to any other node after the most recent search operation (BFS, shortestPathsFrom).
     *
     * If no search has been performed or a node cannot be reached, the function returns `Double.POSITIVE_INFINITY`.
     * @return The maximum distance to any node. */
    fun maxDistance(): Double {
        val maxDistance: Double
        val time = measureTimeMillis {
            maxDistance = nodes().maxOfOrNull { distanceTo(it) } ?: Double.POSITIVE_INFINITY
        }
        if (debugTimeUse) {
            debug("maxDistance took $time ms.")
        }
        return maxDistance
    }

    /** Retrieves the node that is the farthest from the starting node in the most recent search operation (BFS, shortestPathsFrom).
     *
     * @return The node that is the farthest from the starting node. It can be unreachable.
     * @throws IllegalStateException If no search algorithm (bfs, dfs, shortestPathsFrom) has been executed yet. */
    fun furthestNode(): T {
        val node: T
        val time = measureTimeMillis {
            if (searchResults == null) {
                error("Cannot compute furthest node because no search algorithm (bfs, dfs, shortestPathsFrom) has been run yet.")
            }
            val maxDistance = maxDistance()
            node = nodes().first { distanceTo(it) == maxDistance }
        }
        if (debugTimeUse) {
            debug("furthestNode took $time ms.")
        }
        return node
    }

    /** Retrieves a list of edges connected to the specified node.
     *
     * Each edge is represented as a pair, where the first element is the weight of the edge
     * (as a `Double`), and the second element is the connected node.
     *
     * @param node The node whose edges are to be retrieved.
     * @return A list of pairs representing the edges connected to the node.
     * @throws IllegalStateException If the specified node is not found in the graph. */
    fun edges(node: T): List<Pair<Double, T>> {
        val edges: List<Pair<Double, T>>
        val time = measureTimeMillis {
            finalizeAdjacencyListIfNeeded(true).run {
                val u = node2IdOrNull(node) ?: error("'$node' not found in graph")
                val neighbours = adjacencyList.neighbours(u)
                val weights = adjacencyList.weights(u)
                edges = ArrayList(neighbours.size)
                for (i in neighbours.indices) {
                    val v =
                        id2NodeOrNull(neighbours[i]) ?: error("Node with ID ${neighbours[i]} not found in graph")
                    edges.add(weights[i] to v)
                }
            }
        }
        if (debugTimeUse) {
            debug("edges took $time ms.")
        }
        return edges
    }

    /** Retrieves a list of the neighboring nodes of the specified node.
     *
     * NOTE: should be replaced with the forEachNeighbour function if performance is critical, to avoid copying overhead.
     *
     * @param node The node whose neighbors are to be retrieved.
     * @return A list of neighboring nodes connected to the specified node.
     * @throws IllegalStateException If the specified node is not found in the graph. */
    fun neighbours(node: T): List<T> = finalizeAdjacencyListIfNeeded(false).run {
        val neighbours = mutableListOf<T>()
        val u = node2IdOrNull(node) ?: error("'$node' not found in graph")
        adjacencyList.forEachNeighbour(u) { v ->
            neighbours.add(id2Node(v))
        }
        neighbours
    }

    /** Executes a given function on all the neighbours of a given node.
     *
     * More performant than retrieving a copied list of nodes and calling forEach on them.
     * NOTE: For performance, the node is not validated. Passing a node that isn't in the graph results in undefined
     * behaviour (e.g. Graph will add it as a new node, and Grid may process a different tile's neighbours).
     *
     * @param node The node whose neighbours we want to process
     * @param action The function to be called on each neighbour */
    fun forEachNeighbour(node: T, action: (T) -> Unit) = finalizeAdjacencyListIfNeeded(false).run {
        adjacencyList.forEachNeighbour(node2Id(node)) { v ->
            action(id2Node(v))
        }
    }

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
     * @param target An optional node that, if specified, the bfs will stop once it's found. Finding the target will
     * make foundTarget() return true, and store the path to the found target node for use in visualizations.
     * @param reset A boolean indicating whether to reset the previous search results. If set to false, previously
     * visited nodes will not be visited again.
     * @param maxDistance Nodes further away from the startNodes than maxDistance will not be traversed.
     * @throws IllegalStateException If any of the starting nodes is not found in the graph, or if the graph is sparse,
     * and the target node is not defined beforehand. */
    fun bfs(
        startNodes: List<T>,
        target: T? = null,
        reset: Boolean = true,
        maxDistance: Double = Double.POSITIVE_INFINITY
    ) {
        finalizeAdjacencyListIfNeeded(false)
        val time = measureTimeMillis {
            val startNodeIds = startNodes.map { node ->
                node2IdOrNull(node) ?: error("startNode '$node' not found in graph")
            }
            if (isSparse) {
                target?.let {
                    node2IdOrNull(it)
                        ?: error(
                            """target '$target' not found in graph. Target must be added to the graph before a search
                            is performed if the graph is sparse, which it often will be if connectWithRule is used."""
                        )
                }
            }
            val targetId = target?.let { node2IdOrNull(it) } ?: -1
            try {
                searchResults?.foundTarget = false
                finalPath = null
                if (reset) {
                    searchResults = null
                }
                searchResults = BFS(adjacencyList).run {
                    if (isSparse)
                        bfsSparse(startNodeIds, targetId, searchResults, maxDistance)
                    else
                        bfs(startNodeIds, targetId, searchResults, maxDistance)
                }
                searchResults?.currentVisited?.asList()?.lastOrNull()?.let { id2NodeOrNull(it) }
                    ?.also { finalNode ->
                        if (finalNode == target)
                            finalPath = getPath(finalNode)
                    }
            } catch (e: InvalidEdgeException) {
                invalidEdgeError(e, "bfs")
            }
        }
        if (debugTimeUse) {
            debug("bfs took $time ms.")
        }
    }

    /** Overload of fun bfs(startNodes: List<T>, target: T?, reset: Boolean) that accepts a single starting node and
     * an optional target, instead of a list of starting nodes */
    fun bfs(
        startNode: T,
        target: T? = null,
        reset: Boolean = true,
        maxDistance: Double = Double.POSITIVE_INFINITY
    ) =
        bfs(listOf(startNode), target, reset, maxDistance)


    /** Performs a Depth-First Search, which finds all nodes that's reachable from the starting node.
     * It stores results that can be retrieved with the following functions:
     *
     * - `depth()`
     * - `currentVisitedNodes()`
     * - `visitedNodes()`
     * - `distanceTo(node: T)`* Note: will return the depth of the node in the DFS tree, not the shortest distance.
     *
     * @param startNode The starting node for the DFS traversal.
     * @param reset A boolean indicating whether to reset the previous search results. If set to false,
     * previously visited nodes will not be visited again.
     * @throws IllegalStateException If the starting node is not found in the graph. */
    fun dfs(startNode: T, reset: Boolean = true) {
        finalizeAdjacencyListIfNeeded(false)
        val time = measureTimeMillis {
            val startId = node2IdOrNull(startNode) ?: error("startNode '$startNode' not found in graph")
            if (reset)
                searchResults = null
            finalPath = null
            try {
                searchResults = DFS(adjacencyList).run {
                    if (isSparse)
                        dfsSparse(startId, searchResults)
                    else
                        dfs(startId, searchResults)
                }
            } catch (e: InvalidEdgeException) {
                invalidEdgeError(e, "dfs")
            }
        }
        if (debugTimeUse) {
            debug("dfs took $time ms.")
        }
    }

    /** Performs Dijkstra's algorithm to find the shortest path from the starting node to all other nodes. It
     *  stores results that can be retrieved with the following functions:
     *
     * - `currentVisitedNodes()`
     * - `visitedNodes()`
     * - `finalPath()`
     * - `foundTarget()`
     * - `distanceTo(node: T)`
     * - `maxDistance()`
     * - `furthestNode()`
     *
     * @param startNode The starting node to find the shortest paths from.
     * @param target An optional target node. If specified, the algorithm will store the shortest path to the
     * target node for use in visualization and flag the target as found so that foundTarget() returns true
     * @throws IllegalStateException If the starting node is not found in the graph, or if the graph is sparse, and
     * the target node is not defined beforehand. */
    fun shortestPathsFrom(startNode: T, target: T? = null) {
        finalizeAdjacencyListIfNeeded(false)
        val time = measureTimeMillis {
            if (edgesCount == 0 && !adjacencyList.isDynamic) {
                System.err.println("Warning: The adjacency list has no connections, making pathfinding infeasible.")
            }
            val startId = node2IdOrNull(startNode) ?: error("startNode '$startNode' not found in graph")
            if (isSparse) {
                target?.let {
                    node2IdOrNull(it)
                        ?: error(
                            """target '$target' not found in graph. Target must be added to the graph before a search
                            is performed if the graph is sparse, which it often will be if connectWithRule is used."""
                        )
                }
            }
            finalPath = null
            val targetId = target?.let { node2IdOrNull(it) } ?: -1
            try {
                searchResults = Dijkstra(adjacencyList).run {
                    if (isSparse)
                        dijkstraSparse(startId, targetId)
                    else
                        dijkstra(startId, targetId)
                }
            } catch (e: InvalidEdgeException) {
                invalidEdgeError(e, "shortestPathsFrom")
            }
            target?.let {
                finalPath = getPath(it)
            }
        }
        if (debugTimeUse) {
            debug("dijkstra took $time ms.")
        }
    }

    internal fun invalidEdgeError(e: InvalidEdgeException, algorithm: String): Nothing {
        val node = id2Node(e.uId)
        val neighbour = if (node is Int) e.vId else "UNKNOWN"
        error("$algorithm was trying to traverse from node '$node' to node '$neighbour', which is not defined in the graph.")
    }

    /** Executes the Floyd-Warshall algorithm on the graph to compute the shortest paths between all pairs of nodes.
     *
     * This function calculates the shortest path distances for every pair of nodes in the graph and stores the results
     * in the `allDistances` property. The algorithm works with some negative weights, but it assumes that
     * the graph does not contain negative weight cycles.
     *
     * The results can be retrieved using the `distanceFromUtoV(u: T, v: T)` function, which provides the shortest
     * distance between any two nodes.
     *
     * If the graph has no edges, a warning is printed to the standard error stream and nothing is computed. Results
     * from a previous run (if any) are kept, otherwise `distanceFromUtoV` will throw. */
    fun allPairsShortestPaths() {
        finalizeAdjacencyListIfNeeded(true)
        val time = measureTimeMillis {
            if (edgesCount == 0 && !adjacencyList.isDynamic) {
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
        val uId = node2IdOrNull(u) ?: error("'$u' not found in graph")
        val vId = node2IdOrNull(v) ?: error("'$v' not found in graph")
        it[uId][vId]
    } ?: error("FloydWarshall must be run successfully before calling distanceFromUtoV")

// ADDITIONAL ALGORITHMS
    /** Computes the Minimum Spanning Tree (MST) of the graph using Prim's algorithm.
     *
     * If the graph is unweighted, it is first converted to a weighted graph with default edge weights.
     * Does not work on Grids with deleted nodes.
     *
     * @return A pair containing the total weight of the MST and a graph whose edges represents the MST.
     * @throws IllegalStateException If the graph is empty or not fully connected. */
    fun minimumSpanningTree(): Pair<Double, Graph> {
        finalizeAdjacencyListIfNeeded(true)
        val timeStart = System.currentTimeMillis()
        val (totalWeight, mst) = prims(adjacencyList).run {
            first to second.let { connections ->
                val mstGraph = Graph()
                connections.edges().forEach { (uId, vId, w) ->
                    val u = id2Node(uId)
                    val v = id2Node(vId)
                    mstGraph.addEdge(u, v, w)
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
        finalizeAdjacencyListIfNeeded(true)
        val topologicalSorting: List<T>
        val time = measureTimeMillis {
            topologicalSorting = try {
                DFS(adjacencyList).topologicalSort().map { id2Node(it) }
            } catch (e: InvalidEdgeException) {
                invalidEdgeError(e, "TopologicalSort")
            }
        }
        if (debugTimeUse) {
            debug("topologicalSort took $time ms.")
        }
        finalPath = topologicalSorting
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
        finalizeAdjacencyListIfNeeded(true)
        val scc: List<List<T>>
        val time = measureTimeMillis {
            val sccIds = DFS(adjacencyList).stronglyConnectedComponents()
            scc = sccIds.map { component -> component.map { id2Node(it) } }
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
        val nrOfPaths: Long
        val time = measureTimeMillis {
            finalizeAdjacencyListIfNeeded(false)
            val startId = node2IdOrNull(startNode) ?: error("startNode '$startNode' not found in graph")
            val targetId = node2IdOrNull(targetNode) ?: error("targetNode '$targetNode' not found in graph")
            nrOfPaths = if (isSparse) {
                nrOfPathsSparse(adjacencyList, startId, targetId, mod)
            } else {
                nrOfPaths(adjacencyList, startId, targetId, mod)
            }
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
     * @return A list of nodes representing the path from the start to the target node, or null if no path was found.
     * @throws IllegalStateException If no search algorithm (DFS, BFS, shortestPathsFrom) has been executed yet. */
    fun getPath(target: T): List<T>? {
        val path: List<T>
        val time = measureTimeMillis {
            val targetId = node2IdOrNull(target)
            if (searchResults == null) {
                error("Can't getPath because no search has (DFS, BFS, shortestPathsFrom) been run yet")
            }
            val pathIds = searchResults!!.run {
                if (isSparse) {
                    getPathFromMap(targetId, parentsSparse)
                } else {
                    getPathFromArray(targetId, parents)
                }
            }
            if (pathIds.isEmpty()) return null
            path = pathIds.mapNotNull { id2NodeOrNull(it) }
        }
        if (debugTimeUse) {
            debug("getPath took $time ms.")
        }
        return path
    }

    private inline fun getPath(destination: Int?, parentOf: (Int) -> Int): List<Int> {
        val path = mutableListOf<Int>()
        destination?.let { dest ->
            var current = dest
            while (parentOf(current) != -1) {
                path.add(current)
                current = parentOf(current)
                if (current == destination)
                    break
            }
            path.add(current)
        }
        return if (path.size > 1) path.reversed() else emptyList()
    }

    private fun getPathFromArray(destination: Int?, parents: IntArray) = getPath(destination) { parents[it] }
    private fun getPathFromMap(destination: Int?, parents: Map<Int, Int>) =
        getPath(destination) { parents[it] ?: -1 }


// HELPER FUNCTIONS
    /** Clears the search results stored in the graph.
     *
     * The search results are replaced with empty results, as if a search had visited no nodes. Until a new search
     * (DFS, BFS, or shortestPathsFrom) is run, the following functions return:
     *
     * - `depth()`: throws IllegalStateException
     * - `currentVisitedNodes()`: an empty list
     * - `visitedNodes()`: an empty list
     * - `foundTarget()`: false
     * - `distanceTo(node: T)`: Double.POSITIVE_INFINITY
     * - `maxDistance()`: Double.POSITIVE_INFINITY
     * - `furthestNode()`: the first node of the graph (all distances are infinite)
     *
     * NOTE: `finalPath()` is not cleared by this function. It is cleared when the next search is run. */
    fun resetSearchResults() {
        searchResults = GraphSearchResults(nrOfNodes)
    }

// PRINTER FUNCTIONS
    /** Prints the graph's adjacency list to the standard error stream. */
    open fun print() = nodes().forEach {
        System.err.println("$it ---> ${edges(it)}")
    }

    /** Prints which nodes each node is connected to. */
    fun printConnections() {
        System.err.println(buildString {
            nodes().forEach { node ->
                val edges = edges(node)
                val edgeString = edges.joinToString { it.second.toString() }
                append("$node ----> [$edgeString]\n")
            }
        })
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
 * graph.shortestPathsFrom("A", "B")
 * // NOTE: visualizeGraph() requires the smartgraph.css and smartgraph.properties files to be added to the root of your project.
 * graph.visualizeGraph() // Find the needed files here: https://github.com/Norskeaksel/GraphMateKT
 * ```
 *
 * @param debugTimeUse If true, the time taken by each graph algorithm is printed to the standard error stream. Defaults to false.
 */
class Graph(debugTimeUse: Boolean = false) : BaseGraph<Any>(0, debugTimeUse, false) {
    private val node2id = mutableMapOf<Any, Int>()
    private val id2Node = mutableMapOf<Int, Any>()

    private fun getOrAddNodeId(node: Any): Int {
        return node2id[node] ?: addNode(node).run { node2id[node]!! }
    }

    override fun addNode(node: Any) {
        if (node2id.containsKey(node)) {
            return
        }
        node2id[node] = nrOfNodes
        id2Node[nrOfNodes++] = node
        if (!adjacencyList.isDynamic) {
            adjacencyListIsFinalized = false
        }
    }

    override fun addEdge(node1: Any, node2: Any, weight: Double) {
        val id1 = getOrAddNodeId(node1)
        val id2 = getOrAddNodeId(node2)
        edges.addEdge(id1, id2, weight)
        edgesCount++
        adjacencyListIsFinalized = false
    }

    override fun node2IdOrNull(node: Any): Int? = node2id[node]
    override fun node2Id(node: Any): Int = node2id[node] ?: (nrOfNodes++).also {
        node2id[node] = it
        id2Node[it] = node
    }

    override fun id2NodeOrNull(id: Int): Any? = id2Node[id]
    override fun id2Node(id: Int): Any = id2Node[id] ?: error("Node with ID $id not found in graph")
    override fun nodes(): List<Any> = id2Node.values.toList()
    override fun connectWithRule(getNeighbours: (node: Any) -> List<Any>) {
        isSparse = true
        super.connectWithRule(getNeighbours)
    }
}


/** A specialized graph class that represent integer nodes ranging from 0 to size-1.
 *
 * The IntGraph class behaves a lot like the Graph class when used with integers like the example above.
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
 * @param debugTimeUse optional flag that prints the time use of run algorithms to standard error*/
class IntGraph(private val size: Int, debugTimeUse: Boolean = false, isSparse: Boolean = false) :
    BaseGraph<Int>(size, debugTimeUse, isSparse) {

    private val nodes by lazy { IntArray(size) { it } }
    private val nrOfEdgesFrom by lazy { IntArray(size) }

    /** IntGraph doesn't support addNode(), because nodes are set on initialization.
     * @throws IllegalStateException if called.*/
    override fun addNode(node: Int) =
        error("IntGraph doesn't support addNode(), because nodes are set on initialization.")

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


/** A general graph class that represents a 2D grid structure of nodes.
 *
 * It uses the data class:
 * `Tile(val x: Int, val y: Int, val data: Any? = null)`
 * to represent nodes of any datatype, where each node also has x and y coordinates.
 *
 * The grid can be created in two ways:
 * - By specifying a `width` and `height`, optionally initializing it with tiles that has `data=null`.
 * - By passing a list of strings, where each string represents a row, and all strings must have the same length.
 *
 * The Grid class supports the same algorithms as the Graph class. Additionally, it
 * provides methods for connecting nodes in the grid without explicitly adding edges:
 * - `.connectGridDefault()` connects each node to its neighbors in the up, down, left, and right directions, if they exist.
 * - `.connectWithRule(::yourCustomFunction)` (or `.connectWithRule { yourLambda }`) allows custom connections, where the function
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
 * grid.connectWithRule { t ->
 *     grid.getStraightNeighbours(t).filter { it.x > t.x || it.y > t.y } // connect right and down
 * }
 * grid.bfs(Tile(0, 0))
 * grid.visualizeGrid()
 * ```
 *
 * @param width The width of the grid (number of columns).
 * @param height The height of the grid (number of rows).
 * @param initWithDatalessTiles If `true`, initializes the grid with empty tiles. */
class Grid(
    val width: Int,
    val height: Int,
    initWithDatalessTiles: Boolean = true,
    debugTimeUse: Boolean = false,
    isSparse: Boolean = false
) :
    BaseGraph<Tile>(width * height, debugTimeUse, isSparse) {
    private val gridSize = width * height
    private val nodes = MutableList<Tile?>(gridSize) { null }
    private var activeNodes = listOf<Tile>()
    private var activeNodesNeedUpdating = true

    /** Construct the grid from a list of strings, where each string represents a row in the grid, and each character, a node.
     *
     * Sets the grid height to the list size and the width to the length of the first string.
     * Requires that all strings have the same length. If some cells should be deleted,
     * `deleteNodeAtXY(x,y)` or `deleteNodesWithData(data)` can be used after the grid is created.
     *
     * @param stringGrid A list of strings representing the grid
     * */
    constructor(stringGrid: List<String>, debugTimeUse: Boolean = false, isSparse: Boolean = false) : this(
        stringGrid[0].length,
        stringGrid.size,
        false,
        debugTimeUse,
        isSparse
    ) {
        require(stringGrid.all { it.length == width })
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


    /** Adds (or replaces) the tile at the node's (x, y) coordinates. Can also be used to restore a deleted tile.
     *
     * NOTE: For performance, the coordinates are not validated. An x outside 0 until width, with a valid y, overwrites
     * a tile on another row.
     * @param node The tile to add
     * @throws IndexOutOfBoundsException If `x + y * width` is outside the grid (0 until width * height). */
    override fun addNode(node: Tile) {
        val id = node2Id(node)
        nodes[id] = node
        activeNodesNeedUpdating = true
    }

    override fun node2IdOrNull(node: Tile): Int? = xy2Id(node.x, node.y)
    override fun node2Id(node: Tile) = node.x + node.y * width
    override fun id2NodeOrNull(id: Int) = nodes.getOrNull(id)
    override fun id2Node(id: Int): Tile = nodes.getOrNull(id) ?: error("Node with ID $id not found in grid")

    override fun addEdge(node1: Tile, node2: Tile, weight: Double) {
        val u = node2IdOrNull(node1) ?: error("$node1 not found in grid")
        val v = node2IdOrNull(node2) ?: error("$node2 not found in grid")
        edges.addEdge(u, v, weight)
        edgesCount++
        adjacencyListIsFinalized = false
    }

    override fun nodes(): List<Tile> {
        if (activeNodesNeedUpdating) {
            activeNodes = nodes.filterNotNull()
            activeNodesNeedUpdating = false
        }
        return activeNodes
    }

    override fun topologicalSort() =
        finalizeAdjacencyListIfNeeded(true).run {
            DFS(adjacencyList).topologicalSort(deleted()).map { id2Node(it) }.also {
                finalPath = it
            }
        }

    override fun stronglyConnectedComponents(): GridComponents =
        finalizeAdjacencyListIfNeeded(true).run { DFS(adjacencyList).stronglyConnectedComponents(deleted()) }
            .map { component -> component.mapNotNull { id2NodeOrNull(it) } }

    private fun deleted() = BooleanArray(nodes.size) { nodes[it] == null }

    private fun xyInRange(x: Int, y: Int) = x in 0 until width && y in 0 until height
    private fun xy2Id(x: Int, y: Int) =
        if (xyInRange(x, y)) (x + y * width).let { if (gridHasId(it)) it else null } else null

    /** Retrieves the `Tile` node at the specified (x, y) coordinates, if it exists.
     *
     * @param x The x-coordinate of the node.
     * @param y The y-coordinate of the node.
     * @return The `Tile` node at the given coordinates, or `null` if no node exists at the specified location. */
    fun xy2NodeOrNull(x: Int, y: Int) = xy2Id(x, y)?.let { id2NodeOrNull(it) }

    /** Retrieves the `Tile` node at the specified (x, y) coordinates, if it exists.
     *
     * @param x The x-coordinate of the node.
     * @param y The y-coordinate of the node.
     * @return The `Tile` node at the given coordinates, or `null` if no node exists at the specified location.
     * @throws IllegalStateException if no node is found at coordinates (x, y) */
    fun xy2Node(x: Int, y: Int) = xy2Id(x, y)?.let { id2Node(it) } ?: error("No node found at coordinates ($x, $y)")
    private fun gridHasId(id: Int) = nodes.getOrNull(id) != null
    private fun deleteNodeId(id: Int) {
        require(edgesCount == 0 && !adjacencyList.isDynamic) { "Deleting nodes after edges are added is not supported" }
        activeNodesNeedUpdating = true
        nodes[id] = null
    }

    /** Returns the first tile in the grid (row-wise from left to right) that contains the requested data.
     *
     * @param data The value to look for in the grid.
     * @return The first matching tile.
     * @throws NoSuchElementException If no tile with matching data exists. */
    fun firstNodeWithData(data: Any) = nodes.first { it?.data == data }!!

    /** Deletes the node at the specified (x, y) coordinates in the grid.
     *
     * If the coordinates are outside the grid, a warning is printed, and no action is taken.
     *
     * @param x The x-coordinate of the node to delete.
     * @param y The y-coordinate of the node to delete.
     * @throws IllegalArgumentException Called the function after edges have been added to the grid is not supported */
    fun deleteNodeAtXY(x: Int, y: Int) {
        val id = xy2Id(x, y) ?: run {
            System.err.println("Warning, coordinates ($x, $y) are outside the grid")
            return
        }
        deleteNodeId(id)
    }

    /** Deletes all nodes in the grid that have the specified data. Deleted nodes are not considered neighbours of nodes.
     * @param data The data value to match for deletion.
     * @throws IllegalArgumentException Called the function after edges have been added to the grid is not supported */
    fun deleteNodesWithData(data: Any?) {
        nodes.indices.forEach { i ->
            if (nodes[i]?.data == data) {
                deleteNodeId(i)
            }
        }
    }

    private fun forEachStraightNeighbourId(id: Int, action: (Int) -> Unit) {
        val x = id % width
        val y = id / width
        // @formatter:off
        if (y > 0)          nodes[id - width]?.let { action(id - width) }
        if (x > 0)          nodes[id - 1]?.let     { action(id - 1) }
        if (x < width - 1)  nodes[id + 1]?.let     { action(id + 1) }
        if (y < height - 1) nodes[id + width]?.let { action(id + width) }
        // @formatter:on
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
            xy2NodeOrNull(t.x, t.y - 1),
            xy2NodeOrNull(t.x - 1, t.y),
            xy2NodeOrNull(t.x + 1, t.y),
            xy2NodeOrNull(t.x, t.y + 1),
        )

    /** Retrieves the diagonal neighbors of the given tile.
     *
     * The diagonal neighbors are the tiles located at the top-left, top-right, bottom-left, and bottom-right
     * relative to the given tile, if they exist within the grid boundaries and are not deleted.
     *
     * @param t The tile for which to retrieve the diagonal neighbors.
     * @return A list of diagonal neighbors of the given tile, or an empty list if no neighbors exist. */
    fun getDiagonalNeighbours(t: Tile) = listOfNotNull(
        xy2NodeOrNull(t.x - 1, t.y - 1),
        xy2NodeOrNull(t.x + 1, t.y - 1),
        xy2NodeOrNull(t.x - 1, t.y + 1),
        xy2NodeOrNull(t.x + 1, t.y + 1),
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

    /** Connects all nodes in the grid with their straight neighbours, i.e. top, down, left, right neighbours,
     * if they exist within the grid boundaries and have not been deleted.*/
    fun connectGridDefault() {
        nodes().forEach { u ->
            getStraightNeighbours(u).forEach { v ->
                addEdge(u, v)
            }
        }
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

internal interface AdjacencyList {
    private class IncompatibleAlgorithm(message: String) : Exception(message)

    val size: Int
    val isDynamic: Boolean
    fun forEachNeighbour(node: Int, action: (Int) -> Unit)
    fun forEachEdge(node: Int, action: (Double, Int) -> Unit)
    fun nodes(): IntArray
    fun edges(): List<Triple<Int, Int, Double>>
    fun neighbours(node: Int): IntArray
    fun weights(node: Int): DoubleArray
    fun deepCopy(): AdjacencyList
    fun reversed(): AdjacencyList
}


internal class AdjacecnyListDynamic(
    override val size: Int,
    private val forEachNeighbourOf: (node: Int, action: (Int) -> Unit) -> Unit,
) : AdjacencyList {
    private fun unsupported(): Nothing {
        throw UnsupportedOperationException("Function cannot use be used with dynamic connections.")
    }

    override val isDynamic = true
    override fun forEachNeighbour(node: Int, action: (Int) -> Unit) = forEachNeighbourOf(node, action)
    override fun forEachEdge(node: Int, action: (Double, Int) -> Unit) = forEachNeighbourOf(node) { action(1.0, it) }

    override fun neighbours(node: Int): IntArray {
        val neighbours = IntArrayList()
        forEachNeighbourOf(node) { neighbours.add(it) }
        return neighbours.intArray()
    }
    override fun edges(): List<Triple<Int, Int, Double>> = unsupported()
    override fun nodes(): IntArray = unsupported()
    override fun weights(node: Int): DoubleArray = unsupported()
    override fun deepCopy(): AdjacencyList = unsupported()
    override fun reversed(): AdjacencyList = unsupported()
}


internal class AdjacencyListFlattened(val nrOfNodes: Int, val edges: UnboxedEdges) : AdjacencyList {
    val nrOfEdges = edges.size
    val starts: IntArray = IntArray(nrOfNodes)
    val ends: IntArray = IntArray(nrOfNodes)
    val flattenedNeighbours = IntArray(nrOfEdges)
    val flattenedWeights = DoubleArray(nrOfEdges)
    override val isDynamic = false

    init {
        val nrOfEdgesFrom = IntArray(nrOfNodes)
        edges.from.intArray().forEach { nrOfEdgesFrom[it]++ }
        var sum = 0
        repeat(nrOfNodes) { i ->
            starts[i] = sum
            sum += nrOfEdgesFrom[i]
            ends[i] = sum
        }
        repeat(nrOfEdges) { i ->
            edges.run {
                val u = from[i]
                val v = to[i]
                val offset = --nrOfEdgesFrom[u]
                val idx = starts[u] + offset
                flattenedNeighbours[idx] = v
                flattenedWeights[idx] = weights[i]
            }
        }
    }

    override fun nodes() = IntArray(size) { it }
    override fun edges() = edges.boxedEdges()
    override fun neighbours(node: Int): IntArray {
        if (node >= size)
            return intArrayOf()
        val start = starts[node]
        val end = ends[node]
        return flattenedNeighbours.copyOfRange(start, end)
    }

    override fun weights(node: Int): DoubleArray {
        if (node >= size)
            return doubleArrayOf()
        val start = starts[node]
        val end = ends[node]
        return flattenedWeights.copyOfRange(start, end)
    }

    override fun forEachNeighbour(node: Int, action: (Int) -> Unit) {
        if (node >= size)
            return
        val start = starts[node]
        val end = ends[node]
        for (i in start until end) {
            action(flattenedNeighbours[i])
        }
    }

    override fun forEachEdge(node: Int, action: (Double, Int) -> Unit) {
        if (node >= size)
            return
        val start = starts[node]
        val end = ends[node]
        for (i in start until end) {
            action(flattenedWeights[i], flattenedNeighbours[i])
        }
    }

    override fun deepCopy(): AdjacencyList = AdjacencyListFlattened(nrOfNodes, edges.deepCopy())

    override val size get() = nrOfNodes
    override fun reversed(): AdjacencyList = AdjacencyListFlattened(nrOfNodes, edges.reversed())
}


internal class AdjacencyListNested(private val nrOfNodes: Int, private val edges: UnboxedEdges) : AdjacencyList {
    private val nodes by lazy { IntArray(nrOfNodes) { it } }
    private val neighbours by lazy { Array(nrOfNodes) { IntArrayList() } }
    private val weights by lazy { Array(nrOfNodes) { DoubleArrayList() } }

    init {
        repeat(edges.size) {
            val u = edges.from[it]
            val v = edges.to[it]
            val w = edges.weights[it]
            neighbours[u].add(v)
            weights[u].add(w)
        }
    }

    override val isDynamic = false
    override fun nodes() = nodes
    override fun edges() = edges.boxedEdges()
    override fun neighbours(node: Int): IntArray = neighbours[node].intArray()
    override fun weights(node: Int) = weights[node].doubleArray()

    override fun forEachNeighbour(node: Int, action: (Int) -> Unit) {
        neighbours[node].intArray().forEach { v ->
            action(v)
        }
    }

    override fun forEachEdge(node: Int, action: (Double, Int) -> Unit) {
        val neighbours = neighbours[node].intArray()
        val weights = weights[node].doubleArray()
        neighbours.indices.forEach {
            action(weights[it], neighbours[it])
        }
    }

    override fun deepCopy() = AdjacencyListNested(nrOfNodes, edges.deepCopy())
    override val size get() = nrOfNodes

    override fun reversed() = AdjacencyListNested(nrOfNodes, edges.reversed())
}



internal class BFS(private val graph: AdjacencyList) {
    fun bfs(
        startIds: List<Int>,
        targetId: Int = -1,
        previousSearchResult: GraphSearchResults? = null,
        maxDepth: Double = Double.POSITIVE_INFINITY,
    ): GraphSearchResults {
        val r = previousSearchResult ?: GraphSearchResults(graph.size)
        r.currentVisited.clear()
        var depth = r.depth ?: 0
        val queue = ArrayDeque<Int>()
        startIds.forEach {
            queue.add(it)
            r.distances[it] = 0.0
        }
        while (queue.isNotEmpty() && !r.foundTarget) {
            val currentId = queue.removeFirst()
            if (r.visited[currentId])
                continue
            r.visited[currentId] = true
            r.currentVisited.add(currentId)

            val currentDistance = r.distances[currentId]
            if (currentDistance >= maxDepth) {
                continue
            }
            graph.forEachNeighbour(currentId) { v ->
                val newDistance = currentDistance + 1
                try {
                    if ((!r.visited[v] && newDistance < r.distances[v]) || v == targetId) {
                        r.parents[v] = currentId
                        depth = newDistance.toInt().coerceAtLeast(depth)
                        r.distances[v] = newDistance
                        if (v == targetId) {
                            r.currentVisited.add(v)
                            r.visited[v] = true
                            r.foundTarget = true
                        }
                        queue.add(v)
                    }
                } catch (_: ArrayIndexOutOfBoundsException) {
                    throw InvalidEdgeException(currentId, v)
                }
            }
        }
        r.depth = depth
        r.processedOrder = r.currentVisited
        return r
    }

    fun bfsSparse(
        startIds: List<Int>,
        targetId: Int = -1,
        previousSearchResult: GraphSearchResults? = null,
        maxDepth: Double = Double.POSITIVE_INFINITY,
    ): GraphSearchResults {
        val r = previousSearchResult ?: GraphSearchResults(graph.size)
        r.currentVisited.clear()
        var depth = r.depth ?: 0
        val queue = ArrayDeque<Int>()
        startIds.forEach {
            queue.add(it)
            r.distancesSparse[it] = 0.0
        }
        while (queue.isNotEmpty() && !r.foundTarget) {
            val currentId = queue.removeFirst()
            if (currentId in r.visitedSparse)
                continue
            r.currentVisited.add(currentId)
            r.visitedSparse.add(currentId)

            val currentDistance = r.distancesSparse[currentId]!!
            if (currentDistance >= maxDepth) {
                continue
            }
            graph.forEachNeighbour(currentId) { v ->
                val newDistance = currentDistance + 1
                if (v == targetId ||
                    (v !in r.distancesSparse && newDistance < (r.distancesSparse[v] ?: Double.POSITIVE_INFINITY))
                ) {
                    r.parentsSparse[v] = currentId
                    depth = newDistance.toInt().coerceAtLeast(depth)
                    r.distancesSparse[v] = newDistance
                    if (v == targetId) {
                        r.currentVisited.add(v)
                        r.visitedSparse.add(v)
                        r.foundTarget = true
                    }
                    queue.add(v)
                }
            }
        }
        r.depth = depth
        r.processedOrder = r.currentVisited
        return r
    }
}



internal class DFS(private val graph: AdjacencyList) {
    private var r = GraphSearchResults(graph.size)
    fun dfs(
        start: Int,
        initialSearchResults: GraphSearchResults? = null,
    ): GraphSearchResults {
        r = initialSearchResults ?: GraphSearchResults(graph.size)
        r.currentVisited = IntArrayList()
        var maxDepth = r.depth ?: 0
        fun visit(id: Int, depth: Int) {
            if (r.visited[id]) return
            r.visited[id] = true
            r.currentVisited.add(id)
            r.distances[id] = depth.toDouble()
            maxDepth = depth.coerceAtLeast(maxDepth)
            graph.forEachNeighbour(id) { v ->
                try {
                    if (!r.visited[v]) {
                        r.parents[v] = id
                        visit(v, depth + 1)
                    }
                } catch (_: ArrayIndexOutOfBoundsException) {
                    throw InvalidEdgeException(id, v)
                }
            }
            r.processedOrder.add(id)
        }

        visit(start, 0)
        r.depth = maxDepth
        return r
    }

    fun dfsSparse(
        start: Int,
        initialSearchResults: GraphSearchResults? = null,
    ): GraphSearchResults {
        r = initialSearchResults ?: GraphSearchResults(graph.size)
        r.currentVisited = IntArrayList()
        var maxDepth = r.depth ?: 0
        fun visit(id: Int, depth: Int) {
            if (id in r.visitedSparse) return
            r.visitedSparse.add(id)
            r.currentVisited.add(id)
            r.distancesSparse[id] = depth.toDouble()
            maxDepth = depth.coerceAtLeast(maxDepth)
            graph.forEachNeighbour(id) { v ->
                try {
                    if (v !in r.visitedSparse) {
                        r.parentsSparse[v] = id
                        visit(v, depth + 1)
                    }
                } catch (_: ArrayIndexOutOfBoundsException) {
                    throw InvalidEdgeException(id, v)
                }
            }
            r.processedOrder.add(id)
        }

        visit(start, 0)
        r.depth = maxDepth
        return r
    }

    fun stronglyConnectedComponents(deleted: BooleanArray = BooleanArray(graph.size)): IntComponents {
        val topologicalOrder = DFS(graph.reversed()).topologicalSort(deleted).reversed()
        val stronglyConnectedComponents = topologicalOrder.mapNotNull { id ->
            if (r.visited[id]) null
            else {
                dfs(id, r)
                r.currentVisited
            }
        }
        return stronglyConnectedComponents.map { it.asList() }
    }

    fun topologicalSort(deleted: BooleanArray = BooleanArray(graph.size)): List<Int> {
        for (i in 0 until graph.size) {
            if (deleted[i]) continue
            dfs(i, r)
        }
        return r.processedOrder.asList()
    }
}


internal class Dijkstra(private val graph: AdjacencyList) {
    private var r = GraphSearchResults(graph.size)
    fun dijkstra(start: Int, target: Int = -1): GraphSearchResults {
        r = GraphSearchResults(graph.size)
        r.distances[start] = 0.0
        val pq = PriorityQueue<Edge> { a, b -> a.first.compareTo(b.first) }
        pq.add(Edge(0.0, start))
        while (pq.isNotEmpty()) {
            val u = pq.poll().second
            if (r.visited[u])
                continue
            r.visited[u] = true
            r.currentVisited.add(u)
            if (u == target) {
                break
            }
            graph.forEachEdge(u) { d, v ->
                val newDistance = r.distances[u] + d
                try {
                    if (newDistance < r.distances[v]) {
                        r.distances[v] = newDistance
                        r.parents[v] = u
                        if (!r.visited[v]) {
                            pq.add(Edge(newDistance, v))
                        }
                        if (v == target) {
                            r.foundTarget = true
                        }
                    }
                } catch (_: ArrayIndexOutOfBoundsException) {
                    throw InvalidEdgeException(u, v)
                }
            }
        }
        r.processedOrder = r.currentVisited
        return r
    }

    fun dijkstraSparse(start: Int, target: Int = -1): GraphSearchResults {
        r = GraphSearchResults(graph.size)
        r.distancesSparse[start] = 0.0
        val pq = PriorityQueue<Edge> { a, b -> a.first.compareTo(b.first) }
        pq.add(Edge(0.0, start))
        while (pq.isNotEmpty()) {
            val u = pq.poll().second
            if (u in r.visitedSparse)
                continue
            r.visitedSparse.add(u)
            r.currentVisited.add(u)
            if (u == target) {
                break
            }
            graph.forEachEdge(u) { d, v ->
                val newDistance = r.distancesSparse[u]!! + d
                try {
                    if (newDistance < (r.distancesSparse[v] ?: Double.POSITIVE_INFINITY)) {
                        r.distancesSparse[v] = newDistance
                        r.parentsSparse[v] = u
                        if (v !in r.visitedSparse) {
                            pq.add(Edge(newDistance, v))
                        }
                        if(v == target) {
                            r.foundTarget = true
                        }
                    }
                } catch (_: ArrayIndexOutOfBoundsException) {
                    throw InvalidEdgeException(u, v)
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
            graph.forEachEdge(node) { d, v ->
                distances[u][v] = d.coerceAtMost(distances[u][v])
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
    val visited by lazy { BooleanArray(graphSize) }
    val distances by lazy { DoubleArray(graphSize) { Double.POSITIVE_INFINITY } }
    val parents by lazy { IntArray(graphSize) { -1 } }
    var depth: Int? = null
    val visitedSparse = mutableSetOf<Int>()
    val distancesSparse = mutableMapOf<Int, Double>()
    val parentsSparse = mutableMapOf<Int, Int>()
    var currentVisited = IntArrayList()
    var processedOrder = IntArrayList()
    var foundTarget = false
}


internal interface Memo {
    operator fun get(id: Int): Long
    operator fun set(id: Int, value: Long)
}

internal class MemoArray(graphSize: Int) : Memo {
    private val memo = LongArray(graphSize) { -1L }
    override operator fun get(id: Int): Long = memo[id]
    override operator fun set(id: Int, value: Long) {
        memo[id] = value
    }
}

internal class MemoMap : Memo {
    private val memo = mutableMapOf<Int, Long>()
    override operator fun get(id: Int): Long = memo[id] ?: -1L
    override operator fun set(id: Int, value: Long) {
        memo[id] = value
    }
}

private fun memoDfs(currentId: Int, target: Int, graph: AdjacencyList, memo: Memo, mod: Long): Long {
    if (currentId == target)
        return 1L
    if (memo[currentId] != -1L)
        return memo[currentId]
    var totalPaths = 0L
    graph.forEachNeighbour(currentId) { neighbour ->
        totalPaths = (totalPaths + memoDfs(neighbour, target, graph, memo, mod)) % mod
    }
    memo[currentId] = totalPaths % mod
    return totalPaths
}

internal fun nrOfPaths(graph: AdjacencyList, start: Int, target: Int, mod: Long): Long {
    val memo = MemoArray(graph.size)
    return memoDfs(start, target, graph, memo, mod)
}

internal fun nrOfPathsSparse(graph: AdjacencyList, start: Int, target: Int, mod: Long): Long {
    val memo = MemoMap()
    return memoDfs(start, target, graph, memo, mod)
}package graphMateKT.graphAlgorithms


internal fun prims(graph: AdjacencyList): Pair<Double, AdjacencyList> {
    if (graph.size == 0) error("The graph is empty. Cannot do minimumSpanningTree")

    val visited = BooleanArray(graph.size)
    val connections = UnboxedEdges()
    val pq = PriorityQueue<Triple<Int, Int, Double>> { a, b -> a.third.compareTo(b.third) }
    var totalWeight = 0.0

    visited[0] = true
    graph.forEachEdge(0) { weight, to ->
        pq.add(Triple(0, to, weight))
    }
    var c = 0
    while (c < graph.size - 1) {
        if (pq.isEmpty()) error("The graph is not fully connected. Cannot do minimumSpanningTree")
        val (u, v, w) = pq.poll()
        if (visited[v]) continue
        visited[v] = true
        c++
        totalWeight += w

        connections.addEdge(u, v, w)
        graph.forEachEdge(v) { weight, next ->
            if (!visited[next]) {
                pq.add(Triple(v, next, weight))
            }
        }
    }

    return totalWeight to AdjacencyListFlattened(graph.size, connections)
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
