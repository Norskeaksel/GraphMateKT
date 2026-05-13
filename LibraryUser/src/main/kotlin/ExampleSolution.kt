// Solves https://codeforces.com/problemset/problem/20/C if the GraphMateKTSingleFile code is added
fun main() {
    val (n, m) = readInts(2)
    val g = IntGraph(n + 1)
    repeat(m) {
        val (u, v, w) = readInts(3)
        g.connect(u, v, w.toDouble())
    }
    g.dijkstra(1)
    val path = g.getPath(n)
    if (path.size == 1 && path[0] != 1) {
        println(-1)
        return
    }
    println(path.joinToString(" "))
}
