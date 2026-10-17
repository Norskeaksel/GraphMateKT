package graphMateKT.solutions

import graphMateKT.graphClasses.Grid
import graphMateKT.readLines

internal fun main() {
    val ans = islandBuses()
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/island */
internal fun islandBuses(): String {
    val input = readLines()
    val maps = input.joinToString("\n").split("\n\n")
    val ans = mutableListOf<String>()
    maps.forEachIndexed { i, mapString ->
        val mapList = mapString.split("\n")
        val islandGrid = Grid(mapList).apply {
            deleteNodesWithData('.')
            deleteNodesWithData('B')
            this.connectGridDefault()
        }
        val bridgesGrid = Grid(mapList).apply {
            deleteNodesWithData('.')
            deleteNodesWithData('X')
            deleteNodesWithData('#')
            this.connectGridDefault()
        }
        val busesGrid = Grid(mapList).apply {
            deleteNodesWithData('.')
            connectWithRule {
                when (it.data) {
                    '#' -> getStraightNeighbours(it).filter { it.data != 'B' }
                    'B' -> getStraightNeighbours(it).filter { it.data != '#' }
                    else -> getStraightNeighbours(it)
                }
            }
        }
        val islands = islandGrid.stronglyConnectedComponents().size
        val bridges = bridgesGrid.stronglyConnectedComponents().size
        val buses = busesGrid.stronglyConnectedComponents().size
        ans.add(
            """
            Map ${i + 1}
            islands: $islands
            bridges: $bridges
            buses needed: $buses
            
        """.trimIndent()
        )
    }
    return ans.joinToString("\n")
}
