package graphMateKT

internal data class GraphSearchResults(private val graphSize: Int) {
    val visited by lazy { BooleanArray(graphSize) }
    val distances by lazy { DoubleArray(graphSize) { Double.POSITIVE_INFINITY } }
    val parents by lazy { IntArray(graphSize) { -1 } }
    val targetVisited by lazy { BooleanArray(graphSize) }
    var depth: Int? = null

    val visitedSparse = mutableSetOf<Int>()
    val distancesSparse = mutableMapOf<Int, Double>()
    val parentsSparse = mutableMapOf<Int, Int>()
    val targetVisitedSparse = mutableSetOf<Int>()

    var currentVisited = IntArrayList()
    var processedOrder = IntArrayList()
    var foundTarget = false
}
