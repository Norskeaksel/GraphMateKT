import graphMateKT.solutions.knightsfen


import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import graphMateKT.debug
import graphMateKT.solutions.KNIGHT_ORDERS
import graphMateKT.solutions.bitcode2Rank
import graphMateKT.solutions.board2Id
import graphMateKT.solutions.id2Board
import graphMateKT.solutions.rank2Bitcode
import kotlin.system.measureTimeMillis

class KnightsfenTest {
    @ParameterizedTest(name = "bitcode2Rank")
    @CsvSource(
        "0011, 2, 0",
        "0101, 2, 1",
        "0110, 2, 2",
        "1001, 2, 3",
        "1010, 2, 4",
        "1100, 2, 5",
        "000000000000111111111111, 12, 0",
        "111111111111000000000000, 12, ${KNIGHT_ORDERS - 1}",
    )
    fun testBitcode2Rank(bitcode: String, nrOfZeros: Int, expected: Int) {
        assertThat(bitcode2Rank(bitcode.toCharArray(), nrOfZeros)).isEqualTo(expected)
    }

    @ParameterizedTest(name = "rank2Bitcode")
    @CsvSource(
        "0, 2, 0011",
        "1, 2, 0101",
        "2, 2, 0110",
        "3, 2, 1001",
        "4, 2, 1010",
        "5, 2, 1100",
        "0, 12, 000000000000111111111111",
        "${KNIGHT_ORDERS - 1}, 12, 111111111111000000000000",
    )
    fun testRank2Bitcode(rank: Int, nrOfZeros: Int, expected: String) {
        assertThat(rank2Bitcode(rank, nrOfZeros).concatToString()).isEqualTo(expected)
    }

    @ParameterizedTest(name = "id2Board")
    @ValueSource(ints = [0, 1, KNIGHT_ORDERS - 1, KNIGHT_ORDERS, 12 * KNIGHT_ORDERS, 25 * KNIGHT_ORDERS - 1])
    fun testId2BoardRoundTrips(id: Int) {
        val board = id2Board(id)
        assertThat(board2Id(board)).isEqualTo(id)
    }

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