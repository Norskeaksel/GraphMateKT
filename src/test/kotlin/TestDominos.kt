import graphMateKT.solutions.dominos

import graphMateKT.debug
import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class DominosTest {
    @Test
    fun dominosa() {
        val expectedOutput = """1
2
"""
        File("src/test/SampleInput/Dominos/input1").inputStream().use {
            assertThat(dominos(it)).isEqualTo(expectedOutput)
        }
    }

    @Test
    fun dominosb() {
        val time = measureTimeMillis {
            File("src/test/SampleInput/Dominos/input2").inputStream().use {
                dominos(it)
            }
        }
        debug("dominos time use: $time ms")
    }

    @Test
    fun dominosc() {
        val expectedOutput = """3
"""
        File("src/test/SampleInput/Dominos/input3").inputStream().use {
            assertThat(dominos(it)).isEqualTo(expectedOutput)
        }
    }
}