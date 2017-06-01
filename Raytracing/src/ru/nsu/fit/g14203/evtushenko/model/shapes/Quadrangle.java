package ru.nsu.fit.g14203.evtushenko.model.shapes;

import ru.nsu.fit.g14203.evtushenko.model.Intersection;
import ru.nsu.fit.g14203.evtushenko.model.Matrix;
import ru.nsu.fit.g14203.evtushenko.model.Point3D;
import ru.nsu.fit.g14203.evtushenko.model.World;
import ru.nsu.fit.g14203.evtushenko.model.properties.OpticalParameters;

import java.util.List;


public class Quadrangle implements Primitive {
    private final Triangle[] triangles = new Triangle[2];
    private final World world = new World();
    private final Point3D a;
    private final Point3D b;
    private final Point3D c;
    private final Point3D d;
    private final OpticalParameters opticalParameters;

    public Quadrangle(Point3D a, Point3D b, Point3D c, Point3D d, OpticalParameters opticalParameters) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.opticalParameters = opticalParameters;

        triangles[0] = new Triangle(a, b, c, opticalParameters);
        triangles[1] = new Triangle(c, d, a, opticalParameters);
    }

    public Point3D getA() {
        return a;
    }

    public Point3D getB() {
        return b;
    }

    public Point3D getC() {
        return c;
    }

    public Point3D getD() {
        return d;
    }

    public World getWorld() {
        return world;
    }

    public List<Line> getLines() {
        List<Line> lines = triangles[0].getLines();
        lines.addAll(triangles[1].getLines());
        return lines;
    }

    public OpticalParameters getOpticalParameters() {
        return opticalParameters;
    }

    @Override
    public Intersection getIntersection(Matrix from, Matrix direction) {
        Intersection intersection = triangles[0].getIntersection(from, direction);
        if (intersection != null) {
            return intersection;
        }
        return triangles[1].getIntersection(from, direction);
    }

    @Override
    public Primitive rotate(Matrix transform) {
        Point3D rotatedA = new Point3D(transform.multiply(a.toMatrix4()).subMatrix(0, 0, 1, 3));
        Point3D rotatedB = new Point3D(transform.multiply(b.toMatrix4()).subMatrix(0, 0, 1, 3));
        Point3D rotatedC = new Point3D(transform.multiply(c.toMatrix4()).subMatrix(0, 0, 1, 3));
        Point3D rotatedD = new Point3D(transform.multiply(d.toMatrix4()).subMatrix(0, 0, 1, 3));

        return new Quadrangle(rotatedA, rotatedB, rotatedC, rotatedD, opticalParameters);
    }
}
