import java.io.ByteArrayInputStream
import java.io.InputStream

internal fun makeTestInput(testCases: Int, nodes: Int, edges: Int, includeTestCases: Boolean = true): InputStream {
    val sb = StringBuilder()
    if(includeTestCases){
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