package graphMateKT.solutions

import graphMateKT.graphClasses.Graph
import graphMateKT.readInt
import graphMateKT.readString

internal fun main() {
    val ans = favourable()
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/favourable */
internal fun favourable(): String {
    val n = readInt()
    val ans = StringBuilder()
    repeat(n){
        val s = readInt()
        val g = Graph()
        val start = "1"
        repeat(s){
            val words = readString().split(" ")
            val from = words[0]
            val neighbours = words.drop(1)
            neighbours.forEach {
                g.addEdge(from, it)
                if(it == "favourably" && g.neighbours(it).none { it == "trueEnding" }){
                    g.addEdge(it, "trueEnding")
                }
            }
        }
        // g.visualizeGraph()
        g.addNode("trueEnding")
        ans.appendLine(g.nrOfPaths(start, "trueEnding").toString())
    }
    return ans.toString()
}
