import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day18RAMRunTest{
    val input = """
        5,4
        4,2
        4,5
        3,0
        2,1
        6,3
        2,4
        1,5
        0,6
        3,3
        2,6
        5,1
        1,2
        5,5
        2,5
        6,5
        1,4
        0,4
        6,4
        1,1
        6,1
        1,0
        0,5
        1,6
        2,0
    """.trimIndent().lines()
    @Test
    fun test1a(){
        val ans = graphMateKT.solutions.day18aRAMRun2024(input, 7, 12)
        assertThat(ans).isEqualTo(22)
    }
}
