import graphMateKT.solutions.distinctiveCharacter


import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test
import graphMateKT.debug
import kotlin.system.measureTimeMillis

class DistinctiveCharacterTest {

    @Test
    fun distinctiveCharactera() {
        val expectedOutput = """00010"""
        File("src/test/SampleInput/DistinctiveCharacter/input1").inputStream().use {
            assertThat(distinctiveCharacter(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun distinctiveCharacterb() {
        val expectedOutput = """1111"""
        File("src/test/SampleInput/DistinctiveCharacter/input2").inputStream().use {
            assertThat(distinctiveCharacter(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun distinctiveCharacterSpeed() {
        val n = 1e5.toInt()
        val input = listOf("$n 20") + List(n) { it.toString(2).padStart(20, '0') }
        makeStringsTestInput(input).use { line ->
            val time = measureTimeMillis {
                distinctiveCharacter(line)
            }
            debug("distinctiveCharacter time use: $time ms")
        }
    }
}