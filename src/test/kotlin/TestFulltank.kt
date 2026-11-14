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
    fun fulltankb() {
        val expectedOutput = """1
"""
        File("src/test/SampleInput/Fulltank/input2").inputStream().use {
            assertThat(fulltank(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun fulltankc() {
        val expectedOutput = """2
impossible
"""
        File("src/test/SampleInput/Fulltank/input3").inputStream().use {
            assertThat(fulltank(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun fulltankd() {
        val expectedOutput = """impossible
"""
        File("src/test/SampleInput/Fulltank/input4").inputStream().use {
            assertThat(fulltank(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun fulltanke() {
        val expectedOutput = """100
"""
        File("src/test/SampleInput/Fulltank/input5").inputStream().use {
            assertThat(fulltank(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun fulltankf() {
        val expectedOutput = """0
"""
        File("src/test/SampleInput/Fulltank/input6").inputStream().use {
            assertThat(fulltank(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun fulltankSpeed() {
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