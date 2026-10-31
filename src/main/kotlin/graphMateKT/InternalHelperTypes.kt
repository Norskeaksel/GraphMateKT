package graphMateKT

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

internal data class Not(val node: Any)

internal operator fun Any.not() = Not(this)

internal data class TrieNode(val children: MutableMap<Char, TrieNode> = mutableMapOf(), var isTerminal: Boolean = false)

/** Edge has a weight w to a destination node v */
internal typealias Edge = Pair<Double, Int>
