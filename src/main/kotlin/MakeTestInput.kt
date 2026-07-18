import java.io.ByteArrayInputStream
import java.io.InputStream

internal fun makeGraphTestInput(testCases: Int, nodes: Int, edges: Int, includeTestCases: Boolean = true): InputStream {
    val sb = StringBuilder()
    if (includeTestCases) {
        sb.appendLine(testCases)
    }
    repeat(testCases) {
        val rng = kotlin.random.Random(it)
        sb.appendLine("$nodes $edges")
        repeat(edges) {
            val u = rng.nextInt(nodes - 1) + 1
            val v = rng.nextInt(nodes - 1) + 1
            sb.appendLine("$u $v")
        }
    }
    return ByteArrayInputStream(sb.toString().toByteArray())
}

internal fun makeStringsTestInput(lines: List<String>): InputStream {
    val sb = StringBuilder()
    lines.forEach { line ->
        sb.appendLine(line)
    }
    return ByteArrayInputStream(sb.toString().toByteArray())
}