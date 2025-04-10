public class Main {
    public static void main(String[] args) {

        ShortestPathFinder sequential = new BellmanFordSequential();
        ShortestPathFinder parallel = new BellmanFordParallel();

        long t0 = System.nanoTime();

        Graph g = new Graph(5000, 0.5f, 0);

        long t1 = System.nanoTime();
        ShortestPathFinder.Result result1 = sequential.findShortestPaths(g, 0);
        long t2 = System.nanoTime();
        ShortestPathFinder.Result result2 = parallel.findShortestPaths(g, 0);
        long t3 = System.nanoTime();

        System.out.println("Generating graph time, ms: " + (t1 - t0) * 1e-6);
        System.out.println("Sequential time, ms: " + (t2 - t1) * 1e-6);
        System.out.println("Parallel time, ms: " + (t3 - t2) * 1e-6);
        System.out.println("Speedup: " + (t2 - t1) / (double)(t3 - t2));

        boolean isSequentialCorrect = PathChecker.verifySolution(g, 0, result1);
        boolean isParallelCorrect = PathChecker.verifySolution(g, 0, result2);

        System.out.println("Sequential solution is correct: " + isSequentialCorrect);
        System.out.println("Parallel solution is correct: " + isParallelCorrect);


    }
}