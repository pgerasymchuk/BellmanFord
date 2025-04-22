import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//import java.util.concurrent.atomic.AtomicBoolean;

public class BellmanFordParallel implements ShortestPathFinder {

    @Override
    public Result findShortestPaths(Graph g, int source) {
        int[] distances = new int[g.V];
        int[] predecessors = new int[g.V];
        Arrays.fill(distances, Integer.MAX_VALUE);
        Arrays.fill(predecessors, -1);
        distances[source] = 0;

        //AtomicBoolean changes = new AtomicBoolean(false);

        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        long t1 = System.nanoTime();

        byte[] groupNumberOfEdges = new byte[g.E];
        for (int i = 0; i < g.E; i++) {
            groupNumberOfEdges[i] = (byte)(g.edges.get(i).destination() % numThreads);
        }

        long t2 = System.nanoTime();
        System.out.println("Initializing, ms: " + (t2 - t1) * 1e-6);

        ArrayList<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < g.V - 1; i++) {
            //changes.set(false);

            for (int j = 0; j < numThreads; j++) {
                int finalJ = j;
                tasks.add(() -> {
                    //boolean localChange = false;
                    for (int k = 0; k < g.E; k++) {
                        if (groupNumberOfEdges[k] == finalJ) {
                            Graph.Edge edge = g.edges.get(k);
                            int u = edge.source();
                            int v = edge.destination();
                            int w = edge.weight();

                            if (distances[u] != Integer.MAX_VALUE && distances[u] + w < distances[v]) {
                                distances[v] = distances[u] + w;
                                predecessors[v] = u;
                                //localChange = true;
                            }
                        }
                    }
                    //if (localChange) { changes.set(true); }
                    return null;
                });
            }

            try {
                executor.invokeAll(tasks);
                tasks.clear();
            } catch (InterruptedException e) { throw new RuntimeException(e); }

            //if (!changes.get()) { System.out.printf("parallel break at %s iteration\n", i); break; } // no updates in this iteration
        }

        for (int j = 0; j < numThreads; j++) {
            int finalJ = j;
            tasks.add(() -> {
                for (int k = 0; k < g.E; k++) {
                    if (groupNumberOfEdges[k] == finalJ) {
                        Graph.Edge edge = g.edges.get(k);
                        int u = edge.source();
                        int v = edge.destination();
                        int w = edge.weight();

                        if (distances[u] != Integer.MAX_VALUE && distances[u] + w < distances[v]) {
                            throw new IllegalArgumentException("Graph contains a negative-weight cycle");
                        }
                    }
                }
                return null;
            });
        }

        try {
            executor.invokeAll(tasks);
            tasks.clear();
        } catch (InterruptedException e) { throw new RuntimeException(e); }

        executor.shutdown();

        List<Integer> distanceList = Arrays.stream(distances).boxed().toList();
        List<Integer> predecessorList = Arrays.stream(predecessors).boxed().toList();

        return new Result(distanceList, predecessorList);
    }

}
