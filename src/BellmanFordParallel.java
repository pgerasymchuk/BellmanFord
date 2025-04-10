import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class BellmanFordParallel implements ShortestPathFinder {

    @Override
    public Result findShortestPaths(Graph g, int source) {
        List<Integer> distances = Collections.synchronizedList(new ArrayList<>(Collections.nCopies(g.V, Integer.MAX_VALUE)));
        distances.set(source, 0);
        List<Integer> predecessors = Collections.synchronizedList(new ArrayList<>(Collections.nCopies(g.V, -1)));
        AtomicBoolean changes = new AtomicBoolean(false);

        int numThreads = Runtime.getRuntime().availableProcessors();
//        int numThreads = 100;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        int edgesPerThread = Math.max(1, g.E / numThreads);

        for (int i = 0; i < g.V - 1; i++) {
            changes.set(false);

            List<Future<?>> futures = new ArrayList<>();
            Object lock = new Object();

            for (int start = 0; start < g.E; start += edgesPerThread) {
                int finalStart = start;
                int end = Math.min(start + edgesPerThread, g.E);

                futures.add(executor.submit(() -> {
                    boolean localChange = false;

                    for (int j = finalStart; j < end; j++) {
                        Graph.Edge edge = g.edges.get(j);

                        //synchronized (lock) {
                            if (distances.get(edge.source()) != Integer.MAX_VALUE &&
                                distances.get(edge.source()) + edge.weight() < distances.get(edge.destination())) {

                                distances.set(edge.destination(), distances.get(edge.source()) + edge.weight());
                                predecessors.set(edge.destination(), edge.source());
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

            if (!changes.get()) { break; } // no updates in this iteration
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
