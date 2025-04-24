import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class BellmanFordParallel implements ShortestPathFinder {

    private final int numThreads;

    public BellmanFordParallel(int numThreads) {
        this.numThreads = numThreads;
    }

    @Override
    public Result findShortestPaths(Graph g, int source) {
        List<Integer> distances = new ArrayList<>(Collections.nCopies(g.V, Integer.MAX_VALUE));
        distances.set(source, 0);
        List<Integer> predecessors = new ArrayList<>(Collections.nCopies(g.V, -1));

        AtomicBoolean changes = new AtomicBoolean(false);

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        byte[] groupNumberOfEdges = new byte[g.E];
        for (int i = 0; i < g.E; i++) {
            groupNumberOfEdges[i] = (byte)(g.edges[i].destination() % numThreads);
        }

        ArrayList<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < g.V - 1; i++) {
            changes.set(false);

            for (int j = 0; j < numThreads; j++) {
                int finalJ = j;
                tasks.add(() -> {
                    boolean localChange = false;
                    int k = 0;
                    for (Graph.Edge edge : g.edges) {
                        if (groupNumberOfEdges[k] == finalJ) {
                            if (distances.get(edge.source()) != Integer.MAX_VALUE &&
                                    distances.get(edge.source()) + edge.weight() < distances.get(edge.destination())) {
                                distances.set(edge.destination(), distances.get(edge.source()) + edge.weight());
                                predecessors.set(edge.destination(), edge.source());
                                localChange = true;
                            }
                        }
                        k++;
                    }
                    if (localChange) { changes.set(true); }
                    return null;
                });
            }

            try {
                executor.invokeAll(tasks);
                tasks.clear();
            } catch (InterruptedException e) { throw new RuntimeException(e); }

            if (!changes.get()) { break; }
        }

        for (int j = 0; j < numThreads; j++) {
            int finalJ = j;
            tasks.add(() -> {
                int k = 0;
                for (Graph.Edge edge : g.edges) {
                    if (groupNumberOfEdges[k] == finalJ) {
                        if (distances.get(edge.source()) != Integer.MAX_VALUE &&
                                distances.get(edge.source()) < distances.get(edge.destination()) - edge.weight()) {

                            throw new IllegalArgumentException("Graph contains a negative-weight cycle");
                        }
                    }
                    k++;
                }
                return null;
            });
        }

        try {
            executor.invokeAll(tasks);
            tasks.clear();
        } catch (InterruptedException e) { throw new RuntimeException(e); }

        executor.shutdown();

        return new Result(distances, predecessors);
    }
}
