package ru.nsu.fit.g14203.evtushenko.model;

import java.awt.*;

public class ColoredLine3D {
    private final double x1, y1, z1;
    private final double x2, y2, z2;
    private final Color color;

    public ColoredLine3D(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        color = Color.BLACK;
    }

    public ColoredLine3D(double x1, double y1, double z1, double x2, double y2, double z2, Color color) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.color = color;
    }

    public double getX1() {
        return x1;
    }

    public double getY1() {
        return y1;
    }

    public double getZ1() {
        return z1;
    }

    public double getX2() {
        return x2;
    }

    public double getY2() {
        return y2;
    }

    public double getZ2() {
        return z2;
    }

    public Color getColor() {
        return color;
    }
}
