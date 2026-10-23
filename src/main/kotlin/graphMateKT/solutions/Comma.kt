package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.debug
import graphMateKT.graphClasses.IntGraph
import java.io.InputStream

internal fun main() {
    val ans = comma(System.`in`)
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/comma */
internal fun comma(inputStream: InputStream): String {
    val scanner = InputReader(inputStream)
    val originalString = scanner.nextLine()!!
    val chunks = originalString.trim().split(" ")
    debug(chunks.joinToString(" "))
    val wordIndexes = mutableMapOf<String, MutableList<Int>>()
    chunks.forEachIndexed { index, chunk ->
        wordIndexes.getOrPut(chunk.trimChunk()) { mutableListOf() }.add(index)
    }
    val wordsWithPrecedingComma = mutableSetOf<String>()
    val wordsWithTrailingComma = mutableSetOf<String>()
    chunks.windowed(2, 1).forEach { (first, second) ->
        if (first.last() == ',') {
            wordsWithTrailingComma.add(first.trimChunk())
            wordsWithPrecedingComma.add(second.trimChunk())
        }
        if (second.last() == ',') {
            wordsWithTrailingComma.add(second.trimChunk())
        }
    }
    val startingNodes = wordsWithTrailingComma.flatMap { wordIndexes[it]!! }

    val trimmedWords = originalString.replace(",", "").replace(".", "").trim().split(" ")
    val graph = IntGraph(trimmedWords.size)
    graph.connectWithRule { u ->
        debug("Current word: ${trimmedWords[u]} index: $u")
        val neighbours = mutableListOf<Int>()
        val word = trimmedWords[u]
        if (word in wordsWithTrailingComma) {
            neighbours.addAll(wordIndexes[word]!! + wordIndexes[word]!!.map { if (it < trimmedWords.size - 1) it + 1 else  it})
            neighbours.forEach {
                if(it < trimmedWords.size - 1) {
                    wordsWithPrecedingComma.add(trimmedWords[it + 1])
                }
            }
        }
        if (word in wordsWithPrecedingComma) {
            neighbours.addAll((wordIndexes[word]!!).map { if (it > 0) it - 1 else 0 })
        }
        debug("Adding words: ${neighbours.map { trimmedWords[it] }.joinToString(" ")} indexes: ${neighbours.joinToString(" ")}")
        neighbours
    }

    graph.bfs(startingNodes)
    wordsWithTrailingComma.addAll(graph.visitedNodes().map { chunks[it].trimChunk() })
    return chunks.joinToString(" ") { word ->
        if (word.trimChunk() in wordsWithTrailingComma) {
            if (word.last() in listOf(',', '.'))
                word
            else
                "$word,"
        } else {
            word
        }
    }
}

internal fun String.trimChunk() = if (last() in listOf(',', '.')) dropLast(1) else this