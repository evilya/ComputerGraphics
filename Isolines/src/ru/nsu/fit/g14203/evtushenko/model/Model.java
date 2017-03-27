package ru.nsu.fit.g14203.evtushenko.model;

import ru.nsu.fit.g14203.evtushenko.Observable;

public class Model extends Observable {
    private int width;
    private int height;

    private double[][] nodes;
    private double min;
    private double max;

    private double a;
    private double b;
    private double c;
    private double d;

    private int n;
    private int k;
    private int m;

    private Color[] colors;
    private double[] isolines;

    void updateNodeValues() {
        min = Double.MAX_VALUE;
        max = Double.MIN_VALUE;
        double dx = Math.abs(b - a) / (m - 1);
        double dy = Math.abs(c - d) / (k - 1);
        nodes = new double[k][m];
        for (int y = 0; y < k; y++) {
            for (int x = 0; x < m; x++) {
                double value = value(dx * x, dy * y);
                nodes[y][x] = value;
                max = value > max ? value : max;
                min = value < min ? value : min;
            }
        }
    }

    void updateMinMax(){
        
    }

    public double value(double x, double y) {
        return x*x + y*y;
    }

    public void setViewSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static class Color {
        private int r;
        private int g;
        private int b;
    }
}
