import graphMateKT.solutions.beehives2
        

import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test
import graphMateKT.debug
import kotlin.system.measureTimeMillis

class Beehives2Test {

    @Test
    fun beehives2a() {
        val expectedOutput = """3"""
        File("src/test/SampleInput/Beehives2/input1").inputStream().use{
            assertThat(beehives2(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun beehives2b() {
        val expectedOutput = """impossible"""
        File("src/test/SampleInput/Beehives2/input2").inputStream().use{
            assertThat(beehives2(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun beehives2c() {
        val expectedOutput = """3"""
        File("src/test/SampleInput/Beehives2/input3").inputStream().use{
            assertThat(beehives2(it)).isEqualTo(expectedOutput)
        }
    }
}