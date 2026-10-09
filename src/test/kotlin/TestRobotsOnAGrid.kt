import graphMateKT.solutions.robotsOnAGrid
        

import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test
import graphMateKT.debug
import kotlin.system.measureTimeMillis

class RobotsOnAGridTest {

    @Test
    fun RobotsOnAGrida() {
        val expectedOutput = """6"""
        File("src/test/SampleInput/RobotsOnAGrid/input1").inputStream().use{
            assertThat(robotsOnAGrid(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun RobotsOnAGridb() {
        val expectedOutput = """THE GAME IS A LIE"""
        File("src/test/SampleInput/RobotsOnAGrid/input2").inputStream().use{
            assertThat(robotsOnAGrid(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun RobotsOnAGridSpeed() {
        val input = listOf("1000") + List(1000) { ".".repeat(1000) }
        makeStringsTestInput(input).use { line ->
            val time = measureTimeMillis {
               robotsOnAGrid(line)
            }
            debug("RobotsOnAGrid time use: $time ms")
        }
    }
}