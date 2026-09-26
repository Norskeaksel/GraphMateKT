package graphMateKT

/** Edge has a weight w to a destination node v */
typealias Edge = Pair<Double, Int>
/** Mutable list of edges */
typealias Edges = MutableList<Edge>
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
            expandArray()
        }
        intArray[size] = value
        size++
    }

    operator fun get(i: Int) = intArray[i]
    fun intArray(): IntArray {
        if(intArray.size > size) {
            intArray = intArray.copyOf(size)
        }
        return intArray
    }

    private fun expandArray() {
        intArray = intArray.copyOf(intArray.size * 2)
    }
}

internal class DoubleArrayList {
    private var doubleArray: DoubleArray = DoubleArray(INITIAL_CAPACITY)
    var size: Int = 0
        private set

    fun add(value: Double) {
        if (size >= doubleArray.size) {
            expandArray()
        }
        doubleArray[size] = value
        size++
    }

    operator fun get(i: Int) = doubleArray[i]
    fun doubleArray(): DoubleArray {
        if(doubleArray.size > size) {
            doubleArray = doubleArray.copyOf(size)
        }
        return doubleArray
    }
    private fun expandArray() {
        doubleArray = doubleArray.copyOf(doubleArray.size * 2)
    }
}

internal class UnboxedEdges {
    val from = IntArrayList()
    val to = IntArrayList()
    val weights = DoubleArrayList()
    var maxId: Int = 0
        private set
    var size = 0
        private set

    fun addEdge(u: Int, v: Int, weight: Double) {
        from.add(u)
        to.add(v)
        weights.add(weight)
        maxId = maxOf(u, v, maxId)
        size++
    }

    fun addEdge(u: Int, v: Int) {
        addEdge(u, v, 1.0)
    }

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
