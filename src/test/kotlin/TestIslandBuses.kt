import graphMateKT.INPUT
import graphMateKT._reader
import graphMateKT.solutions.islandBuses
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import java.io.File
import kotlin.system.measureTimeMillis
import org.junit.jupiter.api.Test

class IslandBusesTest {
    companion object {
        @JvmStatic
        @AfterAll
        fun resetInput() {
            _reader = INPUT.bufferedReader()
        }
    }

    @Test
    fun solvea() {
        val expectedOutput = """Map 1
islands: 7
bridges: 4
buses needed: 4

Map 2
islands: 3
bridges: 2
buses needed: 1

Map 3
islands: 6
bridges: 0
buses needed: 6

Map 4
islands: 3
bridges: 1
buses needed: 2
"""
        _reader = File("src/test/SampleInput/IslandBuses/input1").inputStream().bufferedReader()
        assertThat(islandBuses()).isEqualTo(expectedOutput)
    }

    @Test
    fun speedTest() {
        val time1 = measureTimeMillis {
            _reader = File("src/test/SampleInput/IslandBuses/input2").inputStream().bufferedReader()
            islandBuses()
        }
        val time2 = measureTimeMillis {
            _reader = File("src/test/SampleInput/IslandBuses/input3").inputStream().bufferedReader()
            islandBuses()
        }
        val time3 = measureTimeMillis {
            _reader = File("src/test/SampleInput/IslandBuses/input4").inputStream().bufferedReader()
            islandBuses()
        }

        System.err.println("Island busses speed tests: \n" +
                "time1: $time1 ms\n" +
                "time2: $time2 ms\n" +
                "time3: $time3 ms")
    }
}