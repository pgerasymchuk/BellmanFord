public class BellmanFordSequential implements ShortestPathFinder {

    @Override
    public Result findShortestPaths(Graph graph, int source) {
        int[] distances = new int[graph.V];
        int[] predecessors = new int[graph.V];
        for (int i = 0; i < graph.V; i++) {
            distances[i] = Integer.MAX_VALUE;
            predecessors[i] = -1;
        }
        distances[source] = 0;

        for (int i = 0; i < graph.V - 1; i++) {
            for (Graph.Edge edge : graph.edges) {
                if (distances[edge.source()] != Integer.MAX_VALUE &&
                    distances[edge.source()] + edge.weight() < distances[edge.destination()]) {
                        distances[edge.destination()] = distances[edge.source()] + edge.weight();
                        predecessors[edge.destination()] = edge.source();
                }
            }
        }

        for (Graph.Edge edge : graph.edges) {
            if (distances[edge.source()] + edge.weight() < distances[edge.destination()]) {
                throw new IllegalArgumentException("Graph contains a negative-weight cycle");
            }
        }

        return new Result(distances, predecessors);
    }
}
