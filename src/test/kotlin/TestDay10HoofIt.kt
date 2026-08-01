import graphMateKT.solutions.day10aHoofIt2024
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day10HoofItTest{
    val input = """
        89010123
        78121874
        87430965
        96549874
        45678903
        32019012
        01329801
        10456732
    """.trimIndent().lines()
    val exa = """
...0...
...1...
...2...
6543456
7.....7
8.....8
9.....9
    """.trimIndent().lines()

    val exb = """
        ..90..9
        ...1.98
        ...2..7
        6543456
        765.987
        876....
        987....
    """.trimIndent().lines()

    @Test
    fun test1a(){
        val ans = day10aHoofIt2024(exa)
        assertThat(ans).isEqualTo(2)
    }
    @Test
    fun test1b(){
        val ans = day10aHoofIt2024(exb)
        assertThat(ans).isEqualTo(4)
    }
    @Test
    fun test1() {
        val ans = day10aHoofIt2024(input)
        assertThat(ans).isEqualTo(36)
    }
}
