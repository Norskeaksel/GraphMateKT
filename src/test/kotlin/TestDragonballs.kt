import graphMateKT.solutions.dragonballs


import java.io.File
import org.junit.jupiter.api.Test

class DragonballsTest {
    @Test
    fun dragonballsa() {
        File("src/test/SampleInput/Dragonballs/input1").inputStream().use {
            dragonballs(it, 10)
        }
    }
    @Test
    fun dragonballsb() {
        File("src/test/SampleInput/Dragonballs/input2").inputStream().use {
            dragonballs(it, 101)
        }
    }
    @Test
    fun dragonballsc() {
        File("src/test/SampleInput/Dragonballs/input3").inputStream().use {
            dragonballs(it, 5)
        }
    }
}