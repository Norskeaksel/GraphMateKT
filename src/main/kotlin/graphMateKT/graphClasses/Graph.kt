package graphMateKT.graphClasses

import graphMateKT.Edges

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
 * graph.dijkstra("A", "B")
 * // NOTE: visualizeGraph() requires the smartgraph.css and smartgraph.properties files to be added to the root of your project.
 * graph.visualizeGraph() // Find the needed files here: https://github.com/Norskeaksel/GraphMateKT
 * ```
 *
 * @param debugTimeUse If true, the time taken by each graph algorithm is printed to the standard error stream. Defaults to false.
 */
class Graph(debugTimeUse: Boolean = false) : BaseGraph<Any>(debugTimeUse) {
    private var nrOfNodes = 0
    private val node2id = mutableMapOf<Any, Int>()
    private val id2Node = mutableMapOf<Int, Any>()
    private val localAdjacencyList = mutableListOf<Edges>()
    private var adjacencyListIsFinalized = true

    private fun getOrAddNodeId(node: Any): Int {
        return node2id[node] ?: addNode(node).run { node2id[node]!! }
    }

    override fun addNode(node: Any) {
        if (node2id.containsKey(node)) {
            //System.err.println("Warning: The node already exists, it can't be added again")
            return
        }
        node2id[node] = nrOfNodes
        id2Node[nrOfNodes++] = node
        localAdjacencyList.add(mutableListOf())
        adjacencyListIsFinalized = false
    }

    override fun addEdge(node1: Any, node2: Any, weight: Double) {
        val id1 = getOrAddNodeId(node1)
        val id2 = getOrAddNodeId(node2)
        localAdjacencyList[id1].add(weight to id2)
        edgesCount++
        adjacencyListIsFinalized = false
    }

    override fun addEdge(node1: Any, node2: Any) {
        addEdge(node1, node2, 1.0)
    }

    override fun node2Id(node: Any): Int? = node2id[node]
    override fun id2Node(id: Int): Any? = id2Node[id]
    override fun nodes(): List<Any> = id2Node.values.toList()
    override fun finalizeAdjacencyListIfNeeded() {
        if (adjacencyListIsFinalized) return
        adjacencyList = NestedAdjacencyList(localAdjacencyList)
        adjacencyListIsFinalized = true
    }
}
