package graphMateKT

internal data class GraphSearchResults(private val initialGraphSize: Int) {
    val visited = IntArrayList(initialGraphSize)
    val distances = DoubleArrayList(initialGraphSize, Double.POSITIVE_INFINITY)
    val parents = IntArrayList(initialGraphSize, -1)
    var depth: Int? = null
    var currentlyVisited = IntArrayList()
    var processedOrder = IntArrayList()
    var foundTarget = false
}
