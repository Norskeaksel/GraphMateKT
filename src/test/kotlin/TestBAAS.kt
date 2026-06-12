// Solves https://open.kattis.com/problems/crosscountry?tab=metadata
import graphMateKT.debug
import graphMateKT.solutions.baas
import org.assertj.core.api.Assertions.assertThat
import java.io.File
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class BAASTest {
    @Test
    fun Baasa() {
        File("src/test/SampleInput/Baas/input1").inputStream().use {
            assertThat(baas(it)).isEqualTo(15)
        }
    }

    @Test
    fun Baasb() {
        File("src/test/SampleInput/Baas/input2").inputStream().use {
            assertThat(baas(it)).isEqualTo(60)
        }
    }

    @Test
    fun Baasc() {
        File("src/test/SampleInput/Baas/heavyTestInput").inputStream().use {
            val ans: Int
            val time = measureTimeMillis {
                ans = baas(it)
            }
            debug("BAAS speed test time: $time ms")
            assertThat(ans).isEqualTo(399)
        }
    }
}
