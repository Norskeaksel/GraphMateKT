package graphMateKT.graphAlgorithms

import graphMateKT.Edge
import graphMateKT.Edges
import graphMateKT.graphClasses.AdjacencyList
import java.util.*

internal fun prims(graph: AdjacencyList): Pair<Double, MutableList<Edges>> {
    if (graph.size == 0) error("The graph is empty. Cannot do minimumSpanningTree")

    val visited = BooleanArray(graph.size)
    val connections = MutableList<Edges>(graph.size) { mutableListOf() }
    val pq = PriorityQueue<Triple<Double, Int, Int>> { a, b -> a.first.compareTo(b.first) }
    var totalWeight = 0.0

    visited[0] = true
    graph.forEachEdge(0) { weight, to ->
        pq.add(Triple(weight, 0, to))
    }
    var c = 0
    while (c < graph.size - 1) {
        if (pq.isEmpty()) error("The graph is not fully connected. Cannot do minimumSpanningTree")
        val (w, u, v) = pq.poll()
        if (visited[v]) continue
        visited[v] = true
        c++
        totalWeight += w

        connections[u].add(Edge(w, v))
        connections[v].add(Edge(w, u))

        graph.forEachEdge(v) { weight, next ->
            if (!visited[next]) {
                pq.add(Triple(weight, v, next))
            }
        }
    }

    return Pair(totalWeight, connections)
}
