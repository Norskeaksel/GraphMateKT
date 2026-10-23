import graphMateKT.solutions.comma
        

import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test
import graphMateKT.debug
import kotlin.system.measureTimeMillis

class CommaTest {

    @Test
    fun commaa() {
        val expectedOutput = """please, sit spot. sit spot, sit. spot, here now, here."""
        File("src/test/SampleInput/Comma/input1").inputStream().use{
            assertThat(comma(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun commab() {
        val expectedOutput = """          one, two. one, tree. four, tree. four, four. five, four. six five."""
        File("src/test/SampleInput/Comma/input2").inputStream().use{
            assertThat(comma(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun commaSpeed() {
        val input = listOf("1") + List(100) { "1".repeat(100) }
        val expectedOutput = """"""
        makeStringsTestInput(input).use { line ->
            val time = measureTimeMillis {
                assertThat(comma(line)).isEqualTo(expectedOutput)
            }
            debug("comma time use: $time ms")
        }
    }
}