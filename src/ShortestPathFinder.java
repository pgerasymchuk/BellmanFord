import java.util.List;

interface ShortestPathFinder {
    record Result (List<Double> distances, List<Integer> predecessors) {}

    Result findShortestPaths(Graph g, int source);
}
