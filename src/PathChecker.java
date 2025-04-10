import java.util.*;

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
        record EdgeVertices(int start, int end) {}
        Map<EdgeVertices, Integer> edges = new HashMap<>();
        for (Graph.Edge edge : g.edges){
            edges.put(new EdgeVertices(edge.source(), edge.destination()), edge.weight());
        }

        List<Integer> distances = result.distances();
        List<Integer> predecessors = result.predecessors();

        Map<Integer, Integer> realDistances = new HashMap<>();

        for (int v = 0; v < distances.size(); v++) {
            if (v == source) {
                realDistances.put(v, 0);
            }

            int dist = 0;
            int current = v;
            boolean reachable = true;

            while (current != source) {
                int prev = predecessors.get(current);
                if (prev == -1) {
                    reachable = false;
                    break;
                }

                int edgeWeight = edges.get(new EdgeVertices(prev, current));
                dist += edgeWeight;
                current = prev;
            }

            if (reachable) {
                realDistances.put(v, dist);
            }
        }

        for (int v = 0; v < distances.size(); v++) {
            if (!distances.get(v).equals(realDistances.get(v))) {
                return false;
            }
        }
        return true;
    }
}
