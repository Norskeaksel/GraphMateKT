package graphMateKT

internal data class GraphSearchResults(private val graphSize: Int) {
    val visited by lazy { BooleanArray(graphSize) }
    val distances by lazy { DoubleArray(graphSize) { Double.POSITIVE_INFINITY } }
    val parents by lazy { IntArray(graphSize) { -1 } }
    var depth: Int? = null
    val visitedSparse = HashSet<Int>()
    val distancesSparse = HashMap<Int, Double>()
    val parentsSparse = HashMap<Int, Int>()
    var currentVisited = IntArrayList()
    var processedOrder = IntArrayList()
    var foundTarget = false
}
