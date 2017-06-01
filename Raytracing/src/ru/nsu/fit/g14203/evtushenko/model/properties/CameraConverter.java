package ru.nsu.fit.g14203.evtushenko.model.properties;

import ru.nsu.fit.g14203.evtushenko.model.Matrix;
import ru.nsu.fit.g14203.evtushenko.model.Point3D;
import ru.nsu.fit.g14203.evtushenko.model.observer.ObservableBase;
import ru.nsu.fit.g14203.evtushenko.model.observer.ObserverEvent;


public class CameraConverter extends ObservableBase implements Cloneable {
    private Point3D cameraPosition;
    private Point3D viewPoint;
    private Point3D upVector;
    private Matrix worldToCamMatrix;

    CameraConverter() {
    }

    CameraConverter(Point3D cameraPosition, Point3D viewPoint, Point3D upVector) {
        this.cameraPosition = cameraPosition;
        this.viewPoint = viewPoint;
        this.upVector = upVector;
        update();
    }

    @Override
    public CameraConverter clone() throws CloneNotSupportedException {
        CameraConverter cameraConverter = (CameraConverter) super.clone();
        cameraConverter.cameraPosition = cameraPosition.clone();
        cameraConverter.viewPoint = viewPoint.clone();
        cameraConverter.upVector = upVector.clone();
        cameraConverter.worldToCamMatrix = worldToCamMatrix.clone();

        return cameraConverter;
    }

    private void update() {
        Point3D position = getCameraPosition();
        Point3D viewPoint = getViewPoint();
        Point3D up = getUpVector();

        Point3D zC = new Point3D(position.toMatrix3().subtract(viewPoint.toMatrix3()).normalize());
        Point3D xC = new Point3D(new Matrix(3, 3, new double[]{
                0, -up.getZ(), up.getY(),
                up.getZ(), 0, -up.getX(),
                -up.getY(), up.getX(), 0
        }).multiply(zC.toMatrix3()).normalize());
        Point3D yC = new Point3D(new Matrix(3, 3, new double[]{
                0, -zC.getZ(), zC.getY(),
                zC.getZ(), 0, -zC.getX(),
                -zC.getY(), zC.getX(), 0
        }).multiply(xC.toMatrix3()));

        Matrix shift = new Matrix(4, 4, new double[]{
                1, 0, 0, -position.getX(),
                0, 1, 0, -position.getY(),
                0, 0, 1, -position.getZ(),
                0, 0, 0, 1
        });

        Matrix MRotateCam = new Matrix(4, 4, new double[]{
                xC.getX(), xC.getY(), xC.getZ(), 0,
                yC.getX(), yC.getY(), yC.getZ(), 0,
                zC.getX(), zC.getY(), zC.getZ(), 0,
                0, 0, 0, 1
        });

        worldToCamMatrix = MRotateCam.multiply(shift);
    }

    private Point3D getCameraPosition() {
        return cameraPosition;
    }

    private Point3D getViewPoint() {
        return viewPoint;
    }

    private Point3D getUpVector() {
        return upVector;
    }

    public Matrix getWorldToCamMatrix() {
        return worldToCamMatrix;
    }

    public enum Event implements ObserverEvent {
        POSITION,
        VP,
        UP,
    }
}
