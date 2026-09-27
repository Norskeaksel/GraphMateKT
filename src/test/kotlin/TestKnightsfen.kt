import graphMateKT.solutions.knightsfen
        

import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test
import graphMateKT.debug
import kotlin.system.measureTimeMillis

class KnightsfenTest {

    /* @Test
    fun knightsfena() {
        val expectedOutput = """Unsolvable in less than 11 move(s).
Solvable in 7 move(s).
"""
        File("src/test/SampleInput/Knightsfen/input1").inputStream().use{
            assertThat(knightsfen(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun knightsfenSpeed() {
        val input = listOf("1") + List(100) { "1".repeat(100) }
        val expectedOutput = """"""
        makeStringsTestInput(input).use { line ->
            val time = measureTimeMillis {
                assertThat(knightsfen(line)).isEqualTo(expectedOutput)
            }
            debug("knightsfen time use: $time ms")
        }
    }*/
}