package graphMateKT.graphClasses

import graphMateKT.Edge
import graphMateKT.Tile
import graphMateKT.graphAlgorithms.DFS

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
    BaseGraph<Tile>(width * height, debugTimeUse) {
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

    override fun addEdge(node1: Tile, node2: Tile, weight: Double) {
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
