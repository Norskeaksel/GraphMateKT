import graphMateKT.solutions.ticketcompleted


import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import java.io.File
import org.junit.jupiter.api.Test
import graphMateKT.debug
import kotlin.system.measureTimeMillis

class TicketcompletedTest {

    @Test
    fun ticketcompleteda() {
        val expectedOutput = 0.33333333333333333333
        File("src/test/SampleInput/Ticketcompleted/input1").inputStream().use {
            assertThat(ticketcompleted(it)).isCloseTo(expectedOutput, within(1e-7))
        }
    }

    @Test
    fun ticketcompletedb() {
        val expectedOutput = 0.4
        File("src/test/SampleInput/Ticketcompleted/input2").inputStream().use {
            assertThat(ticketcompleted(it)).isCloseTo(expectedOutput, within(1e-7))
        }
    }

    @Test
    fun ticketcompletedc() {
        val expectedOutput = 0.42857142857142857143
        File("src/test/SampleInput/Ticketcompleted/input3").inputStream().use {
            assertThat(ticketcompleted(it)).isCloseTo(expectedOutput, within(1e-7))
        }
    }

    @Test
    fun ticketcompletedSpeed() {
        makeGraphTestInput(testCases = 1, nodes = 100_000, edges = 1_000_000, false).use { input ->
            val time = measureTimeMillis {
                ticketcompleted(input)
            }
            debug("ticketcompleted time use: $time ms")
        }
    }
}