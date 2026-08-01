import graphMateKT.debug
import graphMateKT.solutions.terraces
        
import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class TerracesTest {

    @Test
    fun Terracesa() {
        val expectedOutput = 4
        File("src/test/SampleInput/Terraces/input1").inputStream().use{
            assertThat(terraces(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun Terracesb() {
        val expectedOutput = 8
        File("src/test/SampleInput/Terraces/input2").inputStream().use{
            assertThat(terraces(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun Terracesc() {
        val expectedOutput = 5
        File("src/test/SampleInput/Terraces/input3").inputStream().use{
            assertThat(terraces(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun Terracesd() {
        val expectedOutput = 2
        File("src/test/SampleInput/Terraces/input4").inputStream().use{
            assertThat(terraces(it)).isEqualTo(expectedOutput)
        }
    }
    @Test
    fun TerracesdSpeed() {
        val z = 500
        val expectedOutput = z * z
        val input = mutableListOf("$z $z")
        val line = "0 ".repeat(z)
        repeat(z){
            input.add(line)
        }
        input.joinToString("\n").byteInputStream().use {
            assertThat(terraces(it)).isEqualTo(expectedOutput)
        }
    }
}