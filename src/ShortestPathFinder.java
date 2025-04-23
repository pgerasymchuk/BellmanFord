interface ShortestPathFinder {
    record Result (int[] distances, int[] predecessors) {}

    Result findShortestPaths(Graph g, int source);
}
