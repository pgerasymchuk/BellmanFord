import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PathChecker {

    public static List<Integer> getPath(List<Integer> predecessors, int destination) {
        List<Integer> path = new ArrayList<>();
        for (int at = destination; at != -1; at = predecessors.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return path.size() > 1 ? path : new ArrayList<>();
    }

    public static boolean verifySolution(Graph g, int source, ShortestPathFinder.Result result) {
        if (result == null) return false;

        List<Integer> distances = result.distances();
        List<Integer> predecessors = result.predecessors();
        boolean isValid = true;

        // Check 1: Source distance should be 0
        if (distances.get(source) != 0) {
            System.out.println("Verification failed: Source distance is not 0");
            isValid = false;
        }

        // Check 2: Triangle inequality for all edges
        for (Graph.Edge edge : g.edges) {
            int u = edge.source();
            int v = edge.destination();
            int weight = edge.weight();

            if (distances.get(u) != Integer.MAX_VALUE &&
                    distances.get(v) != Integer.MAX_VALUE &&
                    distances.get(u) + weight < distances.get(v)) {
                System.out.println("Verification failed: Triangle inequality violated for edge " +
                        u + " -> " + v);
                isValid = false;
            }
        }

        // Check 3: Path validity
        for (int v = 0; v < g.V; v++) {
            if (v == source || distances.get(v) == Integer.MAX_VALUE) continue;

            List<Integer> path = getPath(predecessors, v);
            if (path.isEmpty()) {
                System.out.println("Verification failed: No path to vertex " + v +
                        " despite finite distance");
                isValid = false;
                continue;
            }

            // Verify path starts at source and ends at destination
            if (path.getFirst() != source || path.getLast() != v) {
                System.out.println("Verification failed: Invalid path endpoints for vertex " + v);
                isValid = false;
            }

            // Verify path distance matches computed distance
            int pathDistance = 0;
            for (int i = 0; i < path.size() - 1; i++) {
                int current = path.get(i);
                int next = path.get(i + 1);
                boolean foundEdge = false;

                for (Graph.Edge edge : g.edges) {
                    if (edge.source() == current && edge.destination() == next) {
                        pathDistance += edge.weight();
                        foundEdge = true;
                        break;
                    }
                }

                if (!foundEdge) {
                    System.out.println("Verification failed: Invalid edge in path to vertex " + v);
                    isValid = false;
                }
            }

            if (pathDistance != distances.get(v)) {
                System.out.println("Verification failed: Path distance mismatch for vertex " + v +
                        ". Expected: " + distances.get(v) + ", Found: " + pathDistance);
                isValid = false;
            }
        }

        return isValid;
    }
}
