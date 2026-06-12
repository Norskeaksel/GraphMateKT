package graphMateKT.graphAlgorithms

import graphMateKT.graphClasses.AdjacencyList
import kotlin.math.min

internal class FloydWarshall(val graph: AdjacencyList) {
    val n = graph.size
    private val distances = Array(n) { DoubleArray(n) { Double.POSITIVE_INFINITY } }

    init {
        graph.nodes().forEachIndexed { u, node ->
            distances[u][u] = 0.0
            graph.forEachEdge(node){ d, v ->
                distances[u][v] = d
            }
        }
    }

    fun floydWarshall(): Array<DoubleArray> {
        repeat(n) { k ->
            repeat(n) { i ->
                repeat(n) { j ->
                    distances[i][j] = min(distances[i][j], distances[i][k] + distances[k][j])
                }
            }
        }
        return distances
    }
    fun printDistances() {
        distances.forEachIndexed { i, row ->
            System.err.println("$i: ${row.joinToString(", ")}")
        }
    }
}
