public class MathUtils {

    public final static double epsilon = 1e-6;

    public static boolean isLess(Double a, Double b) {
        return a + epsilon < b;
    }

    public static boolean equals(Double a, Double b) {
        return Math.abs(a - b) < epsilon;
    }
}
