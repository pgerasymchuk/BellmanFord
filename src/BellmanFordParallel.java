import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class BellmanFordParallel implements ShortestPathFinder {

    @Override
    public Result findShortestPaths(Graph g, int source) {
        List<Integer> distances = new ArrayList<>(Collections.nCopies(g.V, Integer.MAX_VALUE));
        distances.set(source, 0);
        List<Integer> predecessors = new ArrayList<>(Collections.nCopies(g.V, -1));
        AtomicBoolean changes = new AtomicBoolean(false);

        int numThreads = Runtime.getRuntime().availableProcessors();
//        int numThreads = 100;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        //int edgesPerThread = Math.max(1, g.E / numThreads);

        long t1 = System.nanoTime();

        List<List<Graph.Edge>> groupedEdges = new ArrayList<>(g.V); // edges grouped by destination vertex
        for (int i = 0; i < g.V; i++) {
            groupedEdges.add(new ArrayList<>());
        }
        for (Graph.Edge edge : g.edges) {
            groupedEdges.get(edge.destination()).add(edge);
        }

        long t2 = System.nanoTime();
        System.out.println("Initializing, ms: " + (t2 - t1) * 1e-6);

        for (int i = 0; i < g.V - 1; i++) {
            changes.set(false);

            List<Future<?>> futures = new ArrayList<>();
            Object lock = new Object();

            /*for (int start = 0; start < g.E; start += edgesPerThread) {
                int finalStart = start;
                int end = Math.min(start + edgesPerThread, g.E);*/
            //for (int dest = 0; dest < g.V; dest++) {
            int edgeGroupsPerThread = groupedEdges.size() / numThreads;   // rewrite to g.V / numThreads for performance
            for (int j = 0; j < groupedEdges.size(); j += edgeGroupsPerThread) {  // rewrite to g.V / numThreads for performance

                int finalJ = j;
                futures.add(executor.submit(() -> {
                    boolean localChange = false;

                    for (int k = finalJ; k < finalJ + edgeGroupsPerThread; k++){
                        for (Graph.Edge edge : groupedEdges.get(k)) {
                            //synchronized (lock) {
                                if (distances.get(edge.source()) != Integer.MAX_VALUE &&
                                        distances.get(edge.source()) + edge.weight() < distances.get(edge.destination())) {

                                    distances.set(edge.destination(), distances.get(edge.source()) + edge.weight());
                                    predecessors.set(edge.destination(), edge.source());
                                    localChange = true;
                                }
                            //}
                        }
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
            if (distances.get(edge.source()) != Integer.MAX_VALUE &&
                    distances.get(edge.source()) + edge.weight() < distances.get(edge.destination())) {

                throw new IllegalArgumentException("Graph contains a negative-weight cycle");
            }
        }

        executor.shutdown();
        return new Result(distances, predecessors);
    }
}
