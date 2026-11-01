import graphMateKT.solutions.rubiksrevenge


import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test
import graphMateKT.debug
import kotlin.system.measureTimeMillis

class RubiksrevengeTest {

    @Test
    fun rubiksrevengea() {
        val expectedOutput = 3
        File("src/test/SampleInput/Rubiksrevenge/input1").inputStream().use {
            assertThat(rubiksrevenge(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun rubiksrevengeb() {
        val expectedOutput = 4
        File("src/test/SampleInput/Rubiksrevenge/input2").inputStream().use {
            assertThat(rubiksrevenge(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun rubiksrevengec() {
        File("src/test/SampleInput/Rubiksrevenge/input3").inputStream().use {
            rubiksrevenge(it)
        }
    }

    @Test
    fun rubiksrevenged() {
        val expectedOutput = 7
        File("src/test/SampleInput/Rubiksrevenge/input4").inputStream().use {
            assertThat(rubiksrevenge(it)).isEqualTo(expectedOutput)
        }
    }
}