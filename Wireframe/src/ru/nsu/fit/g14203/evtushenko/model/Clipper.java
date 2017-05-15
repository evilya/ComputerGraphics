package ru.nsu.fit.g14203.evtushenko.model;

import ru.nsu.fit.g14203.evtushenko.model.geom.Line;
import ru.nsu.fit.g14203.evtushenko.model.geom.Point3D;

import java.util.BitSet;

public class Clipper {
    private double xMin;
    private double xMax;
    private double yMin;
    private double yMax;
    private double zMin;
    private double zMax;

    public Clipper(double xMin, double xMax, double yMin, double yMax, double zMin, double zMax) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.zMin = zMin;
        this.zMax = zMax;
    }

    private BitSet getBits(Point3D point3D) {
        BitSet a = new BitSet(6);
        if (point3D.getX() < xMin) {
            a.set(0);
        } else if (point3D.getX() > xMax) {
            a.set(1);
        }

        if (point3D.getY() < yMin) {
            a.set(2);
        } else if (point3D.getY() > yMax) {
            a.set(3);
        }

        if (point3D.getZ() < zMin) {
            a.set(4);
        } else if (point3D.getZ() > zMax) {
            a.set(5);
        }
        return a;
    }

    public Line<Point3D> getClippedLine(Point3D start, Point3D end) {
        double x0 = start.getX();
        double x1 = end.getX();
        double y0 = start.getY();
        double y1 = end.getY();
        double z0 = start.getZ();
        double z1 = end.getZ();

        BitSet startBitSet = getBits(start);
        BitSet endBitSet = getBits(end);

        boolean inBox = false;

        while (true) {
            if (startBitSet.isEmpty() && endBitSet.isEmpty()) {
                inBox = true;
                break;
            } else {
                if (startBitSet.intersects(endBitSet)) {
                    break;
                } else {
                    double x = 0;
                    double y = 0;
                    double z = 0;

                    BitSet bitset = !startBitSet.isEmpty() ? startBitSet : endBitSet;

                    if (bitset.get(3)) {
                        x = x0 + (x1 - x0) * (yMax - y0) /
                                (y1 - y0);
                        y = yMax;
                        z = z0 + (z1 - z0) * (yMax - y0) /
                                (y1 - y0);
                    } else if (bitset.get(2)) {
                        x = x0 + (x1 - x0) * (yMin - y0) /
                                (y1 - y0);
                        y = yMin;
                        z = z0 + (z1 - z0) * (yMin - y0) /
                                (y1 - y0);
                    } else if (bitset.get(1)) {
                        y = y0 + (y1 - y0) * (xMax - x0) /
                                (x1 - x0);
                        x = xMax;
                        z = z0 + (z1 - z0) * (xMax - x0) /
                                (x1 - x0);

                    } else if (bitset.get(0)) {
                        y = y0 + (y1 - y0) * (xMin - x0) /
                                (x1 - x0);
                        x = xMin;
                        z = z0 + (z1 - z0) * (xMin - x0) /
                                (x1 - x0);

                    } else if (bitset.get(5)) {
                        x = x0 + (x1 - x0) * (zMax - z0) /
                                (z1 - z0);

                        y = y0 + (y1 - y0) * (zMax - z0) /
                                (z1 - z0);
                        z = zMax;
                    } else if (bitset.get(4)) {
                        x = x0 + (x1 - x0) * (zMin - z0) /
                                (z1 - z0);

                        y = y0 + (y1 - y0) * (zMin - z0) /
                                (z1 - z0);
                        z = zMin;
                    }

                    if (bitset.equals(startBitSet)) {
                        x0 = x;
                        y0 = y;
                        z0 = z;
                        startBitSet = getBits(new Point3D(x0, y0, z0));
                    } else {
                        x1 = x;
                        y1 = y;
                        z1 = z;
                        endBitSet = getBits(new Point3D(x1, y1, z1));
                    }
                }
            }
        }

        if (inBox) {
            return new Line<>(new Point3D(x0, y0, z0),
                    new Point3D(x1, y1, z1));
        } else {
            return null;
        }
    }
}
