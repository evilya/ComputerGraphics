package ru.nsu.fit.g14203.evtushenko.model;

import ru.nsu.fit.g14203.evtushenko.math.Matrix;
import ru.nsu.fit.g14203.evtushenko.model.geom.Shape3D;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Scaler {
    private final Model model;
    private Matrix scale = new Matrix(new double[][]{
            {1, 0, 0, 0},
            {0, 1, 0, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
    });

    public Scaler(Model model) {
        this.model = model;
    }

    public void update() {
        List<Shape3D> shapes = model.getSourceShapes();
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        double xMax = Double.MIN_VALUE;
        double xMin = Double.MAX_VALUE;
        double yMax = Double.MIN_VALUE;
        double yMin = Double.MAX_VALUE;
        double zMax = Double.MIN_VALUE;
        double zMin = Double.MAX_VALUE;
        for (Shape3D shape : shapes) {
            List<Double> coordinates = shape.getLines()
                    .stream()
                    .flatMap(l -> Stream.of(l.getEnd(), l.getStart()))
                    .flatMap(p -> Stream.of(p.getX(), p.getY(), p.getZ()))
                    .collect(Collectors.toList());

            max = Math.max(max, Collections.max(coordinates));
            min = Math.min(min, Collections.min(coordinates));

            List<Double> xs = shape.getLines()
                    .stream()
                    .flatMap(l -> Stream.of(l.getEnd().getX(), l.getStart().getX()))
                    .collect(Collectors.toList());

            List<Double> ys = shape.getLines()
                    .stream()
                    .flatMap(l -> Stream.of(l.getEnd().getY(), l.getStart().getY()))
                    .collect(Collectors.toList());

            List<Double> zs = shape.getLines()
                    .stream()
                    .flatMap(l -> Stream.of(l.getEnd().getZ(), l.getStart().getZ()))
                    .collect(Collectors.toList());

            xMax = Math.max(xMax, Collections.max(xs));
            xMin = Math.min(xMin, Collections.min(xs));

            yMax = Math.max(yMax, Collections.max(ys));
            yMin = Math.min(yMin, Collections.min(ys));

            zMax = Math.max(zMax, Collections.max(zs));
            zMin = Math.min(zMin, Collections.min(zs));
        }
//        scale = new Matrix(new double[][]{
//                {2. / (max - min), 0, 0, -(min + max) / (max - min)},
//                {0, 2. / (max - min), 0, -(min + max) / (max - min)},
//                {0, 0, 2. / (max - min), -(min + max) / (max - min)},
//                {0, 0, 0, 1}
//        });
//        scale = new Matrix(new double[][]{
//                {1. / (max - min), 0, 0, 0},
//                {0, 1. / (max - min), 0, 0},
//                {0, 0, 1. / (max - min), 0},
//                {0, 0, 0, 1}./
//        });
        scale = new Matrix(new double[][]{
                {2. / (max - min), 0, 0, -(xMax + xMin) / ((max - min))},
                {0, 2. / (max - min), 0, -(yMax + yMin) / ((max - min))},
                {0, 0, 2. / (max - min), -(zMax + zMin) / ((max - min))},
                {0, 0, 0, 1}
        });
    }

    public Matrix getMatrix() {
        return scale;
    }
}
