package ru.nsu.fit.g14203.evtushenko.utils;

public class MathUtils {
    private static final double EPSILON = 1e-9;

    public static int constraint(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    public static double constraint(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    public static int constraint(int value) {
        return Math.min(Math.max(value, 0), 255);
    }

    public static boolean isInRange(double value, double firstBound, double secondBound) {
        return value - Math.min(firstBound, secondBound) >= EPSILON
                && value - Math.max(firstBound, secondBound) < EPSILON;
    }
}
