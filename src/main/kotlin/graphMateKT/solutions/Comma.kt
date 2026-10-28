package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.graphClasses.IntGraph
import java.io.InputStream

internal fun main() {
    val ans = comma(System.`in`)
    println(ans)
    System.out.flush()
}

private val wordsWithPrecedingComma = mutableSetOf<String>()
private val wordsWithTrailingComma = mutableSetOf<String>()

/** Solves https://open.kattis.com/problems/comma */
internal fun comma(inputStream: InputStream): String {
    val scanner = InputReader(inputStream)
    val originalString = scanner.nextLine()!!
    val chunks = originalString.trim().split(" ")
    // debug(chunks.joinToString(" "))
    val nextIndexes = findNextIndexesOfWords(chunks)
    val startingNodes = chunks.mapIndexedNotNull { i, chunk -> if (chunk.last() == ',') i else null }
    initializeWordSets(chunks)

    val graph = IntGraph(chunks.size)
    graph.connectWithRule { u ->
        val (pChunk, currentChunk, nChunk) = Triple(chunks.getOrNull(u - 1), chunks[u], chunks.getOrNull(u + 1))
        val (pWord, currentWord, nWord) = Triple(pChunk?.trimChunk(), currentChunk.trimChunk(), nChunk?.trimChunk())
        //debug("Current: $currentChunk")
        val neighbours = mutableListOf(nextIndexes[u])
        if (currentWord in wordsWithTrailingComma) {
            if (currentChunk.last() != '.') {
                nWord?.let {
                    neighbours.add(u + 1)
                    wordsWithPrecedingComma.add(it)
                }
            }
        }
        if (currentWord in wordsWithPrecedingComma) {
            pChunk?.let {
                if (it.last() != '.') {
                    neighbours.add(u - 1)
                    wordsWithTrailingComma.add(pWord!!)
                }
            }
        }
        //debug("Neighbours for $currentChunk: ${neighbours.map { chunks[it] }.joinToString(" ")}")
        neighbours
    }

    repeat(8) { // 1, 2 and 4 fail, but 8 passes
        graph.bfs(startingNodes)
    }
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

private fun String.trimChunk() = if (last() in listOf(',', '.')) dropLast(1) else this

private fun findNextIndexesOfWords(chunks: List<String>): IntArray {
    val wordIndexes = mutableMapOf<String, MutableList<Int>>()
    chunks.forEachIndexed { index, chunk ->
        wordIndexes.getOrPut(chunk.trimChunk()) { mutableListOf() }.add(index)
    }
    val nextIndexes = IntArray(chunks.size)
    wordIndexes.values.forEach { indexes ->
        indexes.forEachIndexed { idx, chunkIndex ->
            nextIndexes[chunkIndex] = indexes[(idx + 1) % indexes.size]
        }
    }
    return nextIndexes
}

private fun initializeWordSets(chunks: List<String>) {
    chunks.windowed(2, 1).forEach { (first, second) ->
        if (first.last() == ',') {
            wordsWithTrailingComma.add(first.trimChunk())
            wordsWithPrecedingComma.add(second.trimChunk())
        }
        if (second.last() == ',') {
            wordsWithTrailingComma.add(second.trimChunk())
        }
    }
}
