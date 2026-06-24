package graphMateKT.graphics.desktopGui

object GUIConstants {
    const val guiFontSize = 24.0
    const val infoIconFontSize = 40.0

    const val graphInput = """0 Bob 10.0
0 Alice 3.0
Bob 1
Alice Bob 4.0
Alice 1 8.0
Alice Chad 2.0
Eve
1 Chad 5.0"""

    const val gridInput = """0....#1
###.##.
..#.#..
#.#.#.#
......0"""

    const val intGraphInput = """6 7
0 2 3.0
0 3 10.0
3 1
2 3 4.0
2 1 8.0
2 4 2.0
1 4 5.0"""

    const val graphInfo = "Visualize a graph defined by edges from one node to another node with an optional weight. Example:\nAlice Bob 2.0\nChad Eve"
    const val gridInputInfo = "Visualize a grid defined by a width and height (Example: 30 40), OR;\ndefined by rectangle string where each character represents a node. Example:\nabc\n101"
    const val intGraphInputInfo = "Visualize an IntGraph defined by the nr of integer nodes and nr of edges,\nfollowed by edges with optional weight. Example:\n2 1\n1 0 1.0"
}