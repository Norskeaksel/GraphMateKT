package graphMateKT

/** Edge has a weight w to a destination node v */
internal typealias Edge = Pair<Double, Int>
/** List of edges */
internal typealias Edges = MutableList<Edge>
/** Used to represent a weighted graph */

/** List of list of nodes */
internal typealias Components = List<List<Any>>
/** List of list of integer nodes */
internal typealias IntComponents = List<List<Int>>

// internal fun AdjacencyList.deepCopy() = map { it.toMutableList() }.toMutableList()

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
