import java.util.Arrays;

public class BellmanFordSequential implements ShortestPathFinder {

    @Override
    public Result findShortestPaths(Graph g, int source) {
        int[] distances = new int[g.V];
        int[] predecessors = new int[g.V];
        Arrays.fill(distances, Integer.MAX_VALUE);
        Arrays.fill(predecessors, -1);
        distances[source] = 0;
        //boolean changes;

        for (int i = 0; i < g.V - 1; i++) {
            //changes = false;
            for (Graph.Edge edge : g.edges) {
                int u = edge.source();
                int v = edge.destination();
                int w = edge.weight();

                if (distances[u] != Integer.MAX_VALUE && distances[u] + w < distances[v]) {
                    distances[v] = distances[u] + w;
                    predecessors[v] = u;
                    //changes = true;
                }
            }
            //if (!changes) { System.out.printf("seq break at %s iteration\n", i); break; } // no updates in this iteration
        }

        for (Graph.Edge edge : g.edges) {
            int u = edge.source();
            int v = edge.destination();
            int w = edge.weight();

            if (distances[u] != Integer.MAX_VALUE && distances[u] < distances[v] - w) {
                throw new IllegalArgumentException("Graph contains a negative-weight cycle");
            }
        }

        return new Result(distances, predecessors);
    }
}
