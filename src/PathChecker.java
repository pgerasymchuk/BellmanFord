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
        Map<EdgeVertices, Double> edges = new HashMap<>();
        for (Graph.Edge edge : g.edges){
            edges.put(new EdgeVertices(edge.source(), edge.destination()), edge.weight());
        }

//        int[] distances = result.distances();
//        int[] predecessors = result.predecessors();
        List<Double> distances = result.distances();
        List<Integer> predecessors = result.predecessors();

        Map<Integer, Double> realDistances = new HashMap<>();

        //for (int v = 0; v < distances.length; v++) {
        for (int v = 0; v < distances.size(); v++) {
            if (v == source) {
                realDistances.put(v, 0.0);
                continue;
            }

            double dist = 0.0;
            int current = v;
            boolean reachable = true;

            while (current != source) {
                //int prev = predecessors[current];
                int prev = predecessors.get(current);
                if (prev == -1) {
                    reachable = false;
                    break;
                }

                double edgeWeight = edges.get(new EdgeVertices(prev, current));
                dist += edgeWeight;
                current = prev;
            }

            realDistances.put(v, reachable ? dist : Double.MAX_VALUE);
        }

//        for (int v = 0; v < distances.length; v++) {
//            if (distances[v] != realDistances.get(v)) {
//                return false;
//            }
//        }
        for (int v = 0; v < distances.size(); v++) {
            if (!MathUtils.equals(distances.get(v), realDistances.get(v))) {
                return false;
            }
        }
        return true;
    }
}
