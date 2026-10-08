import graphMateKT.solutions.knightsfen


import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class KnightsfenTest {
    @Test
    fun knightsfena() {
        val expectedOutput = """Solvable in 1 move(s).
"""
        File("src/test/SampleInput/Knightsfen/input1").inputStream().use {
            assertThat(knightsfen(it)).isEqualTo(expectedOutput)
        }
    }



     @Test
    fun knightsfenb() {
        val expectedOutput = """Unsolvable in less than 11 move(s).
Solvable in 7 move(s).
"""
        File("src/test/SampleInput/Knightsfen/input2").inputStream().use {
            measureTimeMillis {
                assertThat(knightsfen(it)).isEqualTo(expectedOutput)
            }.also { println("knightsfenb took $it ms") }
        }
    }

    @Test
    fun knightsfenc() {
        val expectedOutput = """Solvable in 2 move(s).
"""
        File("src/test/SampleInput/Knightsfen/input3").inputStream().use {
            assertThat(knightsfen(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun knightsfend() {
        val expectedOutput = """Solvable in 7 move(s).
"""
        File("src/test/SampleInput/Knightsfen/input4").inputStream().use {
            assertThat(knightsfen(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun knightsfene() {
        val expectedOutput = """Solvable in 4 move(s).
"""
        File("src/test/SampleInput/Knightsfen/input5").inputStream().use {
            assertThat(knightsfen(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun knightsfenf() {
        val expectedOutput = """Solvable in 3 move(s).
"""
        File("src/test/SampleInput/Knightsfen/input6").inputStream().use {
            assertThat(knightsfen(it)).isEqualTo(expectedOutput)
        }
    }
}