package ru.nsu.fit.g14203.evtushenko.model;

import ru.nsu.fit.g14203.evtushenko.math.Matrix;

public class CameraConverter {
    private Matrix toCam = new Matrix(new double[][]{
            {1, 0, 0, 10},
            {0, 1, 0, 0},
            {0, 0, -1, 0},
            {0, 0, 0, 1}
    });

    public Matrix getMatrix(){
        return toCam;
    }
}
