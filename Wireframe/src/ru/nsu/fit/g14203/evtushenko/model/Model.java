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

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Model extends Observable implements Runnable {

    private BlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<>(1);

    private List<ColoredLine2D> resultLines = new ArrayList<>();
    private List<ColoredLine3D> sourceLines = new ArrayList<>();

    private Matrix xRotate = new Matrix(new double[][]{
            {1, 0, 0, 0},
            {0, 1, 0, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
    });
    private Matrix yRotate = new Matrix(new double[][]{
            {1, 0, 0, 0},
            {0, 1, 0, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
    });
    private Matrix rotate =  new Matrix(new double[][]{
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

    public Model() {
        sourceLines.addAll(Arrays.asList(
                new ColoredLine3D(0, 0, 0, 0.5, 0, 0, Color.RED),
                new ColoredLine3D(0, 0, 0, 0, 0.5, 0, Color.GREEN),
                new ColoredLine3D(0, 0, 0, 0, 0, 0.5, Color.BLUE),
                new ColoredLine3D(-1, -1, -1, -1, -1, 1),
                new ColoredLine3D(-1, -1, -1, -1, 1, -1),
                new ColoredLine3D(-1, -1, -1, 1, -1, -1),
                new ColoredLine3D(1, 1, 1, -1, 1, 1),
                new ColoredLine3D(1, 1, 1, 1, -1, 1),
                new ColoredLine3D(1, 1, 1, 1, 1, -1),
                new ColoredLine3D(1, 1, -1, 1, -1, -1),
                new ColoredLine3D(1, 1, -1, -1, 1, -1),
                new ColoredLine3D(1, -1, 1, -1, -1, 1),
                new ColoredLine3D(1, -1, 1, 1, -1, -1),
                new ColoredLine3D(-1, 1, 1, -1, -1, 1),
                new ColoredLine3D(-1, 1, 1, -1, 1, -1)
        ));
    }

    public void rotate(double angleX, double angleY) {
        taskQueue.clear();
        taskQueue.add(() -> {
            double sc = 1.;
            xRotate = new Matrix(new double[][]{
                    {cos(angleX/sc), -sin(angleX/sc), 0, 0},
                    {sin(angleX/sc), cos(angleX/sc), 0, 0},
                    {0, 0, 1, 0},
                    {0, 0, 0, 1}
            });
            yRotate = new Matrix(new double[][]{
                    {cos(angleY/sc), 0, sin(angleY/sc), 0},
                    {0, 1, 0, 0},
                    {-sin(angleY/sc), 0, cos(angleY/sc), 0},
                    {0, 0, 0, 1},
            });
            rotate = yRotate.multiplyRight(xRotate).multiplyRight(rotate);
            notifyObservers(EventType.REPAINT);
        });
    }

    public void zoom(double zoom) {
        taskQueue.clear();
        taskQueue.add(() -> {
            mPersp = new Matrix(new double[][]{
                    {1, 0, 0, 0},
                    {0, 1, 0, 0},
                    {0, 0, 1, 0},
                    {1. / zoom, 0, 0, 0}
            });
            notifyObservers(EventType.REPAINT);
        });
    }

    public void update() {
        taskQueue.clear();
        taskQueue.add(() -> {
            resultLines = sourceLines.stream().map(line -> {
                Matrix node1 = new Matrix(new double[][]{
                        {line.getX1()},
                        {line.getY1()},
                        {line.getZ1()},
                        {1}
                });
                Matrix node2 = new Matrix(new double[][]{
                        {line.getX2()},
                        {line.getY2()},
                        {line.getZ2()},
                        {1}
                });

//                node1 = xRotate.multiplyRight(node1);
//                node2 = xRotate.multiplyRight(node2);
//                node1 = yRotate.multiplyRight(node1);
//                node2 = yRotate.multiplyRight(node2);
                
                node1 = rotate.multiplyRight(node1);
                node2 = rotate.multiplyRight(node2);

                node1 = toCam.multiplyRight(node1);
                node2 = toCam.multiplyRight(node2);
                node1 = mPersp.multiplyRight(node1);
                node2 = mPersp.multiplyRight(node2);

                double x1 = (node1.getMatrix()[1][0] / node1.getMatrix()[3][0] + 5) / 10;
                double y1 = (node1.getMatrix()[2][0] / node1.getMatrix()[3][0] + 5) / 10;
                double x2 = (node2.getMatrix()[1][0] / node2.getMatrix()[3][0] + 5) / 10;
                double y2 = (node2.getMatrix()[2][0] / node2.getMatrix()[3][0] + 5) / 10;
                return new ColoredLine2D(x1, y1, x2, y2, line.getColor());
            }).collect(Collectors.toList());
            notifyObservers(EventType.READY);
        });
    }

    public List<ColoredLine2D> getResultLines() {
        return resultLines;
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
