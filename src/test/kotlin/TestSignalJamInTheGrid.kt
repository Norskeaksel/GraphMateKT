import graphMateKT.solutions.signalJamInTheGrid


import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test
import graphMateKT.debug
import kotlin.system.measureTimeMillis

class SignalJamInTheGridTest {

    @Test
    fun SignalJamInTheGrida() {
        val expectedOutput = 4
        File("src/test/SampleInput/SignalJamInTheGrid/input1").inputStream().use {
            assertThat(signalJamInTheGrid(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun SignalJamInTheGridb() {
        val expectedOutput = -1
        File("src/test/SampleInput/SignalJamInTheGrid/input2").inputStream().use {
            assertThat(signalJamInTheGrid(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun SignalJamInTheGridc() {
        val expectedOutput = 2 * 2 + 2 * 2 + 1
        val time = measureTimeMillis {
            File("src/test/SampleInput/SignalJamInTheGrid/input3").inputStream().use {
                assertThat(signalJamInTheGrid(it)).isEqualTo(expectedOutput)
            }
        }
        debug("SignalJamInTheGrid time use: $time ms")
    }


    @Test
    fun SignalJamInTheGridd() {
        val expectedOutput = 0
        File("src/test/SampleInput/SignalJamInTheGrid/input4").inputStream().use {
            assertThat(signalJamInTheGrid(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun SignalJamInTheGridSpeed() {
        val expectedOutput = 2 * 98 + 2 * 98 + 1
        val time = measureTimeMillis {
            File("src/test/SampleInput/SignalJamInTheGrid/inputSpeed").inputStream().use {
                assertThat(signalJamInTheGrid(it)).isEqualTo(expectedOutput)
            }
        }
        debug("SignalJamInTheGrid time use: $time ms")
    }
}