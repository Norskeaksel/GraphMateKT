package graphMateKT.graphClasses

import graphMateKT.Components
import graphMateKT.Not
import graphMateKT.graphAlgorithms.twoSat
import graphMateKT.not

internal class ClauseGraph {
    private val nodes = mutableListOf<Any?>()

    // private var edgeNr = 1.0
    private var id = 1
    private val node2id = mutableMapOf<Any, Int>()
    private val id2Node = mutableMapOf<Int, Any>()
    val dependencyGraph = Graph()

    fun addClause(clause: ClauseGraph.() -> Unit) {
        this.clause()
    }

    fun setTrue(node: Any) {
        addClause { node Or node }
    }

    private fun getUVIDPairs(node1: Any, node2: Any): Pair<Int, Int> {
        fun getNodeId(node: Any): Int {
            val id = node2id[if (node is Not) node.node else node] ?: /*error("Node $node not found") */ run {
                if (node is Not) {
                    addNode(node.node)
                    node2id[node.node]!!
                } else {
                    addNode(node)
                    node2id[node]!!
                }
            }
            return if (node is Not) -id else id
        }

        val u = getNodeId(node1)
        val v = getNodeId(node2)
        return u to v
    }

    fun not(node: Any) = Not(node)

    /** a V b <--> -a -> b and -b -> a */
    infix fun Any.Or(other: Any) {
        val (u, v) = getUVIDPairs(this, other)
        dependencyGraph.addEdge(-u, v)
        dependencyGraph.addEdge(-v, u)
    }

    /** a ^ b <--> (a V b) and (-a V -b) */
    infix fun Any.Xor(other: Any) {
        this Or other
        val newThis = if (this is Not) this.node else this
        val newOther = if (other is Not) other.node else other
        !newThis Or !newOther
    }

    fun addNode(node: Any) {
        if (node2id.containsKey(node)) {
            System.err.println("Warning: The node already exists, it can't be added again")
            return
        }
        nodes.add(node)
        node2id[node] = id
        id2Node[id] = node
        dependencyGraph.addNode(id)
        dependencyGraph.addNode(-id)
        id++
    }

    fun nodes() = nodes.filterNotNull()

    fun twoSat(): Pair<Components, Map<Any, Boolean>>? {
        val (intSCC, integerTruthMap) = twoSat(dependencyGraph) ?: return null
        val scc = intSCC.map { component ->
            component.map { if (it > 0) id2Node[it]!! else not(id2Node[-it]!!) }
        }
        val truthMap = integerTruthMap.filter { it.key > 0 }.mapKeys { k -> id2Node[k.key]!! }
        return scc to truthMap
    }
}
