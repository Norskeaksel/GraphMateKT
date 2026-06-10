package graphMateKT.solutions

import InputReader
import graphMateKT.graphClasses.IntGraph
import java.io.InputStream
import kotlin.math.min


internal fun main() {
    val ans = baas(System.`in`)
    println(ans)
    System.out.flush()
}

// Solves https://open.kattis.com/problems/baas
internal fun baas(inputStream: InputStream): Int {
    val scanner = InputReader(inputStream)
    val n = scanner.nextInt()
    val nrOfEdges = n * (n - 1)
    val intGraph = IntGraph(n, nrOfEdges)
    val stepTime = scanner.nextIntArray(n)
    repeat(n) { step_i ->
        val c_i = scanner.nextInt()
        repeat(c_i) {
            val a_j = scanner.nextInt() - 1
            intGraph.addEdge(step_i, a_j)
        }
    }
    var optimizedTime = Int.MAX_VALUE
    val topologicalOrder = intGraph.topologicalSort()
    val totalStepTime = IntArray(n)
    topologicalOrder.indices.forEach {
        topologicalOrder.forEachIndexed { i, node ->
            var maxTime = 0
            intGraph.forEachNeighbour(node) { neighbor ->
                maxTime = maxOf(maxTime, totalStepTime[neighbor])
            }
            totalStepTime[node] = stepTime[node] + maxTime
            if (i == it)
                totalStepTime[node] -= stepTime[node]
        }
        optimizedTime = min(optimizedTime, totalStepTime[n - 1])
    }
    return optimizedTime
}
