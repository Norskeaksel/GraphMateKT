package graphMateKT

internal data class GraphSearchResults(private val initialGraphSize: Int) {
    val visited = IntArrayList(initialGraphSize)
    val distances = DoubleArray(initialGraphSize)
    val parents = IntArrayList(initialGraphSize)
    var depth: Int? = null
    var currentVisited = IntArrayList()
    var processedOrder = IntArrayList()
    var foundTarget = false
}
