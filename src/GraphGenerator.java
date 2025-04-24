import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GraphGenerator {

    public static Graph generateGraph(int V, int avgEdgesFromVertex, int minWeight, int maxWeight, int randomState) {
        if (avgEdgesFromVertex > V - 1) {
            throw new IllegalArgumentException("Invalid average number of edges from vertex");
        }

        int E = V * avgEdgesFromVertex;
        Graph g = new Graph(V, E);

        Random random = new Random(randomState);
        record EdgeVertices(int source, int destination) {}
        Set<EdgeVertices> edgeVerticesSet = new HashSet<>();

        for (int i = 0; i < E; i++) {
            int source, destination;
            do {
                source = random.nextInt(V);
                destination = random.nextInt(V - source) + source;
                //destination = random.nextInt(V);
            } while (source == destination || !edgeVerticesSet.add(new EdgeVertices(source, destination)));
            //g.edges.add(new Graph.Edge(source, destination, random.nextInt(maxWeight - minWeight + 1) + minWeight));
            g.edges[i] = new Graph.Edge(source, destination, random.nextInt(maxWeight - minWeight + 1) + minWeight);
        }

        return g;
    }
}
