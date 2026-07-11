package graphMateKT.graphAlgorithms

internal data class GraphSearchResults(private val graphSize: Int) {
    val visited = BooleanArray(graphSize)
    val distances = DoubleArray(graphSize) { Double.POSITIVE_INFINITY }
    val parents: IntArray = IntArray(graphSize) { -1 }
    var depth: Int = 0
    var currentVisited = mutableListOf<Int>()
    var processedOrder = mutableListOf<Int>()
    var foundTarget = false
}
