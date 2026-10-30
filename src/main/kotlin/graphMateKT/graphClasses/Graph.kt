package graphMateKT.graphClasses

/** A general graph class that represents nodes of any datatype.
 *
 * Any new node is given an ID upon creation, which is used to build an adjacency list. The class maintains internal
 * maps between ID's and nodes and vice versa.
 *
 * <i>Example usage:</i>
 *
 * ```
 * val graph = Graph()
 * graph.addEdge("A", "B", 5.0)
 * graph.addEdge("A", "C", 2.0)
 * graph.addEdge("C", "B", 1.0)
 * graph.shortestPathsFrom("A", "B")
 * // NOTE: visualizeGraph() requires the smartgraph.css and smartgraph.properties files to be added to the root of your project.
 * graph.visualizeGraph() // Find the needed files here: https://github.com/Norskeaksel/GraphMateKT
 * ```
 *
 * @param debugTimeUse If true, the time taken by each graph algorithm is printed to the standard error stream. Defaults to false.
 */
class Graph(debugTimeUse: Boolean = false) : BaseGraph<Any>(0, debugTimeUse, false) {
    private val node2id = HashMap<Any, Int>()
    private val id2Node = mutableListOf<Any>()

    private fun getOrAddNodeId(node: Any): Int {
        return node2id[node] ?: addNode(node).run { node2id[node]!! }
    }

    override fun addNode(node: Any) {
        if (node2id.containsKey(node)) {
            return
        }
        node2id[node] = nrOfNodes++
        id2Node.add(node)
        if (!adjacencyList.isDynamic) {
            adjacencyListIsFinalized = false
        }
    }

    /** Adds a directed edge from node1 to node2. Nodes that don't exist in the graph yet are created.
     *
     * @param node1 The starting node of the edge.
     * @param node2 The ending node of the edge.
     * @param weight The weight of the edge, for example used to calculate distances with shortestPathsFrom. Defaults to 1.0. */
    override fun addEdge(node1: Any, node2: Any, weight: Double) {
        val id1 = getOrAddNodeId(node1)
        val id2 = getOrAddNodeId(node2)
        edges.addEdge(id1, id2, weight)
        edgesCount++
        adjacencyListIsFinalized = false
    }

    override fun node2IdOrNull(node: Any): Int? = node2id[node]
    override fun node2Id(node: Any): Int = node2id[node] ?: (nrOfNodes++).also {
        node2id[node] = it
        id2Node.add(node)
    }

    override fun id2NodeOrNull(id: Int): Any? = id2Node.getOrNull(id)
    override fun id2Node(id: Int): Any = id2Node.getOrNull(id) ?: error("Node with ID $id not found in graph")
    override fun nodes(): List<Any> = id2Node

    override fun connectWithRule(getNeighbours: (node: Any) -> List<Any>) {
        isSparse = true
        super.connectWithRule(getNeighbours)
    }
}
