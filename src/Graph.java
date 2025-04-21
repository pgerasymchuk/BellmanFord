import java.util.*;

// Represents a directed graph
public class Graph {

    public record Edge(int source, int destination, int weight) {}

    public final int V;
    public final int E;
    public final List<Edge> edges;

    public final int MIN_WEIGHT = 1;//-10000;
    public final int MAX_WEIGHT = 10000;

    public Graph(int V, int avgEdgesFromVertex, int randomState) {
        if (avgEdgesFromVertex > V - 1) {
            throw new IllegalArgumentException("Invalid average number of edges from vertex");
        }

        this.V = V;
        this.E = V * avgEdgesFromVertex;
        this.edges = new ArrayList<>(this.E);

        Random random = new Random(randomState);
        record EdgeVertices(int source, int destination) {}
        Set<EdgeVertices> edgeVerticesSet = new HashSet<>();

        for (int i = 0; i < this.E; i++) {
            int source, destination;
            do {
                source = random.nextInt(V);
                destination = random.nextInt(V);
            } while (source == destination || !edgeVerticesSet.add(new EdgeVertices(source, destination)));
            edges.add(new Edge(source, destination, random.nextInt(MAX_WEIGHT - MIN_WEIGHT + 1) + MIN_WEIGHT));
        }
    }
}
