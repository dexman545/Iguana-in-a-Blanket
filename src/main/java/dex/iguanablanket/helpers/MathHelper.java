package dex.iguanablanket.helpers;

public abstract class MathHelper {
    public static double clampValue(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }
}
