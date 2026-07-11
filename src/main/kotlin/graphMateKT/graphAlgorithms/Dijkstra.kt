package graphMateKT.graphAlgorithms

import graphMateKT.Edge
import graphMateKT.graphClasses.AdjacencyList
import java.util.*

internal class Dijkstra(private val graph: AdjacencyList) {
    private var r = GraphSearchResults(graph.size)
    fun dijkstra(start: Int): GraphSearchResults {
        r =  GraphSearchResults(graph.size)
        r.distances[start] = 0.0
        val pq = PriorityQueue<Edge> { a, b -> a.first.compareTo(b.first) }
        pq.add(Edge(0.0, start))
        while (pq.isNotEmpty()) {
            val u = pq.poll().second
            if (r.visited[u]) continue
            r.visited[u] = true
            r.currentVisited.add(u)
            graph.forEachEdge(u){ d, v ->
                val newDistance = r.distances[u] + d
                if (newDistance < r.distances[v]) {
                    r.distances[v] = newDistance
                    r.parents[v] = u
                    if (!r.visited[v]) {
                        pq.add(Edge(newDistance, v))
                    }
                }
            }
        }
        r.processedOrder = r.currentVisited
        return r
    }
}
