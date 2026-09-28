package graphMateKT.graphClasses

class AdjacecnyListDynamic(override val size: Int, val getNeighbours: (Int) -> List<Int>) : AdjacencyList {
    override fun forEachNeighbour(node: Int, action: (Int) -> Unit) {
        getNeighbours(node).forEach(action)
    }

    override fun forEachEdge(node: Int, action: (Double, Int) -> Unit) {
        action(1.0, node)
    }
}