import java.util.HashSet;
import java.util.Random;
import java.util.Set;

// Represents a directed graph
public class Graph {

    public record Edge(int source, int destination, int weight) {}

    public final int V;
    public final int E;
    public final Edge[] edges;

    // density = E / ( V * (V - 1))
    public Graph(int V, float density, int randomState) {
        if (density <= 0.0 || density > 1.0) {
            throw new IllegalArgumentException("Graph density must be between 0.0 and 1.0");
        }

        this.V = V;
        this.E = (int) density * V * (V - 1);
        this.edges = new Edge[E];

        Random random = new Random(randomState);
        Set<String> edgeSet = new HashSet<>();
        for (int i = 0; i < E; i++) {
            int source, destination;
            do {
                source = random.nextInt(V);
                destination = random.nextInt(V);
            } while (source == destination || !edgeSet.add(source + "-" + destination));
            edges[i] = new Edge(source, destination, random.nextInt());
        }
    }
}
