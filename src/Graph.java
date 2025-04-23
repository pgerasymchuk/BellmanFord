import java.util.*;

// Represents a directed graph
public class Graph {

    public record Edge(int source, int destination, int weight) {}

    public final int V;
    public final int E;
    public final List<Edge> edges;

    public Graph(int V, int E) {
        this.V = V;
        this.E = E;
        this.edges = new ArrayList<>(E);
    }
}
