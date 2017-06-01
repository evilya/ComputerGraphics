package ru.nsu.fit.g14203.evtushenko.model.shapes;

import ru.nsu.fit.g14203.evtushenko.model.Point3D;


public class Line implements Cloneable {
    private Point3D first;
    private Point3D second;

    public Line(Point3D first, Point3D second) {
        this.first = first;
        this.second = second;
    }

    public Point3D getFirst() {
        return first;
    }

    public Point3D getSecond() {
        return second;
    }

    public Point3D getMinPoint() {
        return new Point3D(
                Math.min(first.getX(), second.getX()),
                Math.min(first.getY(), second.getY()),
                Math.min(first.getZ(), second.getZ())
        );
    }

    public Point3D getMaxPoint() {
        return new Point3D(
                Math.max(first.getX(), second.getX()),
                Math.max(first.getY(), second.getY()),
                Math.max(first.getZ(), second.getZ())
        );
    }

    @Override
    public Line clone() throws CloneNotSupportedException {
        Line line = (Line) super.clone();
        line.first = new Point3D(first.getX(), first.getY(), first.getZ());
        line.second = new Point3D(second.getX(), second.getY(), second.getZ());
        return line;
    }
}
