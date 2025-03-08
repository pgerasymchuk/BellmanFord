import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BellmanFordSequential implements ShortestPathFinder {

    @Override
    public Result findShortestPaths(Graph g, int source) {
        List<Integer> distances = new ArrayList<>(Collections.nCopies(g.V, Integer.MAX_VALUE));
        distances.set(source, 0);
        List<Integer> predecessors = new ArrayList<>(Collections.nCopies(g.V, -1));
        boolean changes;

        for (int i = 0; i < g.V - 1; i++) {
            changes = false;
            for (Graph.Edge edge : g.edges) {
                if (distances.get(edge.source()) != Integer.MAX_VALUE &&
                    distances.get(edge.source()) + edge.weight() < distances.get(edge.destination())) {
                        distances.set(edge.destination(), distances.get(edge.source()) + edge.weight());
                        predecessors.set(edge.destination(), edge.source());
                        changes = true;
                }
            }
            if (!changes) { break; } // no updates in this iteration
        }

        for (Graph.Edge edge : g.edges) {
            if (distances.get(edge.source()) != Integer.MAX_VALUE &&
                distances.get(edge.source()) + edge.weight() < distances.get(edge.destination())) {

                throw new IllegalArgumentException("Graph contains a negative-weight cycle");
            }
        }

        return new Result(distances, predecessors);
    }
}
