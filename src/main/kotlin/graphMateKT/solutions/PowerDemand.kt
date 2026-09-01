package graphMateKT.solutions

import fastInputReader.InputReader
import graphMateKT.graphClasses.Graph
import graphMateKT.graphics.graphGraphics.visualizeGraph
import java.io.InputStream

internal fun main() {
    val ans = powerDemand(System.`in`)
    println(ans)
    System.out.flush()
}

private data class Plant(
    val fixedCost: Int,
    val startCost: Int,
    val capacity: Int,
    val variableCost: Int,
    var isOn: Boolean = false
)

private data class State(val periodIdx: Int, val stateNr: Int, val totalCost: Int)

/** Solves https://open.kattis.com/problems/PowerDemand */
internal fun powerDemand(inputStream: InputStream): Int {
    val scanner = InputReader(inputStream)
    val nrOfPeriods = scanner.nextInt()
    val demand = mutableListOf<Int>()
    repeat(nrOfPeriods) {
        demand.add(scanner.nextInt())
    }
    val nrOfPlantTypes = scanner.nextInt()
    val graph = Graph()
    val plants = mutableListOf<Plant>()
    repeat(nrOfPlantTypes) {
        val nrOfPlant = scanner.nextInt()
        val (plantFixedCost, plantStartCost, plantCapacity, plantVariableCost) = scanner.nextIntArray(4)
        val plant = Plant(plantFixedCost, plantStartCost, plantCapacity, plantVariableCost)
        repeat(nrOfPlant) {
            plants.add(plant)
        }
    }
    val initialState = State(0, 0, 0)
    val goalState = State(nrOfPeriods + 1, 0, 0)
    val potentialStates = Array(nrOfPeriods + 1) { mutableListOf<State>() }
    potentialStates[0].add(initialState)
    val nrOfStatePlantPermutations = 1 shl plants.size
    graph.addNode(initialState)
    repeat(nrOfPeriods + 1) { pi ->
        potentialStates[pi].forEach { currentState ->
            if (pi == nrOfPeriods) {
                graph.addEdge(currentState, goalState)
                return@forEach
            }
            repeat(nrOfStatePlantPermutations) { newStateNr ->
                val currentBitString = currentState.stateNr.toString(2).padStart(plants.size, '0')
                val newBitString = newStateNr.toString(2).padStart(plants.size, '0')
                var costIncrease = 0
                currentBitString.indices.forEach { i ->
                    if (newBitString[i] == '1') {
                        costIncrease += plants[i].fixedCost
                        if (currentBitString[i] == '0') {
                            costIncrease += plants[i].variableCost
                        }
                    }
                }
                val nextState = State(pi + 1, newStateNr, currentState.totalCost + costIncrease)
                // TODO: add filter for ensuring enough capacity
                potentialStates[pi+1].add(nextState)
                graph.addEdge(currentState, nextState, costIncrease)
            }
        }
    }
    graph.printConnections()
    graph.dijkstra(initialState, goalState)
    // graph.visualizeGraph()
    return graph.distanceTo(goalState).toInt()
}
