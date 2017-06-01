package ru.nsu.fit.g14203.evtushenko.model;

import ru.nsu.fit.g14203.evtushenko.model.observer.Observable;
import ru.nsu.fit.g14203.evtushenko.model.observer.ObservableBase;
import ru.nsu.fit.g14203.evtushenko.model.observer.ObserverEvent;


public class World extends ObservableBase implements Observable, Cloneable {
    private Point3D center;
    private double xAngle;
    private double yAngle;
    private double zAngle;
    private Matrix transform;
    private Matrix rotate;

    public World() {
        this(new Point3D(0, 0, 0), 0, 0, 0);
    }

    private World(Point3D center, double xAngle, double yAngle, double zAngle) {
        this.center = center;
        this.xAngle = xAngle;
        this.yAngle = yAngle;
        this.zAngle = zAngle;
        update();
    }

    @Override
    public World clone() throws CloneNotSupportedException {
        World world = (World) super.clone();
        world.setCenter(center.clone());
        world.setxAngle(xAngle);
        world.setyAngle(yAngle);
        world.setzAngle(zAngle);
        return world;
    }

    private void setCenter(Point3D center) {
        this.center = center;
        update();
        notifyObservers(Event.CENTER);
    }

    private void setxAngle(double xAngle) {
        this.xAngle = xAngle + 0.;
        update();
        notifyObservers(Event.ROTATION);
    }

    private void setyAngle(double yAngle) {
        this.yAngle = yAngle + 0.;
        update();
        notifyObservers(Event.ROTATION);
    }

    private void setzAngle(double zAngle) {
        this.zAngle = zAngle + 0.;
        update();
        notifyObservers(Event.ROTATION);
    }

    private void update() {
        Matrix offset = new Matrix(4, 4, new double[]{
                1, 0, 0, center.getX(),
                0, 1, 0, center.getY(),
                0, 0, 1, center.getZ(),
                0, 0, 0, 1
        });

        double xCos = Math.cos(xAngle);
        double xSin = Math.sin(xAngle);
        Matrix xRotate = new Matrix(4, 4, new double[]{
                1, 0, 0, 0,
                0, xCos, -xSin, 0,
                0, xSin, xCos, 0,
                0, 0, 0, 1
        });

        double yCos = Math.cos(yAngle);
        double ySin = Math.sin(yAngle);
        Matrix yRotate = new Matrix(4, 4, new double[]{
                yCos, 0, ySin, 0,
                0, 1, 0, 0,
                -ySin, 0, yCos, 0,
                0, 0, 0, 1
        });

        double zCos = Math.cos(zAngle);
        double zSin = Math.sin(zAngle);
        Matrix zRotate = new Matrix(4, 4, new double[]{
                zCos, -zSin, 0, 0,
                zSin, zCos, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        });

        rotate = zRotate.multiply(yRotate.multiply(xRotate));
        transform = offset.multiply(rotate);
    }

    public Matrix getRotate() {
        return rotate;
    }

    public void setRotate(Matrix rotate) {
        this.rotate = rotate;
        updateCorners();
        Matrix offset = new Matrix(4, 4, new double[]{
                1, 0, 0, center.getX(),
                0, 1, 0, center.getY(),
                0, 0, 1, center.getZ(),
                0, 0, 0, 1
        });
        transform = offset.multiply(rotate);

        notifyObservers(Event.ROTATION);
    }

    public Matrix getTransform() {
        return transform;
    }

    private void updateCorners() {
        if (rotate == null) {
            xAngle = 0;
            yAngle = 0;
            zAngle = 0;
            return;
        }

        xAngle = Math.atan2(rotate.get(1, 2), rotate.get(2, 2));
        yAngle = Math.atan2(-rotate.get(0, 2),
                Math.sqrt(
                        rotate.get(1, 2) * rotate.get(1, 2)
                                + rotate.get(2, 2) * rotate.get(2, 2)));
        zAngle = Math.atan2(rotate.get(0, 1), rotate.get(0, 0));
    }

    public enum Event implements ObserverEvent {
        CENTER,
        ROTATION,
    }
}
