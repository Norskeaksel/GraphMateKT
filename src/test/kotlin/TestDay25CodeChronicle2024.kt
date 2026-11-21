import graphMateKT.solutions.adventOfCode.Day25CodeChronicle2024
        

import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test
import graphMateKT.debug
import kotlin.system.measureTimeMillis

class Day25CodeChronicle2024Test {

    @Test
    fun Day25CodeChronicle2024a() {
        val expectedOutput = 3
        File("src/test/SampleInput/Day25CodeChronicle2024/input1").inputStream().use{
            assertThat(Day25CodeChronicle2024(it)).isEqualTo(expectedOutput)
        }
    }
}