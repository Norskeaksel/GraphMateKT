package graphMateKT.graphics.desktopGui

object ModeText {
    val graphInput = """
        Alice Bob 10.0
        Alice Cat 3.0
        Bob David
        Cat Bob 4.0
        Cat David 8.0
        Cat Eve 2.0
        Frank
        David Eve 5.0
        """.trimIndent()

    val gridInput = """
           S....#E
           ###.##.
           ..#.#..
           #.#.#.#
           .......
        """.trimIndent()

    val intGraphInput = """
        6 7
        0 1 10.0
        0 2 3.0
        1 3
        2 1 4.0
        2 3 8.0
        2 4 2.0
        3 4 5.0
        """.trimIndent()
    val graphInputInfo = "Edges from node u to node v with an optional weight (Example: Alice Bob 2.0)."
    val gridInputInfo = "Width and height of the grid (Example: 30 40), OR a rectangle string where each character represents a node."
    val intGraphInputInfo = "Nr of integer nodes and nr of edges, followed by edges with optional weight. Example:\n2 1\n1 0"
}