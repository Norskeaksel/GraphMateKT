package graphMateKT.graphics.desktopGui

internal object GUIConstants {
    const val GUI_FONT_SIZE = 24.0
    const val INFO_ICON_FONT_SIZE = 40.0

    const val GRAPH_INPUT = """0 Bob 10.0
0 Alice 3.0
Bob 1
Alice Bob 4.0
Alice 1 8.0
Alice Chad 2.0
Eve
1 0 5.0"""

    const val GRID_INPUT = """0....#1
###.##.
..#.#..
#.#.#.#
.#....0"""

    const val INT_GRAPH_INPUT = """6 7
0 2 3.0
0 3 10.0
3 1
2 3 4.0
2 1 8.0
2 4 2.0
4 2 5.0"""

    const val GRAPH_INFO = "Visualize a graph defined by edges from one node to another node with an optional weight. Example:\nAlice Bob 2.0\nChad Eve"
    const val GRID_INPUT_INFO = "Visualize a grid defined by rectangle string where each character represents a node. Example:\nabc\n101"
    const val INT_GRAPH_INPUT_INFO = "Visualize an IntGraph defined by the nr of integer nodes and nr of edges,\nfollowed by edges with optional weight. Example:\n2 1\n1 0 1.0"
}