import graphMateKT.solutions.Day16ReindeerMaze2024
import graphMateKT.solutions.Day16ReindeerMaze2024Part2


import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test

class Day16ReindeerMaze2024Test {

    @Test
    fun Day16ReindeerMaze2024a() {
        val expectedOutput = 7036
        val expectedOutputPart2 = 45
        File("src/test/SampleInput/Day16ReindeerMaze2024/input1").inputStream().use {
            val (grid, ans) = Day16ReindeerMaze2024(it)
            assertThat(ans).isEqualTo(expectedOutput)
            val ans2 = Day16ReindeerMaze2024Part2(grid, ans.toDouble())
            assertThat(ans2).isEqualTo(expectedOutputPart2)
        }
    }

    @Test
    fun Day16ReindeerMaze2024b() {
        val expectedOutput = 11048
        val expectedOutputPart2 = 64
        File("src/test/SampleInput/Day16ReindeerMaze2024/input2").inputStream().use {
            val (grid, ans) = Day16ReindeerMaze2024(it)
            assertThat(ans).isEqualTo(expectedOutput)
            val ans2 = Day16ReindeerMaze2024Part2(grid, ans.toDouble())
            assertThat(ans2).isEqualTo(expectedOutputPart2)
        }
    }
}