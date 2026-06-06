package graphMateKT.graphClasses

import graphMateKT.IntComponents
import graphMateKT.debug
import graphMateKT.graphAlgorithms.*
import graphMateKT.graphAlgorithms.BFS
import graphMateKT.graphAlgorithms.DFS
import graphMateKT.graphAlgorithms.Dijkstra
import graphMateKT.graphAlgorithms.FloydWarshall
import graphMateKT.graphAlgorithms.GraphSearchResults
import kotlin.system.measureTimeMillis

/** And abstract class that's used by the Graph, IntGraph and Grid classes for common functionality */
abstract class BaseGraph<T : Any>(
    size: Int,
    private val debugTimeUse: Boolean = false
) {
    // PROPERTIES AND INITIALIZATION
    protected lateinit var adjacencyList: AdjacencyList
    protected var nodes: MutableList<T?> = MutableList(size) { null }
    protected var nrOfEdges = 0
    private var searchResults: GraphSearchResults? = null
    private var finalPath: List<T>? = null
    private var allDistances: Array<DoubleArray>? = null

    // ABSTRACT FUNCTIONS
    /** Adds the given node to the graph
     * @param node The node to add */
    abstract fun addNode(node: T)
    abstract fun addEdge(node1: T, node2: T, weight: Double)
    protected abstract fun node2Id(node: T): Int?
    protected abstract fun id2Node(id: Int): T?

    /** @return the nodes in the graph */
    abstract fun nodes(): List<T>

    // CORE GRAPH OPERATIONS
    /** Adds an edge between two nodes in the graph, and creates the nodes if they don't exist.
     *
     * @param node1 The starting node of the edge.
     * @param node2 The ending node of the edge.
     * @param weight The weight of the edge. Defaults to `1.0`. */

    /** @return The total number of nodes in the graph. */
    fun size() = nodes().size

    /** Connects two nodes in the graph, by calling addEdge(node1,node2, weight) and addEdge(node2, node1, weight)
     *
     * @param node1 The first node to connect.
     * @param node2 The second node to connect.
     * @param weight The weight of the connection. Defaults to `1.0`. */
    fun connect(node1: T, node2: T, weight: Double = 1.0) {
        addEdge(node1, node2, weight)
        addEdge(node2, node1, weight)
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
    open fun edges(t: T): List<Pair<Double, T>> =
        node2Id(t)?.let { adjacencyList.getEdges(it) }?.map { Pair(it.first, id2Node(it.second)!!) }
            ?: error("Node $t not found in graph")

    /** Retrieves a list the neighboring nodes of the specified node.
     *
     * @param t The node whose neighbors are to be retrieved.
     * @return A list of neighboring nodes connected to the specified node.
     * @throws IllegalStateException If the specified node is not found in the graph. */
    open fun neighbours(t: T): List<T> = node2Id(t)?.let { adjacencyList.getNeighbours(it) }
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
        val time = measureTimeMillis {
            if (nrOfEdges == 0) {
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
        val time = measureTimeMillis {
            if (nrOfEdges == 0) {
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
    } ?: error("FloydWarshall must be run before calling distanceFromUtoV")

    // ADDITIONAL ALGORITHMS
    /** Computes the Minimum Spanning Tree (MST) of the graph using Prim's algorithm.
     *
     * If the graph is unweighted, it is first converted to a weighted graph with default edge weights.
     *
     * @return A pair containing the total weight of the MST and the graph representing the MST.
     * @throws IllegalStateException If the graph is empty or not fully connected. */
    fun minimumSpanningTree(): Pair<Double, Graph> {
        val timeStart = System.currentTimeMillis()
        val mst = prims(adjacencyList).run {
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
        return mst
    }

    /** Builds an order of nodes so that the first nodes has no outgoing edges, then nodes with edges pointing to these
     * and so on, assuming the graph is a Directed Acyclic Graph (DAG). This is done by running a DFS from each node
     * and ordering the nodes by descending depth (post-order).
     *
     * @return A list of nodes in topological order if the graph was a DAG.
     * Otherwise, returns a list of nodes in undefined order */
    open fun topologicalSort(): List<T> {
        val dfsGraph: MutableList<MutableList<Int>>
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
        val sccIds: IntComponents
        val scc: List<List<T>>
        val time = measureTimeMillis {
            sccIds = DFS(adjacencyList).stronglyConnectedComponents()
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

// PRINTER FUNCTIONS
    /** Prints the graph's adjacency list to the standard error stream. */
    fun print() = nodes().forEach {
        System.err.println("$it ---> ${edges(it)}")
    }

    /** Prints which nodes each node is connected to. */
    fun printConnections() = nodes().forEach {
        System.err.println("$it ---> ${edges(it)}")
    }
}
