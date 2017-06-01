package ru.nsu.fit.g14203.evtushenko.model;

import ru.nsu.fit.g14203.evtushenko.model.shapes.Primitive;


public class Intersection {
    private final Matrix position;
    private final Matrix normal;
    private final double distance;
    private final Primitive primitive;

    public Intersection(Matrix position, Matrix normal, double distance, Primitive primitive) {
        this.position = position;
        this.normal = normal;
        this.distance = distance;
        this.primitive = primitive;
    }

    public Matrix getPosition() {
        return position;
    }

    public Matrix getNormal() {
        return normal;
    }

    public double getDistance() {
        return distance;
    }

    public Primitive getPrimitive() {
        return primitive;
    }

}
