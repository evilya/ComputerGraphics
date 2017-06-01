package ru.nsu.fit.g14203.evtushenko.model.shapes;

import ru.nsu.fit.g14203.evtushenko.model.Matrix;
import ru.nsu.fit.g14203.evtushenko.model.Point3D;

import java.awt.*;

public class LightSource {
    private final Point3D position;
    private final Color color;

    public LightSource(Point3D position, Color color) {
        this.position = position;
        this.color = color;
    }

    public Point3D getPosition() {
        return position;
    }

    public Color getColor() {
        return color;
    }

    public LightSource rotate(Matrix transform) {
        Point3D rotatedPosition = new Point3D(transform.multiply(position.toMatrix4()).subMatrix(0, 0, 1, 3));
        return new LightSource(rotatedPosition, color);
    }
}
