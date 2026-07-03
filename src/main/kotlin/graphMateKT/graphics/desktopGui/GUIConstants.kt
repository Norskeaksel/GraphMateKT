package graphMateKT.graphics.desktopGui

internal object GUIConstants {
    const val GUI_FONT_SIZE = 24.0

    const val GRAPH_INPUT = """0 Bob 10.0
0 Alice 3.0
Bob 1
Alice Bob 4.0
Alice 1 8.0
Alice Chad 2.0
Eve
1 0 5.0"""

    const val GRID_INPUT = """0....#..
###..#..
..#..#..
#.#..#..
.#.....1"""

    const val INT_GRAPH_INPUT = """6 7
0 2 3.0
0 3 10.0
3 1
2 3 4.0
2 1 8.0
2 4 2.0
4 2 5.0"""

    const val GRAPH_INFO = "Visualize a graph defined by edges from one node to another node with an optional weight. Example: Alice Bob 2.0."
    const val GRID_INPUT_INFO = "Visualize a grid defined by a rectangle string where each character represents a node. Example: abc."
    const val INT_GRAPH_INPUT_INFO = "Visualize an IntGraph defined by the nr of integer nodes and nr of edges, followed by edges with an optional weight. Example: 2 1, followed by 1 0 1.0."

    const val ALGORITHM_INFO = "Select an algorithm to vizualize it's graph traversal."
    const val START_NODE_INFO = "You can select the starting and target node for search algorithms (BFS, DFS, Dijkstra)."
    const val TARGET_NODE_INFO = "If the target node is set when running Dijkstra of BFS, the path from the start to the target node will be visualized after the search is complete."
    const val DIRECTED_INFO = "If the graph is directed, the edges between nodes only goes one way. If the graph is undirected, an identical edge in the opposite direction is automatically constructed. In this case, the defined nr of edges in an IntGraph must be doubled."
    const val WALL_NODE_INFO = "The character representing walls in the grid. Example: #"
}