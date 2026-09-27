#!/usr/bin/env bash

set -euo pipefail
cd "$(dirname "$0")/.."

OUT="GraphMateKTSingleFile.kt"

FILES=(
  HelperTypes.kt
  graphClasses/BaseGraph.kt
  graphClasses/Graph.kt
  graphClasses/IntGraph.kt
  graphClasses/Grid.kt
  graphClasses/AdjacencyList.kt
  graphClasses/FlattenedAdjacencyList.kt
  graphClasses/NestedAdjacencyList.kt
  graphAlgorithms/BFS.kt
  graphAlgorithms/DFS.kt
  graphAlgorithms/Dijkstra.kt
  graphAlgorithms/FloydWarshall.kt
  graphAlgorithms/GraphSearchResults.kt
  graphAlgorithms/NrOfPaths.kt
  graphAlgorithms/Prims.kt
  AdvancedRead.kt
)

cd src/main/kotlin/graphMateKT

cat "${FILES[@]}" \
  | sed '/^package/d' \
  | sed '/import graphClasses\.\*/d' \
  | sed '/^import graphMateKT/d' \
  | sed -n '/^import /p; /^import /!H; $ { g; s/^\n//; p; }' \
  | awk '/^import/{if(!seen[$0]++)print; next} 1' \
  > "../../../../$OUT"

cd ../../../..
echo "Generated $OUT"
