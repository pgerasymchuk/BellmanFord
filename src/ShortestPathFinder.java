import java.util.List;

public interface ShortestPathFinder {
    record Result (List<Integer> distances, List<Integer> predecessors) {
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Result result = (Result) obj;
            for (int i = 0; i < distances.size(); i++) {
                if (!distances.get(i).equals(result.distances.get(i))) {
                    System.out.println("distances are incorrect");
                    return false;
                }
                if (!predecessors.get(i).equals(result.predecessors.get(i))) {
                    System.out.println("predecessors are incorrect");
                    return false;
                }
            }
            return true;
        }
    }

    Result findShortestPaths(Graph g, int source);
}
