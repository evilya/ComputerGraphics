package ru.nsu.fit.g14203.evtushenko.model;

import ru.nsu.fit.g14203.evtushenko.math.Matrix;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class SceneRotator {
    private Matrix sceneRotation = new Matrix(new double[][]{
            {1, 0, 0, 0},
            {0, 1, 0, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
    });

    public void rotate(double angleX, double angleY) {
        Matrix xRotate = new Matrix(new double[][]{
                {cos(angleX), -sin(angleX), 0, 0},
                {sin(angleX), cos(angleX), 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        });
        Matrix yRotate = new Matrix(new double[][]{
                {cos(angleY), 0, sin(angleY), 0},
                {0, 1, 0, 0},
                {-sin(angleY), 0, cos(angleY), 0},
                {0, 0, 0, 1},
        });
        sceneRotation = yRotate.multiply(xRotate).multiply(sceneRotation);
    }

    public Matrix getMatrix() {
        return sceneRotation;
    }
}
