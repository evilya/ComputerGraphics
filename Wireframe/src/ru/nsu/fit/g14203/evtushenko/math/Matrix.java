package ru.nsu.fit.g14203.evtushenko.math;

public class Matrix {
    private final double[][] matrix;
    private final int width;
    private final int height;

    public Matrix(double[][] matrix) {
        this.matrix = matrix;
        height = matrix.length;
        width = matrix[0].length;
    }

    public Matrix multiply(double multiplier) {
        double[][] result = new double[height][width];
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < height; i++) {
                result[j][i] = matrix[j][i] * multiplier;
            }
        }
        return new Matrix(result);
    }

    public Matrix multiply(Matrix other) {
        int m = matrix.length;
        int n = other.matrix[0].length;
        int o = other.matrix.length;

        double[][] res = new double[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < o; k++) {
                    res[i][j] += matrix[i][k] * other.matrix[k][j];
                }
            }
        }
        return new Matrix(res);
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
