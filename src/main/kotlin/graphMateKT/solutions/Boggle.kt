package graphMateKT.solutions

import graphMateKT.graphClasses.Grid
import graphMateKT.graphClasses.PrefixTree
import graphMateKT.Tile
import graphMateKT.TrieNode
import graphMateKT.readInt
import graphMateKT.readString

//Contraints: 4x4 grid, letter only valid once per word
private val wordScore = IntArray(9).apply {
    this[3] = 1
    this[4] = 1
    this[5] = 2
    this[6] = 3
    this[7] = 5
    this[8] = 11
}

internal fun main() {
    val ans = boggle()
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/boggle */
internal fun boggle(): String {
    val w = readInt()

    val trie = PrefixTree()
    repeat(w) {
        val word = readString()
        trie.add(word)
    }
    readString()
    val b = readInt()
    val ans = StringBuilder()
    repeat(b) { r ->
        var points = 0
        val foundWords = mutableSetOf<String>()
        var longestWordLength = 0
        val lines = mutableListOf<String>()
        repeat(4) {
            lines.add(readString())
        }
        val grid = Grid(lines)
        repeat(4) { y ->
            repeat(4) { x ->
                foundWords.addAll(trieBFS(trie, grid, grid.xy2Node(x, y)))

            }
        }
        foundWords.forEach { word ->
            points += wordScore[word.length].coerceAtLeast(0)
            longestWordLength = longestWordLength.coerceAtLeast(word.length)
        }
        val longestWord = foundWords.filter { it.length == longestWordLength }.min()
        ans.appendLine("$points $longestWord ${foundWords.size}")
        if (r < b - 1)
            readString()
    }
    return ans.toString()
}

private fun trieBFS(trie: PrefixTree, grid: Grid, startTile: Tile): Set<String> {
    val foundWords = mutableSetOf<String>()
    val queue = ArrayDeque<Pair<TrieNode, List<Tile>>>()
    val startChar = startTile.data
    val startNode = trie.root.children[startChar] ?: return emptySet()
    queue.add(startNode to listOf(startTile))
    while (queue.isNotEmpty()) {
        val (node, path) = queue.removeFirst()
        if (node.isTerminal) {
            val word = path.joinToString("") { it.data.toString() }
            foundWords.add(word)
        }

        grid.getAllNeighbours(path.last()).forEach { neighbour ->
            val nextChar = neighbour.data
            val nextNode = node.children[nextChar]
            if (nextNode != null && neighbour !in path) {
                queue.add(nextNode to path + listOf(neighbour))
            }
        }
    }

    return foundWords
}
