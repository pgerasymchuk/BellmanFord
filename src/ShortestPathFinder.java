import java.util.List;

public interface ShortestPathFinder {
    record Result (List<Integer> distances, List<Integer> predecessors) {}

    Result findShortestPaths(Graph g, int source);
}
