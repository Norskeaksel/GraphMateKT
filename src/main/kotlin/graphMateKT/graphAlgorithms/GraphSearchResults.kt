package graphMateKT.graphAlgorithms

internal data class GraphSearchResults(private val graphSize: Int) {
    val visited by lazy { BooleanArray(graphSize) }
    val distances by lazy { DoubleArray(graphSize) { Double.POSITIVE_INFINITY } }
    val parents by lazy { IntArray(graphSize) { -1 } }
    var depth: Int = 0
    val visitedSparse = mutableSetOf<Int>()
    val distancesSparse = mutableMapOf<Int, Double>()
    val parentsSparse = mutableMapOf<Int, Int>()
    var currentVisited = mutableListOf<Int>()
    var processedOrder = mutableListOf<Int>()
    var foundTarget = false
}
