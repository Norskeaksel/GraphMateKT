import graphMateKT.solutions.Day11Reactor2025Part1
        
import graphMateKT.INPUT
import graphMateKT._reader
import graphMateKT.solutions.Day11Reactor2025Part2
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test

class Day11Reactor2025Test {
    companion object {
        @JvmStatic
        @AfterAll
        fun resetInput() {
            _reader = INPUT.bufferedReader()
        }
    }   

    @Test
    fun Day11Reactor2025aPart1() {
        val input = """
            aaa: you hhh
            you: bbb ccc
            bbb: ddd eee
            ccc: ddd eee fff
            ddd: ggg
            eee: out
            fff: out
            ggg: out
            hhh: ccc fff iii
            iii: out
        """.trimIndent().lines()
        val expectedOutput = 5L
        assertThat(Day11Reactor2025Part1(input)).isEqualTo(expectedOutput)
    }

    @Test
    fun Day11Reactor2025aPart2() {
        val input = """
            svr: aaa bbb
            aaa: fft
            fft: ccc
            bbb: tty
            tty: ccc
            ccc: ddd eee
            ddd: hub
            hub: fff
            eee: dac
            dac: fff
            fff: ggg hhh
            ggg: out
            hhh: out
        """.trimIndent().lines()
        val expectedOutputPart2 = 2L
        assertThat(Day11Reactor2025Part2(input)).isEqualTo(expectedOutputPart2)
    }
}