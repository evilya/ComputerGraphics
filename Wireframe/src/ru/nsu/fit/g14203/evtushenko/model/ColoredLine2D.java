package ru.nsu.fit.g14203.evtushenko.model;

import java.awt.*;
import java.awt.geom.Line2D;

public class ColoredLine2D {
    private Line2D.Double line;
    private Color color;

    public ColoredLine2D(double x1, double y1, double x2, double y2, Color color) {
        this.line = new Line2D.Double(x1, y1, x2, y2);
        this.color = color;
    }

    public double getX1() {
        return line.getX1();
    }

    public double getY1() {
        return line.getY1();
    }

    public Color getColor() {
        return color;
    }

    public double getX2() {
        return line.getX2();
    }

    public double getY2() {
        return line.getY2();
    }
}
