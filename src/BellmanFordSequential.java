import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BellmanFordSequential implements ShortestPathFinder {

    @Override
    public Result findShortestPaths(Graph g, int source) {
        List<Integer> distances = new ArrayList<>(Collections.nCopies(g.V, Integer.MAX_VALUE));
        distances.set(source, 0);
        List<Integer> predecessors = new ArrayList<>(Collections.nCopies(g.V, -1));
        //boolean changes;

        for (int i = 0; i < g.V - 1; i++) {
            //changes = false;
            for (Graph.Edge edge : g.edges) {
                if (distances.get(edge.source()) != Integer.MAX_VALUE &&
                        distances.get(edge.source()) + edge.weight() < distances.get(edge.destination())) {
                    distances.set(edge.destination(), distances.get(edge.source()) + edge.weight());
                    predecessors.set(edge.destination(), edge.source());
                    //changes = true;
                }
            }
            //if (!changes) { System.out.printf("seq break at %s iteration\n", i); break; } // no updates in this iteration
        }

        for (Graph.Edge edge : g.edges) {
            if (distances.get(edge.source()) != Integer.MAX_VALUE &&
                    distances.get(edge.source()) < distances.get(edge.destination()) - edge.weight()) {

                throw new IllegalArgumentException("Graph contains a negative-weight cycle");
            }
        }

        int[] distancesArray = distances.stream().mapToInt(Integer::intValue).toArray();
        int[] predecessorsArray = predecessors.stream().mapToInt(Integer::intValue).toArray();

        return new Result(distancesArray, predecessorsArray);
    }
}
