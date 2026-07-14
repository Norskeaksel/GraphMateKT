import graphMateKT.debug
import graphMateKT.solutions.dominos
import graphMateKT.solutions.promotions

import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

const val expectedOutputA = """2
4
3
"""
const val expectedOutputB = """0
1
0
"""

class PromotionsTest {

    @Test
    fun promotionsa() {
        File("src/test/SampleInput/Promotions/input1").inputStream().use {
            assertThat(promotions(it)).isEqualTo(expectedOutputA)
        }
    }

    @Test
    fun promotionsb() {
        File("src/test/SampleInput/Promotions/input2").inputStream().use {
            assertThat(promotions(it)).isEqualTo(expectedOutputB)
        }
    }
    @Test
    fun promotionsSpeed() {
        makeTestInput(testCases = 1, nodes = 5000, edges = 20000).use { input ->
            val time = measureTimeMillis {
                promotions(input)
            }
            debug("promotions time use: $time ms")
        }
    }
}