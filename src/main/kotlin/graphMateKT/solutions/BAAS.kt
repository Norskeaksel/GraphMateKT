package graphMateKT.solutions

import graphMateKT.graphClasses.IntGraph
import graphMateKT.readInt
import graphMateKT.readInts
import kotlin.math.min


internal fun main() {
    val ans = baas()
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/baas
 *  Note, this solution is close to the time limit. To make it pass, the submitted solution must cut away all library
 *  functions that's not needed. */
internal fun baas(): Int {
    val n = readInt()
    val nrOfEdges = n * (n - 1)
    val intGraph = IntGraph(n, nrOfEdges)
    val stepTime = readInts(n)
    repeat(n) { step_i ->
        val c_i = readInt()
        repeat(c_i) {
            val a_j = readInt() - 1
            intGraph.addEdge(step_i, a_j)
        }
    }
    var optimizedTime = Int.MAX_VALUE
    val topologicalOrder = intGraph.topologicalSort()
    val totalStepTime = IntArray(n)
    topologicalOrder.indices.forEach {
        topologicalOrder.forEachIndexed { i, node ->
            totalStepTime[node] = stepTime[node] + (intGraph.neighbours(node).maxOfOrNull { totalStepTime[it] } ?: 0)
            if (i == it)
                totalStepTime[node] -= stepTime[node]
        }
        optimizedTime = min(optimizedTime, totalStepTime[n - 1])
    }
    return optimizedTime
}
