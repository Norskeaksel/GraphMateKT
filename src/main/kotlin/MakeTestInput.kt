import java.io.File

fun main(){
    var testCases = 1
    val nodes = 100_000
    val edges = 100_000
    File("src/test/SampleInput/Dominos/input2").printWriter().use { out ->
        out.println(testCases)
        repeat(testCases){
            val rng = kotlin.random.Random(testCases--)
            out.println("$nodes $edges")
            repeat(edges) {
                val u = rng.nextInt(nodes - 1) + 1
                val v = rng.nextInt(nodes - 1) + 1
                out.println("$u $v")
            }
        }
    }
}