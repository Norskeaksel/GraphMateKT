import graphMateKT.solutions.colorland

import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test

class ColorlandTest {

    @Test
    fun colorlanda() {
        val expectedOutput = 1
        File("src/test/SampleInput/Colorland/input1").inputStream().use {
            assertThat(colorland(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun colorlandb() {
        val expectedOutput = 2
        File("src/test/SampleInput/Colorland/input2").inputStream().use {
            assertThat(colorland(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun colorlandc() {
        val expectedOutput = 4
        File("src/test/SampleInput/Colorland/input3").inputStream().use {
            assertThat(colorland(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun colorlandSpeed() {
        val expectedOutput = 200_000 / 6 + 1
        val input = mutableListOf("200000")
        val colors = listOf("Blue", "Orange", "Pink", "Green", "Red", "Yellow")
        repeat(200_000){
            input.add(colors[it % colors.size])
        }
        input.joinToString("\n").byteInputStream().use {
            assertThat(colorland(it)).isEqualTo(expectedOutput)
        }
    }
}