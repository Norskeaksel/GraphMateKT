package graphMateKT.solutions

import graphMateKT.graphClasses.Grid
import graphMateKT.Tile
import graphMateKT.readString

internal fun main() {
    val ans = day3GearRatios2023()
    println(ans)
    System.out.flush()
}

/** Solves https://adventofcode.com/2023/day/3 */
internal fun day3GearRatios2023(): Int {
    val lines = mutableListOf<String>()
    while (true) {
        val line = readString()
        if (line == "0") break
        lines.add(line)
    }
    val grid = Grid(lines)
    grid.deleteNodesWithData('.')
    val startNodes = mutableListOf<Tile>()
    grid.nodes().forEach { node ->
        if (!node.dataIsDigit())
            startNodes.add(node)
        /*if (node.data == '*') {
            val neighbours = grid.getAllNeighbours(node)
            val topNodes = neighbours.filter { it.y < node.y }
            val midNodes = neighbours.filter { it.y == node.y }
            val bottomNodes = neighbours.filter { it.y > node.y }
            var nrOfAdjacentNumbers = midNodes.count { it.dataIsDigit() }
            listOf(topNodes, bottomNodes).forEach { nodes ->
                when (nodes.size) {
                    1 -> nrOfAdjacentNumbers += 1
                    3 -> nrOfAdjacentNumbers += 1
                    2 -> nrOfAdjacentNumbers += if (nodes.any { it.x == node.x }) 1 else 2
                }
            }
            if (nrOfAdjacentNumbers == 2) startNodes.add(node)
        }*/
    }
    grid.connectWithRule { t ->
        grid.getAllNeighbours(t).filter { it.dataIsDigit() }
    }
    grid.print()
    grid.bfs(startNodes)
    val numbers = mutableListOf<Int>()
    var previousX = -1
    var nextNumber = "0"
    val digitsNodes = grid.visitedNodes().filter { it.dataIsDigit() }
    digitsNodes.forEach { node ->
        if (node.x == previousX + 1)
            nextNumber += node.data
        else {
            numbers.add(nextNumber.toInt())
            nextNumber = node.data.toString()
        }
        previousX = node.x
    }
    numbers.add(nextNumber.toInt())
    // debug(numbers)
    // grid.visualize()
    return numbers.sum()
}
