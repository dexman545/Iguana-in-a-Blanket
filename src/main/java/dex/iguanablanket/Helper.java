package dex.iguanablanket;

public abstract class Helper {
    public static double clampValue(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }
}
