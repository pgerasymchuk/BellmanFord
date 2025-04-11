import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class BellmanFordParallel implements ShortestPathFinder {

    @Override
    public Result findShortestPaths(Graph g, int source) {
        int[] distances = new int[g.V];
        int[] predecessors = new int[g.V];
        Arrays.fill(distances, Integer.MAX_VALUE);
        Arrays.fill(predecessors, -1);
        distances[source] = 0;

        AtomicBoolean changes = new AtomicBoolean(false);

        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        /*long t1 = System.nanoTime();

        List<Graph.Edge>[] groupedEdges = new ArrayList[g.V];
        for (int i = 0; i < g.V; i++) {
            groupedEdges[i] = new ArrayList<>();
        }
        for (Graph.Edge edge : g.edges) {
            groupedEdges[edge.destination()].add(edge);
        }

        long t2 = System.nanoTime();
        System.out.println("Initializing, ms: " + (t2 - t1) * 1e-6);*/

        long t1  = System.nanoTime();
        g.edges.sort(Comparator.comparingInt(Graph.Edge::destination)); // PERFORM SORTING ON COPY OF g.edges !!!


        int edgesPerThread = g.E / numThreads;
        int[] edgesIndicesBoundariesForEachThread = new int[numThreads + 1];
        edgesIndicesBoundariesForEachThread[0] = 0;
        edgesIndicesBoundariesForEachThread[numThreads] = g.E;
        // (numThreads - 1) iteration, this approach does not work if one number spans across the whole group
        for (int i = 1; i < numThreads; i++) {
            int currentIndex = i * edgesPerThread - 1;
            int initialValue, nextValue;
            do {
                initialValue = g.edges.get(currentIndex).destination();
                nextValue = g.edges.get(currentIndex + 1).destination();
                currentIndex++;
            } while (initialValue == nextValue && currentIndex < g.E);

            edgesIndicesBoundariesForEachThread[i] = currentIndex;
        }
        long t2 = System.nanoTime();
        System.out.println("sorting and preparation, ms: " + (t2 - t1) * 1e-6);

        for (int i = 0; i < g.V - 1; i++) {
            changes.set(false);

            List<Future<?>> futures = new ArrayList<>();

            //for (int j = 0; j < g.V; j += edgeGroupsPerThread) {
            for (int j = 0; j < numThreads; j++) {
                //int start = j;
                //int end = Math.min(g.V, j + edgeGroupsPerThread);
                int start = edgesIndicesBoundariesForEachThread[j];
                int end = edgesIndicesBoundariesForEachThread[j + 1];

                futures.add(executor.submit(() -> {
                    boolean localChange = false;
                    for (int k = start; k < end; k++) {
                        //for (Graph.Edge edge : groupedEdges[k]) {
                            Graph.Edge edge = g.edges.get(k);
                            if (distances[edge.source()] != Integer.MAX_VALUE &&
                                    distances[edge.source()] + edge.weight() < distances[edge.destination()]) {

                                distances[edge.destination()] = distances[edge.source()] + edge.weight();
                                predecessors[edge.destination()] = edge.source();
                                localChange = true;
                            }
                        //}
                    }
                    if (localChange) { changes.set(true); }
                }));
            }

            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (Exception e) { throw new RuntimeException(e); }
            }

            if (!changes.get()) { System.out.printf("parallel break at %s iteration\n", i); break; } // no updates in this iteration
        }

        for (Graph.Edge edge : g.edges) {
            int u = edge.source();
            int v = edge.destination();
            int w = edge.weight();
            if (distances[u] != Integer.MAX_VALUE && distances[u] + w < distances[v]) {
                throw new IllegalArgumentException("Graph contains a negative-weight cycle");
            }
        }

        executor.shutdown();

        List<Integer> distanceList = Arrays.stream(distances).boxed().toList();
        List<Integer> predecessorList = Arrays.stream(predecessors).boxed().toList();

        return new Result(distanceList, predecessorList);
    }

}
