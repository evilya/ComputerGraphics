package ru.nsu.fit.g14203.evtushenko.utils;

public class MathUtils {
    public static int constraint(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    public static double constraint(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    public static int constraint(int value) {
        return Math.min(Math.max(value, 0), 255);
    }
}
