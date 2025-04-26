import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Callable;

public class Main {

    private static final int JVM_WARM_UP_ITERATIONS = 20;
    private static final int MIN_EDGE_WEIGHT = -10000;
    private static final int MAX_EDGE_WEIGHT = -10000;
    private static final int REPETITIONS = 4;

    public static void main(String[] args) {

        BellmanFordSequential sequential = new BellmanFordSequential();
        BellmanFordParallel parallel = new BellmanFordParallel();

        int sourceVertex = 0;
/*
        for (int i = 0; i < JVM_WARM_UP_ITERATIONS; i++){
            Graph g = GraphGenerator.generateGraph(10000, 100, MIN_EDGE_WEIGHT, MAX_EDGE_WEIGHT, 0);
            if (!PathChecker.verifySolution(g, sourceVertex, sequential.findShortestPaths(g, 0)) ||
                !PathChecker.verifySolution(g, sourceVertex, parallel.findShortestPaths(g, 0))) {
                    System.out.println("Incorrect solution obtained during JVM warm-up!");
            }
        }*/

        //System.out.println("Number of available processors: " + Runtime.getRuntime().availableProcessors())

        //int[] numVertices = new int[] { 1000, 2000, 5000, 10000, 20000, 30000, 40000, 50000 };
        //int[] avgEdgesFromVertex = new int[] { 5, 10, 20, 50, 70, 100, 150, 200 };
        //int[] threadsNum = new int[] { 2, 4, 8 };

        int[] numVertices = new int[] { 5000, 10000, 20000 };
        int[] avgEdgesFromVertex = new int[] { 10, 50, 100 };
        int[] numThreads = new int[] { 2, 4, 8 };

        try (PrintWriter writer = new PrintWriter(new FileWriter("results.csv", true))) {

        } catch (IOException e) {
            System.out.println("Error occurred when writing to the file!");
        }

        System.out.println("Vert. \t AvgEdg \t Threads \t Seq.(ms) \t Par.(ms) \t Speedup \t Efficiency \t Cost of computation");

            //System.out.println("Threads number: " + numThreads[i]);
        for (int i : numVertices) {
            for (int j : avgEdgesFromVertex) {
                Graph g = GraphGenerator.generateGraph(i, j, MIN_EDGE_WEIGHT, MAX_EDGE_WEIGHT, 0);

                long sumSeqTime = 0;
                long[] sumParallelTimes = new long[numThreads.length];

                for (int r = 0; r < REPETITIONS; r++) {

                    long t1 = System.nanoTime();
                    ShortestPathFinder.Result seqResult = sequential.findShortestPaths(g, sourceVertex);
                    sumSeqTime += (System.nanoTime() - t1);

                    for (int k = 0; k < numThreads.length; k++) {
                        parallel.setNumThreads(numThreads[k]);
                        long t2 = System.nanoTime();
                        ShortestPathFinder.Result parallelResult = parallel.findShortestPaths(g, sourceVertex);
                        sumParallelTimes[k] += (System.nanoTime() - t2);
                    }
                }

                for (int k = 0; k < numThreads.length; k++) {
                    System.out.printf("%d \t %d \t\t %d \t\t\t %.1f \t\t %.1f \t\t %.2f \t\t %.2f \t\t\t %.1f \n",
                            i, j, numThreads[k],
                            sumSeqTime * 1e-6 / REPETITIONS, sumParallelTimes[k] * 1e-6 / REPETITIONS,
                            (double) sumSeqTime / sumParallelTimes[k],
                            (double) sumSeqTime / sumParallelTimes[k] / numThreads[k],
                            sumParallelTimes[k] * 1e-6 / numThreads[k] / REPETITIONS);
                }
                System.out.println();
            }
        }

            /*System.out.println("Generating graph time, ms: " + (t1 - t0) * 1e-6);
        ShortestPathFinder.Result result1 = sequential.findShortestPaths(g, 0);
        long t2 = System.nanoTime();
        System.out.println("Sequential time, ms: " + (t2 - t1) * 1e-6);

        System.out.print("Parallel time, ms: ");
        ShortestPathFinder.Result result2 = null;
        int nTests = 1;
        long sum = 0;
        for (int i = 0; i < nTests; i++) {
            long t3 = System.nanoTime();
            result2 = parallel.findShortestPaths(g, 0);
            long t4 = System.nanoTime();
            System.out.print((t4 - t3) * 1e-6 + " ");
            sum += (t4 - t3);
        }
        double avgParallelTime = sum / (double)nTests;
        
        System.out.println("Average parallel time, ms: " + avgParallelTime * 1e-6);
        System.out.println("Speedup: " + (t2 - t1) / avgParallelTime);

        System.out.println("Sequential and parallel distances are identical: " +
                result1.distances().equals(result2.distances()));
        System.out.println("Sequential and parallel predecessors are identical: " +
                result1.predecessors().equals(result2.predecessors()));

        boolean isSequentialCorrect = PathChecker.verifySolution(g, 0, result1);
        boolean isParallelCorrect = PathChecker.verifySolution(g, 0, result2);
        System.out.println("Sequential solution is correct: " + isSequentialCorrect);
        System.out.println("Parallel solution is correct: " + isParallelCorrect);*/
    }
//
//    private long measureExecutionTime(Callable<ShortestPathFinder.Result> method){
//        long start = System.nanoTime();
//        method.call();
//
//    }
}