import graphMateKT.solutions.arcticNetwork
        

import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test

class ArcticNetworkTest {

    @Test
    fun ArcticNetworka() {
        val expectedOutput = """212.13
"""
        File("src/test/SampleInput/ArcticNetwork/input1").inputStream().use{
            assertThat(arcticNetwork(it)).isEqualTo(expectedOutput)
        }
    }
    @Test
    fun ArcticNetworkb() {
        val expectedOutput = """300.00
"""
        File("src/test/SampleInput/ArcticNetwork/input2").inputStream().use{
            assertThat(arcticNetwork(it)).isEqualTo(expectedOutput)
        }
    }
    @Test
    fun ArcticNetworkc() {
        val expectedOutput = """200.00
"""
        File("src/test/SampleInput/ArcticNetwork/input3").inputStream().use{
            assertThat(arcticNetwork(it)).isEqualTo(expectedOutput)
        }
    }
}