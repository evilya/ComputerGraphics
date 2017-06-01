package ru.nsu.fit.g14203.evtushenko.model.shapes;

import ru.nsu.fit.g14203.evtushenko.model.Intersection;
import ru.nsu.fit.g14203.evtushenko.model.Matrix;
import ru.nsu.fit.g14203.evtushenko.model.Point3D;
import ru.nsu.fit.g14203.evtushenko.model.World;
import ru.nsu.fit.g14203.evtushenko.model.properties.OpticalParameters;

import java.util.ArrayList;
import java.util.List;

public class Triangle implements Primitive {
    private final World world = new World();
    private final Point3D a;
    private final Point3D b;
    private final Point3D c;
    private final OpticalParameters opticalParameters;

    public Triangle(Point3D a, Point3D b, Point3D c, OpticalParameters opticalParameters) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.opticalParameters = opticalParameters;
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

    public World getWorld() {
        return world;
    }

    public List<Line> getLines() {
        List<Line> lines = new ArrayList<>();
        lines.add(new Line(a, b));
        lines.add(new Line(b, c));
        lines.add(new Line(a, c));
        return lines;
    }

    public OpticalParameters getOpticalParameters() {
        return opticalParameters;
    }

    private double area(Matrix a, Matrix b, Matrix c, int first, int second) {
        return 0.5 * ((b.get(0, first) - a.get(0, first)) * (c.get(0, second) - a.get(0, second))
                - (c.get(0, first) - a.get(0, first)) * (b.get(0, second) - a.get(0, second))
        );
    }

    @Override
    public Intersection getIntersection(Matrix from, Matrix direction) {
        Matrix a = this.a.toMatrix3();
        Matrix b = this.b.toMatrix3();
        Matrix c = this.c.toMatrix3();

        Intersection flat = getFlatIntersection(from, direction, a, b, c);
        if (flat == null) {
            return null;
        }

        int i1;
        int i2;
        Point3D normal = new Point3D(flat.getNormal());
        if (Double.compare(normal.getX(), normal.getY()) > 0) {
            if (Double.compare(normal.getX(), normal.getZ()) > 0) {
                i1 = 1;
                i2 = 2;
            } else {
                i1 = 0;
                i2 = 1;
            }
        } else if (Double.compare(normal.getY(), normal.getZ()) > 0) {
            i1 = 2;
            i2 = 0;
        } else {
            i1 = 0;
            i2 = 1;
        }

        double totalArea = area(a, b, c, i1, i2);

        double c1 = area(flat.getPosition(), b, c, i1, i2) / totalArea;
        if (Double.compare(c1, 0) < 0) {
            return null;
        }

        double c2 = area(a, flat.getPosition(), c, i1, i2) / totalArea;
        if (Double.compare(c2, 0) < 0) {
            return null;
        }

        double sum = c1 + c2;
        if (Double.compare(sum, 1.) > 0) {
            return null;
        }

        double c3 = 1. - sum;

        Matrix intersection = (a.multiply(c1)).add(b.multiply(c2)).add(c.multiply(c3));
        return new Intersection(intersection, flat.getNormal(), flat.getDistance(), this);
    }

    private Intersection getFlatIntersection(Matrix from, Matrix direction, Matrix a, Matrix b, Matrix c) {
        Matrix ba = b.subtract(a);
        Matrix ca = c.subtract(a);

        Matrix normal = new Matrix(3, 3, new double[]{
                0, -ba.get(0, 2), ba.get(0, 1),
                ba.get(0, 2), 0, -ba.get(0, 0),
                -ba.get(0, 1), ba.get(0, 0), 0
        }).multiply(ca).normalize();

        double A = normal.get(0, 0);
        double B = normal.get(0, 1);
        double C = normal.get(0, 2);
        double D = -(a.get(0, 0) * A + a.get(0, 1) * B + a.get(0, 2) * C);

        double normDir = normal.scalarProduct(direction);
        if (Double.compare(normDir, 0.) >= 0) {
            return null;
        }

        double t = -(normal.scalarProduct(from) + D) / normDir;
        if (Double.compare(t, 0.) < 0) {
            return null;
        }
        Matrix intersection = from.add(direction.multiply(t));
        Point3D distMatrix = new Point3D(intersection.subtract(from));
        double dist = Math.sqrt(distMatrix.getX() * distMatrix.getX() +
                distMatrix.getY() * distMatrix.getY() +
                distMatrix.getZ() * distMatrix.getZ());
        return new Intersection(intersection, normal, dist, null);
    }

    @Override
    public Primitive rotate(Matrix transform) {
        Point3D rotatedA = new Point3D(transform.multiply(a.toMatrix4()).subMatrix(0, 0, 1, 3));
        Point3D rotatedB = new Point3D(transform.multiply(b.toMatrix4()).subMatrix(0, 0, 1, 3));
        Point3D rotatedC = new Point3D(transform.multiply(c.toMatrix4()).subMatrix(0, 0, 1, 3));
        return new Triangle(rotatedA, rotatedB, rotatedC, opticalParameters);
    }
}
