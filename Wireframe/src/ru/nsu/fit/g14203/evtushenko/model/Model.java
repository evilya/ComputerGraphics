package ru.nsu.fit.g14203.evtushenko.model;

import ru.nsu.fit.g14203.evtushenko.EventType;
import ru.nsu.fit.g14203.evtushenko.Observable;
import ru.nsu.fit.g14203.evtushenko.utils.Clipper;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.awt.geom.Point2D.distance;
import static java.lang.Math.*;


public class Model extends Observable implements Runnable {

    private BlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<>(100);

    private Shape<Point3D> box;
    private List<List<Point2D>> splines = new ArrayList<>();
    private List<Shape<Point2D>> resultShapes = new ArrayList<>();
    private List<Shape<Point3D>> sourceShapes = new ArrayList<>();

    private double a = 0.0;
    private double b = 1.;
    private double c = 0.;
    private double d = 2 * PI;
    private int n = 10;
    private int m = 10;
    private int k = 5;

    private Matrix rotate = new Matrix(new double[][]{
            {1, 0, 0, 0},
            {0, 1, 0, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
    });

    private double max = 1, min = -1;
    private Matrix scale = new Matrix(new double[][]{
            {1, 0, 0, 0},
            {0, 1, 0, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
    });

    private Matrix toCam = new Matrix(new double[][]{
            {1, 0, 0, 10},
            {0, 1, 0, 0},
            {0, 0, -1, 0},
            {0, 0, 0, 1}
    });

    private double sW = 1.;
    private double sH = 1.;
    private double zB = 12;
    private double zF = 1;
    private Matrix ortho = new Matrix(new double[][]{
            {zB / (zB - zF), 0, 0, -zB * zF / (zB - zF)},
            {0, 2 * zF / sW, 0, 0},
            {0, 0, 2 * zF / sH, 0},
            {1, 0, 0, 0}
    });


    public Model() {
        setBox(new Shape<>(new ArrayList<>(Arrays.asList(
                new Line<>(new Point3D(1, 1, 1), new Point3D(1, -1, 1)),
                new Line<>(new Point3D(-1, -1, -1), new Point3D(-1, -1, 1)),
                new Line<>(new Point3D(-1, -1, -1), new Point3D(-1, 1, -1)),
                new Line<>(new Point3D(-1, -1, -1), new Point3D(1, -1, -1)),
                new Line<>(new Point3D(1, 1, 1), new Point3D(-1, 1, 1)),
                new Line<>(new Point3D(1, 1, 1), new Point3D(1, 1, -1)),
                new Line<>(new Point3D(1, 1, -1), new Point3D(1, -1, -1)),
                new Line<>(new Point3D(1, 1, -1), new Point3D(-1, 1, -1)),
                new Line<>(new Point3D(1, -1, 1), new Point3D(-1, -1, 1)),
                new Line<>(new Point3D(1, -1, 1), new Point3D(1, -1, -1)),
                new Line<>(new Point3D(-1, 1, 1), new Point3D(-1, -1, 1)),
                new Line<>(new Point3D(-1, 1, 1), new Point3D(-1, 1, -1)))),
                new Point3D(0, 0, 0), Color.BLACK, 2.f));    }

    public void rotate(double angleX, double angleY) {
        taskQueue.clear();
        taskQueue.add(() -> {
            double sc = 1.;
            Matrix xRotate = new Matrix(new double[][]{
                    {cos(angleX / sc), -sin(angleX / sc), 0, 0},
                    {sin(angleX / sc), cos(angleX / sc), 0, 0},
                    {0, 0, 1, 0},
                    {0, 0, 0, 1}
            });
            Matrix yRotate = new Matrix(new double[][]{
                    {cos(angleY / sc), 0, sin(angleY / sc), 0},
                    {0, 1, 0, 0},
                    {-sin(angleY / sc), 0, cos(angleY / sc), 0},
                    {0, 0, 0, 1},
            });
            rotate = yRotate.multiplyRight(xRotate).multiplyRight(rotate);
            notifyObservers(EventType.REPAINT);
        });
    }

    public void zoom(int z) {
        taskQueue.clear();
        taskQueue.add(() -> {
            zF = Math.exp(z * 0.1);
            ortho = new Matrix(new double[][]{
                    {zB / (zB - zF), 0, 0, -zB * zF / (zB - zF)},
                    {0, 2 * zF / sW, 0, 0},
                    {0, 0, 2 * zF / sH, 0},
                    {1, 0, 0, 0}
            });
            notifyObservers(EventType.REPAINT);
        });
    }

    public void update() {
        taskQueue.clear();
        taskQueue.offer(() -> {
            resultShapes.clear();
            for (Shape<Point3D> shape : sourceShapes) {
                resultShapes.add(new Shape<>(shape.getLines()
                        .stream()
                        .map(a -> convertLine(a, true))
                        .collect(Collectors.toList()),
                        shape.getColor(),
                        shape.getWidth()));
            }
            resultShapes.add(new Shape<>(box.getLines()
                    .stream()
                    .map(a -> convertLine(a, false))
                    .collect(Collectors.toList()),
                    box.getColor(),
                    box.getWidth()));
            notifyObservers(EventType.READY);
        });
    }

    private void setBox(Shape<Point3D> shape) {
        box = shape;
        addBaseAndMove(shape);
    }

    public void addShape(List<Point2D> spline) {
        splines.add(spline);
        Shape<Point3D> shape = new Shape<>(rotateSpline(spline), new Point3D(0,0,0));
        sourceShapes.add(shape);
        addBaseAndMove(shape);
        max = Math.max(max, shape.getLines()
                .stream()
                .flatMap(l -> Stream.of(l.getEnd(), l.getStart()))
                .flatMap(p -> Stream.of(p.getX(), p.getY(), p.getZ()))
                .max(Double::compareTo).orElse(1.));
        min = Math.min(min, shape.getLines()
                .stream()
                .flatMap(l -> Stream.of(l.getEnd(), l.getStart()))
                .flatMap(p -> Stream.of(p.getX(), p.getY(), p.getZ()))
                .min(Double::compareTo).orElse(-1.));
        scale = new Matrix(new double[][]{
                {1. / (max - min), 0, 0, -min - (max - min) / 2},
                {0, 1. / (max - min), 0, -min - (max - min) / 2},
                {0, 0, 1. / (max - min), -min - (max - min) / 2},
                {0, 0, 0, 1}
        });
    }

    private void addBaseAndMove(Shape<Point3D> shape) {
        List<Line<Point3D>> lines = shape.getLines();

        lines.add(new Line<>(new Point3D(0, 0, 0),
                new Point3D(0.5,
                        0,
                        0),
                Color.RED));
        lines.add(new Line<>(new Point3D(0, 0, 0),
                new Point3D(0,
                        0.5,
                        0),
                Color.GREEN));
        lines.add(new Line<>(new Point3D(0, 0, 0),
                new Point3D(0,
                        0,
                        0.5),
                Color.BLUE));
        lines.forEach(l -> {
            l.getStart().move(shape.getCenter());
            l.getEnd().move(shape.getCenter());
        });
    }

    private Line<Point2D> convertLine(Line<Point3D> line, boolean zoom) {
        Matrix node1 = new Matrix(new double[][]{
                {line.getStart().getX()},
                {line.getStart().getY()},
                {line.getStart().getZ()},
                {1}
        });
        Matrix node2 = new Matrix(new double[][]{
                {line.getEnd().getX()},
                {line.getEnd().getY()},
                {line.getEnd().getZ()},
                {1}
        });

        if (zoom) {
            node1 = scale.multiplyRight(node1);
            node2 = scale.multiplyRight(node2);
        }

        node1 = rotate.multiplyRight(node1);
        node2 = rotate.multiplyRight(node2);

        node1 = toCam.multiplyRight(node1);
        node2 = toCam.multiplyRight(node2);

        node2 = ortho.multiplyRight(node2);
        node1 = ortho.multiplyRight(node1);

        for (int i = 0; i < 4; i++) {
            node1.getMatrix()[i][0] /= node1.getMatrix()[3][0];
            node2.getMatrix()[i][0] /= node2.getMatrix()[3][0];
        }

        Clipper clipper = new Clipper(0, 1, -1, 1, -1, 1);
        Line<Point3D> l = clipper.getClippedLine(new Point3D(node1.getMatrix()[0][0], node1.getMatrix()[1][0], node1.getMatrix()[2][0]),
                new Point3D(node2.getMatrix()[0][0], node2.getMatrix()[1][0], node2.getMatrix()[2][0]));
        if (l != null) {
            return new Line<>(new Point2D(l.getStart().getY(), l.getStart().getZ()), new Point2D(l.getEnd().getY(), l.getEnd().getZ()), line.getColor());
        }
        return null;
    }

    public List<Shape<Point2D>> getResultShapes() {
        return resultShapes;
    }

    private List<Line<Point3D>> rotateSpline(List<Point2D> nodePoints) {
        List<Line<Point3D>> result = new ArrayList<>();
        List<Point2D> points = getSplinePoints(nodePoints);
        double len = getSplineLen(points);
        int[] param = getTargetLengthParameters(len * a, len * b, points);
        double angleDelta = (d - c) / (m * k);
        for (int v = 0; v <= m * k; v++) {
            double angle = (v * angleDelta) + c;
            double nextAngle = ((v + 1) * angleDelta) + c;
            for (int i = param[0]; i <= param[1]; i += (param[1] - param[0]) / n) {
                result.add(new Line<>(
                        new Point3D(points.get(i).getY() * cos(angle),
                                points.get(i).getY() * sin(angle),
                                points.get(i).getX()),
                        new Point3D(points.get(i).getY() * cos(nextAngle),
                                points.get(i).getY() * sin(nextAngle),
                                points.get(i).getX())));
            }

            if (v % k == 0) {
                for (int i = param[0]; i < param[1]; i++) {
                    result.add(new Line<>(
                            new Point3D(points.get(i).getY() * cos(angle),
                                    points.get(i).getY() * sin(angle),
                                    points.get(i).getX()),
                            new Point3D(points.get(i + 1).getY() * cos(angle),
                                    points.get(i + 1).getY() * sin(angle),
                                    points.get(i + 1).getX())));
                }
            }
        }
        return result;
    }

    private int[] getTargetLengthParameters(double startOffset, double endOffset, List<Point2D> points) {
        double len = 0.;
        boolean startFound = false;
        int[] result = new int[2];
        for (int i = 0; i < points.size() - 1; i++) {
            Point2D begin = points.get(i);
            Point2D end = points.get(i + 1);
            len += distance(begin.getX(), begin.getY(), end.getX(), end.getY());
            if (!startFound && len >= startOffset) {
                result[0] = i;
                startFound = true;
            }
            if (startFound && len >= endOffset) {
                result[1] = i;
                break;
            }
        }
        return result;
    }

    private List<Point2D> getSplinePoints(List<Point2D> nodePoints) {
        List<Point2D> points = new ArrayList<>();

        Matrix m = new Matrix(new double[][]{
                {-1., 3., -3., 1.},
                {3., -6., 3., 0.},
                {-3., 0., 3., 0.},
                {1., 4., 1., 0.}
        });
        m = m.multiply(1. / 6);
        for (int i = 1; i < nodePoints.size() - 2; i++) {
            Matrix pX = new Matrix(new double[][]{
                    {nodePoints.get(i - 1).getX()},
                    {nodePoints.get(i).getX()},
                    {nodePoints.get(i + 1).getX()},
                    {nodePoints.get(i + 2).getX()},
            });
            Matrix pY = new Matrix(new double[][]{
                    {nodePoints.get(i - 1).getY()},
                    {nodePoints.get(i).getY()},
                    {nodePoints.get(i + 1).getY()},
                    {nodePoints.get(i + 2).getY()},
            });
            Matrix mX = m.multiplyRight(pX);
            Matrix mY = m.multiplyRight(pY);
            for (double t = 0.; t < 1.; t += 1. / 100) {
                Matrix mT = new Matrix(new double[][]{
                        {pow(t, 3),
                                pow(t, 2),
                                t,
                                1
                        }
                });
                double x = mT.multiplyRight(mX).getMatrix()[0][0];
                double y = mT.multiplyRight(mY).getMatrix()[0][0];
                points.add(new Point2D(x, y));
            }
        }
        return points;
    }

    private double getSplineLen(List<Point2D> points) {
        double len = 0.;
        for (int i = 0; i < points.size() - 1; i++) {
            Point2D begin = points.get(i);
            Point2D end = points.get(i + 1);
            len += distance(begin.getX(), begin.getY(), end.getX(), end.getY());
        }
        return len;
    }

    public void updateShapes(){
        taskQueue.clear();
        taskQueue.add(() -> {
            sourceShapes.clear();
            splines.forEach(s -> sourceShapes.add(new Shape<>(rotateSpline(s), new Point3D(0,0,0))));
            notifyObservers(EventType.REPAINT);
        });
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public double getC() {
        return c;
    }

    public void setC(double c) {
        this.c = c;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public List<List<Point2D>> getSplines() {
        return splines;
    }

    @Override
    public void run() {
        while (true) {
            try {
                taskQueue.take().run();
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
