import graphMateKT.solutions.fulltank
        

import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test
import graphMateKT.debug
import kotlin.system.measureTimeMillis

class FulltankTest {

    @Test
    fun fulltanka() {
        val expectedOutput = """170
impossible
"""
        File("src/test/SampleInput/Fulltank/input1").inputStream().use{
            assertThat(fulltank(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun fulltankCounterExample() {
        // Two cities, one road of length 1, tank capacity 1. Buy 1 unit in city 0 (cost 1) and drive.
        val expectedOutput = """1
"""
        File("src/test/SampleInput/Fulltank/input2").inputStream().use {
            assertThat(fulltank(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun fulltankCounterExample2() {
        val expectedOutput = """2
impossible
"""
        File("src/test/SampleInput/Fulltank/input3").inputStream().use {
            assertThat(fulltank(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun fulltankCounterExample3() {
        val expectedOutput = """impossible
"""
        File("src/test/SampleInput/Fulltank/input4").inputStream().use {
            assertThat(fulltank(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun fulltankCounterExample4() {
        val expectedOutput = """100
"""
        File("src/test/SampleInput/Fulltank/input5").inputStream().use {
            assertThat(fulltank(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun fulltankSpeed() {
        // Upper bounds: n = 1000, m = 10000, q = 100
        // Fuel prices 1..100, road lengths 1..100, tank capacity 1..100
        val n = 1000
        val m = 10000
        val q = 100
        val rng = kotlin.random.Random(42)
        val input = buildList {
            add("$n $m")
            add(List(n) { rng.nextInt(1, 101) }.joinToString(" "))
            repeat(m) {
                val u = rng.nextInt(n)
                var v = rng.nextInt(n)
                while (v == u) v = rng.nextInt(n)
                add("$u $v ${rng.nextInt(1, 101)}")
            }
            add("$q")
            repeat(q) {
                add("${rng.nextInt(1, 101)} ${rng.nextInt(n)} ${rng.nextInt(n)}")
            }
        }
        makeStringsTestInput(input).use { line ->
            val time = measureTimeMillis {
                assertThat(fulltank(line).lines().filter { it.isNotEmpty() }).hasSize(q)
            }
            debug("fulltank time use: $time ms")
        }
    }
}