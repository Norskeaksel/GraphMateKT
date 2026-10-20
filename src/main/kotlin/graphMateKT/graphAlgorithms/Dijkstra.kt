package graphMateKT.graphAlgorithms

import graphMateKT.Edge
import graphMateKT.GraphSearchResults
import graphMateKT.InvalidEdgeException
import graphMateKT.graphClasses.AdjacencyList
import java.util.*

internal class Dijkstra(private val graph: AdjacencyList) {
    private var r = GraphSearchResults(graph.size)
    fun dijkstra(start: Int, target: Int = -1): GraphSearchResults {
        r = GraphSearchResults(graph.size)
        r.distances[start] = 0.0
        val pq = PriorityQueue<Edge> { a, b -> a.first.compareTo(b.first) }
        pq.add(Edge(0.0, start))
        while (pq.isNotEmpty()) {
            val u = pq.poll().second
            if (r.visited[u])
                continue
            r.visited[u] = true
            r.currentVisited.add(u)
            if (u == target) {
                break
            }
            graph.forEachEdge(u) { d, v ->
                val newDistance = r.distances[u] + d
                try {
                    if (newDistance < r.distances[v]) {
                        r.distances[v] = newDistance
                        r.parents[v] = u
                        if (!r.visited[v]) {
                            pq.add(Edge(newDistance, v))
                        }
                        if (v == target) {
                            r.foundTarget = true
                        }
                    }
                } catch (_: ArrayIndexOutOfBoundsException) {
                    throw InvalidEdgeException(u, v)
                }
            }
        }
        r.processedOrder = r.currentVisited
        return r
    }

    fun dijkstraSparse(start: Int, target: Int = -1): GraphSearchResults {
        r = GraphSearchResults(graph.size)
        r.distancesSparse[start] = 0.0
        val pq = PriorityQueue<Edge> { a, b -> a.first.compareTo(b.first) }
        pq.add(Edge(0.0, start))
        while (pq.isNotEmpty()) {
            val u = pq.poll().second
            if (u in r.visitedSparse)
                continue
            r.visitedSparse.add(u)
            r.currentVisited.add(u)
            if (u == target) {
                break
            }
            graph.forEachEdge(u) { d, v ->
                val newDistance = r.distancesSparse[u]!! + d
                try {
                    if (newDistance < (r.distancesSparse[v] ?: Double.POSITIVE_INFINITY)) {
                        r.distancesSparse[v] = newDistance
                        r.parentsSparse[v] = u
                        if (v !in r.visitedSparse) {
                            pq.add(Edge(newDistance, v))
                        }
                        if(v == target) {
                            r.foundTarget = true
                        }
                    }
                } catch (_: ArrayIndexOutOfBoundsException) {
                    throw InvalidEdgeException(u, v)
                }
            }
        }
        r.processedOrder = r.currentVisited
        return r
    }
}
