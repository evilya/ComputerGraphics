package ru.nsu.fit.g14203.evtushenko.model;

import java.awt.*;
import java.util.List;

public class Shape<T> {
    private List<Line<T>> lines;
    private Color color = Color.BLACK;
    private float width = 1.f;
    private T center;

    public Shape(List<Line<T>> lines, T center, Color color, float width) {
        this.lines = lines;
        this.center = center;
        this.color = color;
        this.width = width;
    }

    public Shape(List<Line<T>> lines, Color color, float width) {
        this.lines = lines;
        this.color = color;
        this.width = width;
    }

    public Shape(List<Line<T>> lines, T center) {
        this.lines = lines;
        this.center = center;
    }

    public Shape(List<Line<T>> lines, T center, Color color) {
        this.lines = lines;
        this.center = center;
        this.color = color;
    }

    public List<Line<T>> getLines() {
        return lines;
    }

    public void setLines(List<Line<T>> lines) {
        this.lines = lines;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public T getCenter() {
        return center;
    }

    public void setCenter(T center) {
        this.center = center;
    }
    }
