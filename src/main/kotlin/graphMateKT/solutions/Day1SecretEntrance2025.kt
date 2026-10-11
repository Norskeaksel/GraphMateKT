package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.graphClasses.Graph
import graphMateKT.graphics.graphGraphics.visualizeGraph
import java.io.InputStream

var inputLines = listOf<String>()
internal fun main() {
    val ans = day1SecretEntrance2025(System.`in`)
    println(ans)
    System.out.flush()
}

private data class Node(val index: Int, val value: Int)
private data class Node2(val index: Int, val value: Int, val targetValue: Int)

private fun nextNode(node: Node): List<Node> {
    if (node.index >= inputLines.size)
        return emptyList()
    val rotation = inputLines[node.index]
    val direction = rotation[0]
    val nrOfRotations = rotation.drop(1).toInt()
    val nextValue = if (direction == 'R') (node.value + nrOfRotations) else (node.value - nrOfRotations)
    val nextNode = Node(node.index + 1, nextValue)
    return listOf(nextNode)
}

private fun nextNode2(node: Node2): List<Node2> {
    if (node.value == node.targetValue) {
        val nextIndex = node.index + 1
        if (nextIndex >= inputLines.size)
            return emptyList()
        val rotation = inputLines[nextIndex]
        val direction = rotation[0]
        val nextValue = if (direction == 'R') (node.value + 1) else (node.value - 1)
        val nrOfRotations = rotation.drop(1).toInt()
        val nextTargetValue = if (direction == 'R') (node.value + nrOfRotations) else (node.value - nrOfRotations)
        return listOf(Node2(nextIndex, nextValue, nextTargetValue))
    }
    val rotation = inputLines[node.index]
    val direction = rotation[0]
    val nextValue = if (direction == 'R') (node.value + 1) else (node.value - 1)
    return listOf(Node2(node.index, nextValue, node.targetValue))
}

/** Solves https://open.kattis.com/problems/Day1SecretEntrance2025 */
internal fun day1SecretEntrance2025(inputStream: InputStream): Int {
    val scanner = InputReader(inputStream)
    inputLines = generateSequence { scanner.nextString() }.toList()
    val graph = Graph(isSparse = true)
    val startingNode = Node2(-1, 50, 50)
    graph.connectWithRule {
        nextNode2(it as Node2)
    }
    graph.bfs(startingNode)
    return graph.visitedNodes().count { (it as Node2).value.mod(100) == 0 }
}
