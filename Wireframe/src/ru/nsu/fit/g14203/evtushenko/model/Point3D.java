package ru.nsu.fit.g14203.evtushenko.model;

public class Point3D {
    private double x, y, z;

    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void move(Point3D offset){
        x += offset.getX();
        y += offset.getY();
        z += offset.getZ();
    }
}
