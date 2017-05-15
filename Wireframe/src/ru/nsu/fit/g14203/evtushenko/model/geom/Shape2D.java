package ru.nsu.fit.g14203.evtushenko.model.geom;

import java.awt.*;
import java.util.List;

public class Shape2D {
    private List<Line<Point2D>> lines;
    private Color color = Color.BLACK;
    private float width = 1.f;

    public Shape2D(List<Line<Point2D>> lines, Color color, float width) {
        this.lines = lines;
        this.color = color;
        this.width = width;
    }

    public List<Line<Point2D>> getLines() {
        return lines;
    }

    public Color getColor() {
        return color;
    }

    public float getWidth() {
        return width;
    }
}
