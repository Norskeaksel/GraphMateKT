import graphMateKT.solutions.ticketcompleted


import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test
import graphMateKT.debug
import java.math.BigDecimal
import kotlin.minus
import kotlin.plus
import kotlin.system.measureTimeMillis

class TicketcompletedTest {

    @Test
    fun ticketcompleteda() {
        val expectedOutput = BigDecimal(0.33333333333333333333)
        File("src/test/SampleInput/Ticketcompleted/input1").inputStream().use {
            assertThat(ticketcompleted(it)).isBetween(
                expectedOutput.minus(BigDecimal(1e-7)), expectedOutput.plus(
                    BigDecimal(1e-7)
                )
            )
        }
    }

    @Test
    fun ticketcompletedb() {
        val expectedOutput = BigDecimal(0.4)
        File("src/test/SampleInput/Ticketcompleted/input2").inputStream().use {
            assertThat(ticketcompleted(it)).isBetween(
                expectedOutput.minus(BigDecimal(1e-7)), expectedOutput.plus(
                    BigDecimal(1e-7)
                )
            )
        }
    }

    @Test
    fun ticketcompletedc() {
        val expectedOutput = BigDecimal(0.42857142857142857143)
        File("src/test/SampleInput/Ticketcompleted/input3").inputStream().use {
            assertThat(ticketcompleted(it)).isBetween(
                expectedOutput.minus(BigDecimal(1e-7)), expectedOutput.plus(
                    BigDecimal(1e-7)
                )
            )
        }
    }

    @Test
    fun ticketcompletedSpeed() {
        makeTestInput(testCases = 1, nodes = 100_000, edges = 1_000_000, false).use { input ->
            val time = measureTimeMillis {
                ticketcompleted(input)
            }
            debug("ticketcompleted time use: $time ms")
        }
    }
}