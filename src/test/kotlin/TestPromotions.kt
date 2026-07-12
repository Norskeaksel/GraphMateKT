import graphMateKT.solutions.promotions

import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test

class PromotionsTest {

    @Test
    fun promotionsa() {
        val expectedOutput = """2
4
3
"""
        File("src/test/SampleInput/Promotions/input1").inputStream().use {
            //assertThat(promotions(it)).isEqualTo(expectedOutput)
        }
    }
}