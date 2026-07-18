#!/usr/bin/env bash

cd "$(dirname "$0")/.."
cd src/main/kotlin/graphMateKT
cat solutions/Gridmst.kt HelperTypes.kt graphClasses/BaseGraph.kt graphClasses/Graph.kt graphClasses/IntGraph.kt \
graphClasses/Grid.kt graphClasses/AdjacencyList.kt graphClasses/FlattenedAdjacencyList.kt graphClasses/NestedAdjacencyList.kt \
graphAlgorithms/BFS.kt graphAlgorithms/DFS.kt graphAlgorithms/Dijkstra.kt graphAlgorithms/FloydWarshall.kt \
graphAlgorithms/GraphSearchResults.kt graphAlgorithms/NrOfPaths.kt \
graphAlgorithms/Prims.kt AdvancedRead.kt ../fastInputReader/InputReader.kt | \
sed '/^package/d' | sed '/import graphClasses\.\*/d' | sed '/^import graphMateKT/d' \
| sed '/^import fastInputReader/d' | sed -n '/^import /p; /^import /!H; $ { g; s/^\n//; p; }' \
| awk '/^import/{if(!seen[$0]++)print; next} 1' \
> ../../../../../GraphSolutions/src/main/kotlin/MergedSolution.kt
cd ../../../..
SOLUTION_PATH="$(pwd)/GraphSolutions/src/main/kotlin/MergedSolution.kt"
echo "Saved solution to and copied the following path to the clipboard: $SOLUTION_PATH."
printf '%s' "$SOLUTION_PATH" | { clip.exe || xclip -sel clip; } 2>/dev/null
