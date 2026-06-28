# graphMateKT

![My Workflow Status](https://github.com/Norskeaksel/GraphMateKT/actions/workflows/ci.yml/badge.svg)

GraphMateKT contains classes and algorithms for making, traversing and visualizing graphs and grids.
This can for example be used to create and debug competitive programming solutions.
The [solutions](src/main/kotlin/graphMateKT/solutions) folder contains code using this graphLibraryPackage to solve
various problems.
The library contains the general `Graph` class, which can be used to create graphs of any datatype,
the `IntGraph` class, which is performance optimized for integer nodes,
and the `Grid` class, where each node has x and y coordinates in addition to containing any data type.
All the classes inherit from the
abstract [BaseGraph](https://norskeaksel.github.io/GraphMateKT/graphmatekt/graphMateKT.graphClasses/-base-graph/index.html)
class, which defines the basic functionality of the graphs.

## Documentation

[![kdoc](https://img.shields.io/badge/kdoc-1.0.0-brightgreen)](https://norskeaksel.github.io/GraphMateKT  )

## Using the library

Add the following dependency to your `pom.xml` file:

```xml
<dependency>
  <groupId>io.github.norskeaksel</groupId>
  <artifactId>graphmatekt</artifactId>
  <version>1.0.0</version>
</dependency>
```

See [here](https://github.com/Norskeaksel/GraphMateKT/packages/) for the latest version number.

:warning: Please note that for the ```Graph().visualizeGraph() function``` to work as intended
, the files `smartgraph.css` and `smartgraph.properties` **must be added manually** to the root of your project,
as described in [Bruno Silva](https://github.com/brunomnsilva)'s [JavaFXSmartGraph](https://github.com/brunomnsilva/JavaFXSmartGraph) repository.

## Library GUI
A GUI is included in the library, which can be used to see some of the algorithms and the graph and grid visualisation
capabilities of the GraphMateKT library. It can be invoked by running `launchGraphMateKTGUI()`. For convenience, a jar
file of the GUI is included [here](out/artifacts/GraphMateKT_jar), which can be downloaded and run with
`java -jar GraphMateKT.jar`. Note that the `smartgraph.css` and `smartgraph.properties`, must still be present in the
directory the jar file is run from, for the GUI to work as intended.

### Using the library in a single file.
In some competitive programing platforms like Kattis or Codeforces, users are required to upload a single file without
external dependencies. If that's your use case, or you don't want to download the dependency, you can copy some of
the library in a single file format from [GraphMateKTSingleFile.kt](GraphMateKTSingleFile.kt). Note, this file does not
have the graph visualization functions, because they themselves require external dependencies.

## The Graph class

A Graph data structure supports nodes of any datatype.

Any new node is given an ID upon creation, which is used to build an adjacency list. The class maintains internal
maps between IDs and nodes and vice versa. Nodes can be connected unidirectionally with `.addEdge(node1, node2)`
or bidirectionally with `.connect(node1, node2)`.
Once the graph is built, you may use the following graph algorithms:

- **Breadth-First Search (BFS)**:
    - `bfs(startNode: T, target: T? = null, reset: Boolean = true)`
    - `bfs(startNodes: List<T>, target: T? = null, reset: Boolean = true)`

- **Depth-First Search (DFS)**:
    - `dfs(startNode: T, reset: Boolean = true)`

- **Dijkstra's Algorithm**:
    - `dijkstra(startNode: T, target: T? = null)`

- **Floyd-Warshall Algorithm**:
    - `floydWarshall()`

- **Topological Sort**:
    - `topologicalSort()`

- **Strongly Connected Components (SCC)**:
    - `stronglyConnectedComponents()`

- **Prims (MST)**:
    - `minimumSpanningTree()`
- **NrOfPaths**:
  - `nrOfPaths(startNode: T, targetNode: T, mod: Long = Long.MAX_VALUE)`

[Example usage:](src/main/kotlin/graphMateKT/examples/GraphExample.kt)

```kotlin
package examples

import graphMateKT.graphClasses.Graph
import graphMateKT.graphClasses.IntGraph
import graphMateKT.graphGraphics.visualizeGraph


fun main() {
  // --- Example Graph Definition ---
  val graph = Graph()
  graph.addEdge(0, 1, 10.0)
  graph.addEdge(0, 2, 3.0)
  graph.addEdge(1, 3, 2.0)
  graph.addEdge(2, 1, 4.0)
  graph.addEdge(2, 3, 8.0)
  graph.addEdge(2, 4, 2.0)
  graph.addEdge(3, 4, 5.0)

  graph.addNode(5) // Adding an isolated node is also possible
  val startNode = 0
  val targetNode = 3
  graph.dijkstra(startNode, targetNode) // Provide a goal target node to stop the search when the target is found
  val nodes: List<Int> =
    graph.nodes().map { it as Int } // Nodes are of type Any and must therefore be cast to Int
  println("Shortest paths from source node $startNode:")
  nodes.forEach { node ->
    val distValue = graph.distanceTo(node)
    val path = graph.getPath(node)
    println("To node $node: Distance $distValue Path: ${if (distValue < Int.MAX_VALUE) path else null}")
  }
  /* Output:
      Shortest paths from source node 0:
      Distance to node 0: 0.0 Path: [0]
      Distance to node 1: 7.0 Path: [0, 2, 1]
      Distance to node 2: 3.0 Path: [0, 2]
      Distance to node 3: 9.0 Path: [0, 2, 1, 3]
      Distance to node 4: 5.0 Path: [0, 2, 4]
      Distance to node 5: Infinity Path: null
  */


  /* --- Example IntGraph Definition ---
       An IntGraph needs to be initialized with a fixed size and number of edges, because it consists of integer
       nodes from 0 to size-1 and stores its edges in fixed-size arrays.
  */
  val n = graph.size()
  val intGraph = IntGraph(n, graph.nrOfEdges())
  // Add the same edges as the above Graph
  graph.nodes().forEach { fromNode ->
    graph.edges(fromNode).forEach { edge ->
      val weight = edge.first
      val toNode = edge.second as Int // Cast type Any to Int
      intGraph.addEdge(fromNode as Int, toNode, weight)
    }
  }
  intGraph.dijkstra(startNode, targetNode)
  val intNodes: List<Int> = intGraph.nodes()
  println("Shortest paths from source node $startNode:")
  intNodes.forEach { node ->
    val distValue = intGraph.distanceTo(node)
    val path = intGraph.getPath(node)
    println("To node $node: Distance $distValue Path: ${if (distValue < Int.MAX_VALUE) path else null}")
  }
  // Outputs the same as the code above

  // Visualize the graph using brunomnsilva's JavaFXSmartGraph: https://github.com/brunomnsilva/JavaFXSmartGraph
  graph.visualizeGraph(
    // Also works with intGraph.visualizeSearch(
    screenTitle = "Visualizing Dijkstra's shortest path with GraphMateKT",
    animationTicTimeOverride = 1000.0,
    startPaused = true,
  )
}
```

![GraphVizualization.gif](gifs/GraphVizualization.gif)

## The IntGraph class

The IntGraph class behaves a lot like the Graph class when used with integers like the example above. However,
it's more performant, because it does not need to maintain an internal mapping between the nodes and their indexes in
the adjacency list. The obvious drawback being it only supports integer nodes, and that it must be initialized with a
fixed number of nodes (`size`) and edges (`nrOfEdges`).
[Example usage.](src/main/kotlin/graphMateKT/examples/GraphExample.kt)

## The Grid class

The Grid class is a specialized graph class. It uses the data class:
```Tile(val x: Int, val y: Int, var data: Any? = null)```
to represent nodes of any datatype, where each node also have x and y coordinates.
The grid can be created with a width and height, or by passing a list of strings.
The grid can be traversed using the same algorithms as the graph class,
but it also has some additional methods for connecting the grid without explicitly adding
edges. `.connectGridDefault()` connects each node to nodes up, down, left and right of it, if they exist.
If some customization is needed, `.connectGrid(::yourCustomFunction)` (also written like `.connectGrid{ yourLambda}`)
can be used, where yourCustomFunction takes a `Tile` and returns a `List<Tile>` to connect
to. [Example usage:](src/main/kotlin/graphMateKT/examples/GridExample.kt)

```kotlin
import graphMateKT.graphClasses.Grid
import graphMateKT.Tile
import graphMateKT.gridGraphics.visualizeGrid

fun main() {
  // Example Grid Definition. We can also initialize it with a list of strings
  val width = 99
  val height = 99
  val grid = Grid(width, height)

  // We can delete nodes, by specifying them, their coordinates or their data. However, deletions MUST take place
  // before connections are added. Otherwise, the grid can contain connections to the deleted tiles
  // Let's use some custom functions to delete some patterns

  grid.deleteSquareAtOffset(4)
  grid.deleteDiamondAtOffset(8)
  grid.deleteSquareAtOffset(10)
  grid.deleteDiamondAtOffset(20)
  grid.deleteSquareAtOffset(22)
  grid.deleteDiamondAtOffset(44)
  grid.deleteSquareAtOffset(46)

  // We could use `grid.connectGridDefault()` to connect all nodes, but let's define a custom connection instead.
  fun connectDownOrRight(t: Tile): List<Tile> = grid.getStraightNeighbours(t).filter { it.x >= t.x || it.y > t.y }
  grid.connectGrid(bidirectional = true, ::connectDownOrRight)

  // Nodes in a grid consists of Tile objects with x, y coordinates and data
  val startNode = Tile(width / 2, height / 2)

  // We can run a seach algorithm like BFS (Breadth-First Search) from a start node
  val target = Tile(width - 1, height - 1) // Define a target to find a path to it
  grid.bfs(startNode, target)

  // Visualizing the grid, the BFS and the final fastest path to the target
  grid.visualizeGrid(
    screenTitle = "Breadth-First Search from the center to the bottom right corner, using GraphMateKT",
    screenWidthMultiplier = 0.88,
    startPaused = true,
  )
}

private fun Grid.deleteSquareAtOffset(centerOffset: Int) {
  val center = width / 2
  val lowerBound = center - centerOffset
  val upperBound = center + centerOffset
  for (i in lowerBound + 2 until upperBound - 1) {
    deleteNodeAtXY(i, lowerBound)
    deleteNodeAtXY(i, upperBound)
    deleteNodeAtXY(lowerBound, i)
    deleteNodeAtXY(upperBound, i)
  }
}

private fun Grid.deleteDiamondAtOffset(centerOffset: Int) {
  val center = width / 2
  var dx = centerOffset
  var dy = 1
  repeat(centerOffset) {
    deleteNodeAtXY(center - dx, center - dy)
    deleteNodeAtXY(center + dx, center - dy)
    deleteNodeAtXY(center - dx, center + dy)
    deleteNodeAtXY(center + dx, center + dy)
    dx--
    dy++
  }
}
```

<img src="gifs/GridVizualization.gif" width="200%" alt="">

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE-AkselsGraphLibrary) files for details. **All
derivative work should include this license**.
