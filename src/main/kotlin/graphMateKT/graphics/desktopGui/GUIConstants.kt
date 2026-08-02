package graphMateKT.graphics.desktopGui

internal object GUIConstants {
    const val width = 1024.0
    const val height = 768.0
    const val GUI_FONT_SIZE = 24.0

    const val GRAPH_INPUT = """0 Bob 10.0  // An edge from 0 to Bob with weight 10.0
0 Alice 3.0   // An edge from 0 to Alice with weight 3.0
Bob 1          // An edge from Bob to 1 with default weight 1.0
Alice Bob 4.0 
Alice 1 8.0
Alice Chad 2.0
Eve           // An isolated node with no edges
1 0 5.0"""

    const val GRID_INPUT = """0....#..
###..#..
..#..#..
#.#..#..
.#.....1"""

    const val INT_GRAPH_INPUT = """6 7 // Make the IntGraph have 6 nodes and 7 edges_
0 2 3.0  // An edge from 0 to 2 with weight 3.0
0 3 10.0 // An edge from 0 to 3 with weight 10.0
3 1      // An edge from 3 to 1 with default weight 1.0
2 3 4.0
2 1 8.0
2 4 2.0
4 2 5.0"""

    const val ALGORITHM_INFO = "Select an algorithm to vizualize it's graph traversal.\n\n"
    const val START_NODE_INFO =
        "You can select the starting and target node for search algorithms (BFS, DFS, Dijkstra).\n\n"
    const val TARGET_NODE_INFO =
        "If the target node is set when running Dijkstra or BFS, the path from the start to the target node will be visualized after the search is complete.\n\n"
    const val DIRECTED_INFO =
        "If the graph is directed, the edges between nodes only goes one way. If the graph is undirected, an identical edge in the opposite direction is automatically constructed. In this case, the defined nr of edges in an IntGraph must be doubled.\n\n"
    const val WALL_NODE_INFO = "The character representing walls in the grid. Example: #"
}