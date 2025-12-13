
import graphMateKT.readInt
import graphMateKT.readString
import java.io.File
import java.nio.file.Files.createDirectories
import kotlin.io.path.Path

private fun createSolution(name: String) = """package graphMateKT.solutions

import graphMateKT.INPUT
import graphMateKT._reader
import graphMateKT.readInt
import graphMateKT.readInts

internal fun main() {
    val ans = $name()
    println(ans)
    System.out.flush()
}

/** Solves https://open.kattis.com/problems/$name */
internal fun $name(): String {
    val n = readInt()
    repeat(n){
    
    }
    return ""
}
"""

private fun createSolutionTest(name: String, nrOfSampleInputs: Int): String {
    var test = """import graphMateKT.solutions.$name
        
import graphMateKT.INPUT
import graphMateKT._reader
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import java.io.File
import org.junit.jupiter.api.Test

class ${name.capitalize()}Test {
    companion object {
        @JvmStatic
        @AfterAll
        fun resetInput() {
            _reader = INPUT.bufferedReader()
        }
    }   
"""
    repeat(nrOfSampleInputs) {
        test += """
    @Test
    fun ${name}${'a' + it}() {
        val expectedOutput = ""${'"'}${'"'}${'"'}${'"'}
        _reader = File("src/test/SampleInput/${name.capitalize()}/input${it + 1}").inputStream().bufferedReader()
        assertThat($name()).isEqualTo(expectedOutput)
    }

"""
    }
    test += "}"
    return test
}


private fun main() {
    print("Name of the programming puzzle: ")
    val name = readString()
    print("How many sample inputs? ")
    val nrOfSampleInputs = readInt()
    val example = createSolution(name)
    val exampleTest = createSolutionTest(name, nrOfSampleInputs)

    val exampleFile = File("src/main/kotlin/graphMateKT/solutions/${name.capitalize()}.kt")
    val exampleTestFile = File("src/test/kotlin/Test${name.capitalize()}.kt")
    val testInputDirectoryPath = "src/test/SampleInput/${name.capitalize()}"
    exampleFile.writeText(example)
    exampleTestFile.writeText(exampleTest)
    createDirectories(Path(testInputDirectoryPath))
    println(
        "Created example file: ${exampleFile.absolutePath} and\n" +
                "test file: ${exampleTestFile.absolutePath} and"
    )
    repeat(nrOfSampleInputs) {
        val testInputFile = File("${testInputDirectoryPath}/input${it + 1}")
        testInputFile.writeText("")
        println("test input file: ${testInputFile.absolutePath}")
    }
}
