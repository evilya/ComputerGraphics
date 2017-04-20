package ru.nsu.fit.g14203.evtushenko.model;

import ru.nsu.fit.g14203.evtushenko.EventType;
import ru.nsu.fit.g14203.evtushenko.Observable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import static java.awt.geom.Point2D.distance;
import static java.lang.Math.*;


public class Model extends Observable implements Runnable {

    private BlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<>(1);

    private List<Shape<Point2D>> resultShapes = new ArrayList<>();
    private List<Shape<Point3D>> sourceShapes = new ArrayList<>();

    private Matrix rotate = new Matrix(new double[][]{
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
    private Matrix mPersp = new Matrix(new double[][]{
            {1, 0, 0, 0},
            {0, 1, 0, 0},
            {0, 0, 1, 0},
            {1. / 10, 0, 0, 0}
    });

    private double sW = 1.;
    private double sH = 1.;
    private double zB = 0.1;
    private double zF = 1.;
    private Matrix ortho = new Matrix(new double[][]{
            {-1. / (zB - zF), 0, 0, -zF / (zB - zF)},
            {0, 2. / sW, 0, 0},
            {0, 0, 2. / sH, 0},
            {0, 0, 0, 1}
    });


    public Model() {
        sourceShapes.add(new Shape<>(new ArrayList<>(Arrays.asList(
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
                new Point3D(0, 0, 0), Color.BLACK, 2.f));
        sourceShapes.add(new Shape<>(rotateSpline(new ArrayList<>(Arrays.asList(
                new Point2D(0. / 6, 0. / 6),
                new Point2D(1. / 6, 1. / 6),
                new Point2D(2. / 6, 2. / 6),
                new Point2D(3. / 6, 3. / 6),
                new Point2D(4. / 6, 4. / 6),
                new Point2D(5. / 6, 3. / 6),
                new Point2D(4. / 6, 2. / 6),
                new Point2D(3. / 6, 3. / 6),
                new Point2D(2. / 6, 4. / 6),
                new Point2D(1. / 6, 5. / 6),
                new Point2D(0. / 6, 6. / 6)))),
                new Point3D(0, 0, 0), Color.ORANGE));
        sourceShapes.forEach(this::moveShape);
    }

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

    public void zoom(double zoom) {
        taskQueue.clear();
        taskQueue.add(() -> {
            zF = zoom;
            ortho = new Matrix(new double[][]{
                    {-1. / (zB - zF), 0, 0, -zF / (zB - zF)},
                    {0, 2. / sW, 0, 0},
                    {0, 0, 2. / sH, 0},
                    {0, 0, 0, 1}
            });
            notifyObservers(EventType.REPAINT);
        });
    }

    public void update() {
        taskQueue.clear();
        taskQueue.add(() -> {
            resultShapes.clear();
            for (Shape<Point3D> shape : sourceShapes) {
                resultShapes.add(new Shape<>(shape.getLines()
                        .stream()
                        .map(this::convertLine)
                        .collect(Collectors.toList()),
                        shape.getColor(),
                        shape.getWidth()));
            }
            notifyObservers(EventType.READY);
        });
    }

    private void moveShape(Shape<Point3D> shape) {
        Point3D center = shape.getCenter();
        List<Line<Point3D>> lines = shape.getLines();
        lines.forEach(l -> {
            l.getStart().move(shape.getCenter());
            l.getEnd().move(shape.getCenter());
        });
        lines.add(new Line<>(center,
                new Point3D(center.getX() + 0.5,
                        center.getY(),
                        center.getZ()),
                Color.RED));
        lines.add(new Line<>(center,
                new Point3D(center.getX(),
                        center.getY() + 0.5,
                        center.getZ()),
                Color.GREEN));
        lines.add(new Line<>(center,
                new Point3D(center.getX(),
                        center.getY(),
                        center.getZ() + 0.5),
                Color.BLUE));
    }

    private Line<Point2D> convertLine(Line<Point3D> line) {
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

        node1 = rotate.multiplyRight(node1);
        node2 = rotate.multiplyRight(node2);



        node1 = toCam.multiplyRight(node1);
        node2 = toCam.multiplyRight(node2);

        node1 = ortho.multiplyRight(node1);
        node2 = ortho.multiplyRight(node2);
        for (int i = 0; i < 4; i++) {
            node1.getMatrix()[i][0] /= node1.getMatrix()[3][0];
            node2.getMatrix()[i][0] /= node2.getMatrix()[3][0];
        }

        node1 = mPersp.multiplyRight(node1);
        node2 = mPersp.multiplyRight(node2);

        double x1 = (node1.getMatrix()[1][0] / node1.getMatrix()[3][0] + 5) / 10;
        double y1 = (node1.getMatrix()[2][0] / node1.getMatrix()[3][0] + 5) / 10;
        double x2 = (node2.getMatrix()[1][0] / node2.getMatrix()[3][0] + 5) / 10;
        double y2 = (node2.getMatrix()[2][0] / node2.getMatrix()[3][0] + 5) / 10;
        return new Line<>(new Point2D(x1, y1), new Point2D(x2, y2), line.getColor());
    }

    public List<Shape<Point2D>> getResultShapes() {
        return resultShapes;
    }

    private List<Line<Point3D>> rotateSpline(List<Point2D> nodePoints) {
        List<Line<Point3D>> result = new ArrayList<>();
        double a = 0.2;
        double b = 0.6;
        double c = PI / 2;
        double d = 2 * PI;
        int n = 10;
        int m = 10;
        int k = 100;
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
