package ru.nsu.fit.g14203.evtushenko.model;

import ru.nsu.fit.g14203.evtushenko.EventType;
import ru.nsu.fit.g14203.evtushenko.math.Matrix;
import ru.nsu.fit.g14203.evtushenko.model.geom.*;
import ru.nsu.fit.g14203.evtushenko.model.geom.Point2D;
import ru.nsu.fit.g14203.evtushenko.utils.PointMatrixConverter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Model extends AbstractModel {
    private int chosenShapeIndex = -1;
    private boolean rotateShape = false;

    private Shape3D box;
    private List<Shape3D> sourceShapes = new ArrayList<>();
    private List<Shape2D> resultShapes = new ArrayList<>();

    private ModelParameters parameters = new ModelParameters(this);
    private SceneRotator rotator = new SceneRotator();
    private Scaler scaler = new Scaler(this);
    private PovConverter povConverter = new PovConverter(this);
    private CameraConverter cameraConverter = new CameraConverter();


    public Model() {
        setBox(new Shape3D(new ArrayList<>(Arrays.asList(
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
                Color.BLACK,
                2.f));
        addShape(new ArrayList<>(Arrays.asList(
                new Point2D(0, 1. / 10),
                new Point2D(2. / 10, 1. / 10),
                new Point2D(3. / 10, 2. / 10),
                new Point2D(7. / 10, 5. / 10),
                new Point2D(4. / 10, 3. / 10),
                new Point2D(3. / 10, 6. / 10),
                new Point2D(1. / 10, 2. / 10),
                new Point2D(9. / 10, 1. / 10)
        )));
        addShape(new ArrayList<>(Arrays.asList(
                new Point2D(1, 1),
                new Point2D(2, 1),
                new Point2D(3, 2),
                new Point2D(5, 5),
                new Point2D(4, 3),
                new Point2D(3, 2),
                new Point2D(1, 2),
                new Point2D(1, 1)
        )));
    }

    public void moveSelectedShape(Axis axis, double newValue){
        Point3D position = sourceShapes.get(chosenShapeIndex).getPosition();
        switch (axis){
            case X:
                position.setX(newValue);
                break;
            case Y:
                position.setY(newValue);
                break;
            case Z:
                position.setZ(newValue);
                break;
        }
        scaler.update();
        update();
    }


    public void rotate(double angleX, double angleY) {
        runInBackground(() -> {
            if (rotateShape) {
                if (chosenShapeIndex != -1) {
                    Shape3D shape = sourceShapes.get(chosenShapeIndex);
                    shape.rotate(angleX, angleY);
                    scaler.update();
                }
            } else {
                rotator.rotate(angleX, angleY);
            }
            notifyObservers(EventType.REPAINT);
        });
    }

    public void zoom(int z) {
        runInBackground(() -> {
            povConverter.setzF(Math.exp(z * 0.1));
            notifyObservers(EventType.REPAINT);
        });
    }

    public void update() {
        runInBackground(() -> {
            resultShapes.clear();
            for (Shape3D shape : sourceShapes) {
                resultShapes.add(new Shape2D(shape.getLines()
                        .stream()
                        .map(a -> convertLine(a, true))
                        .collect(Collectors.toList()),
                        shape.getColor(),
                        shape.getWidth()));
            }
            resultShapes.add(new Shape2D(box.getLines()
                    .stream()
                    .map(a -> convertLine(a, false))
                    .collect(Collectors.toList()),
                    box.getColor(),
                    box.getWidth()));
            notifyObservers(EventType.READY);
        });
    }

    private void setBox(Shape3D shape) {
        box = shape;
    }

    public void addShape(List<Point2D> points) {
        Shape3D shape = new Shape3D(points, this);
        sourceShapes.add(shape);
        scaler.update();
    }

    public void deleteShape(int index) {
        sourceShapes.remove(index);
        scaler.update();
    }



    private Line<Point2D> convertLine(Line<Point3D> line, boolean zoom) {
        Matrix start = PointMatrixConverter.pointToMatrix(line.getStart());
        Matrix end = PointMatrixConverter.pointToMatrix(line.getEnd());

        if (zoom) {
            start = scaler.getMatrix().multiply(start);
            end = scaler.getMatrix().multiply(end);
        }

        start = rotator.getMatrix().multiply(start);
        end = rotator.getMatrix().multiply(end);

        start = cameraConverter.getMatrix().multiply(start);
        end = cameraConverter.getMatrix().multiply(end);

        end = povConverter.getMatrix().multiply(end);
        start = povConverter.getMatrix().multiply(start);

        for (int i = 0; i < 4; i++) {
            start.getMatrix()[i][0] /= start.getMatrix()[3][0];
            end.getMatrix()[i][0] /= end.getMatrix()[3][0];
        }

        Clipper clipper = new Clipper(0, 1,
                -1, 1,
                -1, 1);
        Line<Point3D> l = clipper.getClippedLine(
                PointMatrixConverter.matrixToPoint(start),
                PointMatrixConverter.matrixToPoint(end));
        if (l != null) {
            return new Line<>(new Point2D(l.getStart().getY(), l.getStart().getZ()),
                    new Point2D(l.getEnd().getY(), l.getEnd().getZ()),
                    line.getColor());
        }
        return null;
    }

    public List<Shape2D> getResultShapes() {
        return resultShapes;
    }

    public void updateShapes() {
        runInBackground(() -> {
            sourceShapes.forEach(Shape3D::update);
            scaler.update();
            notifyObservers(EventType.REPAINT);
        });
    }

    public ModelParameters getParameters() {
        return parameters;
    }

    public PovConverter getPovConverter() {
        return povConverter;
    }

    public List<List<Point2D>> getSplines() {
        return sourceShapes.stream()
                .map(Shape3D::getNodePoints)
                .collect(Collectors.toList());
    }

    public void setRotateShape(boolean rotateShape) {
        this.rotateShape = rotateShape;
    }

    public List<Shape3D> getSourceShapes() {
        return sourceShapes;
    }

    public int getChosenShapeIndex() {
        return chosenShapeIndex;
    }

    public void setChosenShapeIndex(int chosenShapeIndex) {
        this.chosenShapeIndex = chosenShapeIndex;
    }

    public Point3D getChosenShapePosition() {
        return sourceShapes.get(chosenShapeIndex).getPosition();
    }

    public Shape3D getSelectedShape(){
        if (chosenShapeIndex >= 0) {
            return sourceShapes.get(chosenShapeIndex);
        }
        return null;
    }

}
