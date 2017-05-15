package ru.nsu.fit.g14203.evtushenko.utils;

import ru.nsu.fit.g14203.evtushenko.math.Matrix;
import ru.nsu.fit.g14203.evtushenko.model.geom.Point3D;


public class PointMatrixConverter {
    public static Matrix pointToMatrix(Point3D point){
        return new Matrix(new double[][]{
                {point.getX()},
                {point.getY()},
                {point.getZ()},
                {1}
        });
    }

    public static Point3D matrixToPoint(Matrix matrix){
        double[][] matrix1 = matrix.getMatrix();
        return new Point3D(matrix1[0][0], matrix1[1][0], matrix1[2][0]);
    }
}
