import graphMateKT._reader
import graphMateKT.solutions.draughts

import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test

class DraughtsTest {

    @Test
    fun draughtsa() {
        val expectedOutput = """2
4
"""
        File("src/test/SampleInput/Draughts/input1").inputStream().use {
            // assertThat(draughts(it)).isEqualTo(expectedOutput)
        }
    }
}