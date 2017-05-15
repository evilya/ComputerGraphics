package ru.nsu.fit.g14203.evtushenko.model.geom;

import java.awt.*;

public class Line<T> {
    private final T start;
    private final T end;
    private Color color;

    public Line(T start, T end, Color color) {
        this.start = start;
        this.end = end;
        this.color = color;
    }

    public Line(T start, T end) {
        this.start = start;
        this.end = end;
    }

    public T getStart() {
        return start;
    }

    public T getEnd() {
        return end;
    }

    public Color getColor() {
        return color;
    }
}
