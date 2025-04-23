public class Main {
    public static void main(String[] args) {

        ShortestPathFinder sequential = new BellmanFordSequential();
        ShortestPathFinder parallel = new BellmanFordParallel(Runtime.getRuntime().availableProcessors());

        long t0 = System.nanoTime();
        Graph g = new Graph(5000, 100, 0);

        long t1 = System.nanoTime();
        System.out.println("Generating graph time, ms: " + (t1 - t0) * 1e-6);
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
                (result1.distances().equals(result2.distances())));
        System.out.println("Sequential and parallel predecessors are identical: " +
                 result1.predecessors().equals(result2.predecessors()));

        boolean isSequentialCorrect = PathChecker.verifySolution(g, 0, result1);
        boolean isParallelCorrect = PathChecker.verifySolution(g, 0, result2);
        System.out.println("Sequential solution is correct: " + isSequentialCorrect);
        System.out.println("Parallel solution is correct: " + isParallelCorrect);
    }
}