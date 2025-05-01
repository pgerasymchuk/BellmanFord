import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

public class Main {

    private static final int JVM_WARM_UP_ITERATIONS = 20;
    private static final int MIN_EDGE_WEIGHT = -10000;
    private static final int MAX_EDGE_WEIGHT = 10000;
    private static final int REPETITIONS = 4;
    private static final String path1 = "test1.csv";
    private static final String path2 = "test2.csv";

    public static void main(String[] args) {
        System.out.println("Number of available processors: " + Runtime.getRuntime().availableProcessors());

        System.out.println("JVM warm-up started");
        jvmWarmup();
        System.out.println("JVM warm-up finished");

        int[] numVerticesArr = new int[] { 10000, 20000, 30000 };
        int[] avgEdgesFromVertexArr = new int[] { 50, 100, 150 };
        int[] numThreadsArr = new int[] { 2, 4, 6 };
        benchmark(numVerticesArr, avgEdgesFromVertexArr, numThreadsArr, path1);

        System.out.println("Test 1 finished");

        numVerticesArr = new int[] { 1000, 2000, 5000, 10000, 20000, 30000, 40000, 50000 };
        avgEdgesFromVertexArr = new int[] { 5, 10, 20, 50, 70, 100, 150 };
        numThreadsArr = new int[] { 4 };
        benchmark(numVerticesArr, avgEdgesFromVertexArr, numThreadsArr, path2);

        System.out.println("Test 2 finished");
    }

    private static void benchmark(int[] numVerticesArr, int[] avgEdgesFromVertexArr, int[] numThreadsArr, String filePath){
        BellmanFordSequential sequential = new BellmanFordSequential();
        BellmanFordParallel parallel = new BellmanFordParallel();
        int sourceVertex = 0;

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, false))) {
            writer.println("Vertices,AvgEdges,Threads,Sequential(ms),Parallel(ms),Speedup,Efficiency,CostOfComputation");

            for (int numVertices : numVerticesArr) {
                for (int avgEdgesFromVertex : avgEdgesFromVertexArr) {
                    Graph g = GraphGenerator.generateGraph(numVertices, avgEdgesFromVertex,
                            MIN_EDGE_WEIGHT, MAX_EDGE_WEIGHT, 0);

                    long sumSeqTime = 0;
                    long[] sumParallelTimes = new long[numThreadsArr.length];

                    for (int r = 0; r < REPETITIONS; r++) {

                        ShortestPathFinder.Result seqResult = null;
                        ShortestPathFinder.Result parallelResult = null;

                        if (r % 2 == 0) {
                            long t1 = System.nanoTime();
                            seqResult = sequential.findShortestPaths(g, sourceVertex);
                            sumSeqTime += (System.nanoTime() - t1);
                        }

                        for (int k = 0; k < numThreadsArr.length; k++) {
                            parallel.setNumThreads(numThreadsArr[k]);
                            long t1 = System.nanoTime();
                            parallelResult = parallel.findShortestPaths(g, sourceVertex);
                            sumParallelTimes[k] += (System.nanoTime() - t1);
                        }

                        if (r % 2 == 1) {
                            long t1 = System.nanoTime();
                            seqResult = sequential.findShortestPaths(g, sourceVertex);
                            sumSeqTime += (System.nanoTime() - t1);
                        }

                        if (!seqResult.distances().equals(parallelResult.distances()) ||
                            !PathChecker.verifySolution(g, 0, seqResult)) {
                            System.out.printf("Obtained results are not correct: Graph(%d, %d)\n", numVertices, avgEdgesFromVertex);
                        }
                    }

                    for (int k = 0; k < numThreadsArr.length; k++) {
                        writer.printf(Locale.US,"%d,%d,%d,%.1f,%.1f,%.2f,%.2f,%.1f\n",
                                numVertices, avgEdgesFromVertex, numThreadsArr[k],
                                sumSeqTime * 1e-6 / REPETITIONS, sumParallelTimes[k] * 1e-6 / REPETITIONS,
                                (double) sumSeqTime / sumParallelTimes[k],
                                (double) sumSeqTime / sumParallelTimes[k] / numThreadsArr[k],
                                sumParallelTimes[k] * 1e-6 * numThreadsArr[k] / REPETITIONS);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error occurred when writing to the file!");
        }
    }

    private static void jvmWarmup() {
        BellmanFordSequential sequential = new BellmanFordSequential();
        BellmanFordParallel parallel = new BellmanFordParallel();
        int sourceVertex = 0;

        Graph g = GraphGenerator.generateGraph(10000, 100, MIN_EDGE_WEIGHT, MAX_EDGE_WEIGHT, 0);
        for (int i = 0; i < JVM_WARM_UP_ITERATIONS; i++) {
            if (!PathChecker.verifySolution(g, sourceVertex, sequential.findShortestPaths(g, 0)) ||
                !PathChecker.verifySolution(g, sourceVertex, parallel.findShortestPaths(g, 0))) {
                System.out.println("Obtained results during JVM warm-up are not correct!");
            }
        }
    }
}