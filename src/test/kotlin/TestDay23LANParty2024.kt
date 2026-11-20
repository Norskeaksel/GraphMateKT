import graphMateKT.solutions.adventOfCode.Day23LANParty2024Part1
import graphMateKT.solutions.adventOfCode.Day23LANParty2024Part2


import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test

class Day23LANParty2024Test {

    @Test
    fun Day23LANParty2024Part1() {
        val expectedOutput = """7"""
        File("src/test/SampleInput/Day23LANParty2024/input1").inputStream().use{
            assertThat(Day23LANParty2024Part1(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun Day23LANParty2024part2() {
        val expectedOutput = """co,de,ka,ta"""
        File("src/test/SampleInput/Day23LANParty2024/input1").inputStream().use{
            assertThat(Day23LANParty2024Part2(it)).isEqualTo(expectedOutput)
        }
    }
}