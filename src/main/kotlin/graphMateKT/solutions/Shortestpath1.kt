package graphMateKT.solutions
//https://open.kattis.com/problems/shortestpath1

import graphMateKT.graphClasses.IntGraph
import graphMateKT.readInt
import graphMateKT.readInts

internal fun main(){
    while(true){
        val (n,m,q,s) = readInts(4)
        if(n+m+q+s == 0)
            break
        val g = IntGraph(n)
        repeat(m){
            val (u,v,w) = readInts(3)
            g.addEdge(u,v,w.toDouble())
        }
        g.dijkstra(s)
        repeat(q){
            val goal = readInt()
            if(g.distanceTo(goal) == Double.POSITIVE_INFINITY)
                println("Impossible")
            else
                println(g.distanceTo(goal).toInt())
        }
    }
}