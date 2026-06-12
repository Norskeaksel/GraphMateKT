package graphMateKT.solutions

import graphMateKT.graphClasses.IntGraph
import graphMateKT.readInt
import graphMateKT.readInts

internal fun  main() {
    val ans = buttonbashing()
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/buttonbashing */
internal fun  buttonbashing(): String {
    val n = readInt()
    val ans = StringBuilder()
    repeat(n) {
        val done = BooleanArray(3601)
        val (nrOfButtons, target) = readInts(2)
        val buttons = readInts(nrOfButtons)
        val g = IntGraph(3601, 3601 * 3600)
        val q = ArrayDeque<Int>()
        q.add(0)
        while (q.isNotEmpty()) {
            val current = q.removeFirst()
            if (done[current]) continue
            buttons.forEach { b ->
                val v = (current + b).coerceIn(0..3600)
                g.addEdge(current, v)
                if (!done[v])
                    q.add(v)
            }
            done[current] = true
        }
        g.bfs(0, target)
        var newTarget = target
        while (g.distanceTo(newTarget).toInt() == Int.MAX_VALUE) {
            newTarget++.coerceAtMost(3600)
            if (newTarget == 3600)
                break
        }
        val buttonPresses = g.distanceTo(newTarget).toInt()
        val overshooting = newTarget - target
        ans.appendLine("$buttonPresses $overshooting")
    }
    return ans.toString()
}
