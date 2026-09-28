package graphMateKT.graphAlgorithms

import graphMateKT.UnboxedEdges
import graphMateKT.graphClasses.AdjacencyList
import graphMateKT.graphClasses.AdjacencyListFlattened
import java.util.*

internal fun prims(graph: AdjacencyList): Pair<Double, AdjacencyList> {
    if (graph.size == 0) error("The graph is empty. Cannot do minimumSpanningTree")

    val visited = BooleanArray(graph.size)
    val connections = UnboxedEdges()
    val pq = PriorityQueue<Triple<Int, Int, Double>> { a, b -> a.third.compareTo(b.third) }
    var totalWeight = 0.0

    visited[0] = true
    graph.forEachEdge(0) { weight, to ->
        pq.add(Triple(0, to, weight))
    }
    var c = 0
    while (c < graph.size - 1) {
        if (pq.isEmpty()) error("The graph is not fully connected. Cannot do minimumSpanningTree")
        val (u, v, w) = pq.poll()
        if (visited[v]) continue
        visited[v] = true
        c++
        totalWeight += w

        connections.addEdge(u, v, w)
        graph.forEachEdge(v) { weight, next ->
            if (!visited[next]) {
                pq.add(Triple(v, next, weight))
            }
        }
    }

    return totalWeight to AdjacencyListFlattened(graph.size, connections)
}
