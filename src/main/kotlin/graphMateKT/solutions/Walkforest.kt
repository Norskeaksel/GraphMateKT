package graphMateKT.solutions

import graphMateKT.graphClasses.Graph
import graphMateKT.graphClasses.IntGraph
import graphMateKT.readInt
import graphMateKT.readInts

internal fun main() {
    val ans = walkforest()
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/walkforest */
internal fun walkforest(): String {
    val ans = StringBuilder()
    while (true) {
        val n = readInt()
        if (n == 0)
            break
        val m = readInt()
        val g = IntGraph(n + 1, m * 2)
        repeat(m) {
            val (a, b, l) = readInts(3)
            g.connect(a, b, l)
        }
        g.dijkstra(2, 1)
        val dag = makeDAG(g)
        //g.visualize(true)
        //dag.visualize()
        ans.appendLine(nrOfPaths(dag))
    }
    return ans.toString()
}

private fun makeDAG(g: IntGraph): Graph {
    val newG = Graph()
    val distances =  DoubleArray(g.size())
    g.nodes().forEach { distances[it] = g.distanceTo(it) }
    g.nodes().forEach { u ->
        g.edges(u).forEach { (w, v) ->
            if (distances[v] < distances[u])
                newG.addEdge(u, v, w)
        }
    }
    return newG
}

private fun nrOfPaths(g: Graph): Int {
    val nodesSorted = g.topologicalSort().reversed()
    val dp = IntArray(1001)
    dp[1] = 1
    nodesSorted.forEach { u ->
        u as Int
        val neighbours = g.neighbours(u)
        neighbours.forEach { v ->
            v as Int
            dp[v] += dp[u]
        }
    }
    return dp[2]
}
