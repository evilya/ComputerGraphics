package ru.nsu.fit.g14203.evtushenko.utils;

import ru.nsu.fit.g14203.evtushenko.model.Line;
import ru.nsu.fit.g14203.evtushenko.model.Point3D;

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

    private BitSet getOutCode(Point3D point3D) {
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

    public Line<Point3D> getClippedLine(Point3D startPoint, Point3D endPoint) {
        double x0 = startPoint.getX();
        double x1 = endPoint.getX();
        double y0 = startPoint.getY();
        double y1 = endPoint.getY();
        double z0 = startPoint.getZ();
        double z1 = endPoint.getZ();

        BitSet startOutCode = getOutCode(startPoint);
        BitSet endOutCode = getOutCode(endPoint);

        boolean accept = false;

        while (true) {
            if (startOutCode.isEmpty() && endOutCode.isEmpty()) {
                accept = true;
                break;
            } else {
                if (startOutCode.intersects(endOutCode)) {
                    break;
                } else {
                    double x = 0;
                    double y = 0;
                    double z = 0;

                    BitSet outCode = !startOutCode.isEmpty() ? startOutCode : endOutCode;

                    if (outCode.get(3)) {
                        x = x0 + (x1 - x0) * (yMax - y0) /
                                (y1 - y0);
                        y = yMax;
                        z = z0 + (z1 - z0) * (yMax - y0) /
                                (y1 - y0);
                    } else if (outCode.get(2)) {
                        x = x0 + (x1 - x0) * (yMin - y0) /
                                (y1 - y0);
                        y = yMin;
                        z = z0 + (z1 - z0) * (yMin - y0) /
                                (y1 - y0);
                    } else if (outCode.get(1)) {
                        y = y0 + (y1 - y0) * (xMax - x0) /
                                (x1 - x0);
                        x = xMax;
                        z = z0 + (z1 - z0) * (xMax - x0) /
                                (x1 - x0);

                    } else if (outCode.get(0)) {
                        y = y0 + (y1 - y0) * (xMin - x0) /
                                (x1 - x0);
                        x = xMin;
                        z = z0 + (z1 - z0) * (xMin - x0) /
                                (x1 - x0);

                    } else if (outCode.get(5)) {
                        x = x0 + (x1 - x0) * (zMax - z0) /
                                (z1 - z0);

                        y = y0 + (y1 - y0) * (zMax - z0) /
                                (z1 - z0);
                        z = zMax;
                    } else if (outCode.get(4)) {
                        x = x0 + (x1 - x0) * (zMin - z0) /
                                (z1 - z0);

                        y = y0 + (y1 - y0) * (zMin - z0) /
                                (z1 - z0);
                        z = zMin;
                    }

                    if (outCode == startOutCode) {
                        x0 = x;
                        y0 = y;
                        z0 = z;
                        startOutCode = getOutCode(new Point3D(x0, y0, z0));
                    } else {
                        x1 = x;
                        y1 = y;
                        z1 = z;
                        endOutCode = getOutCode(new Point3D(x1, y1, z1));
                    }
                }
            }
        }

        if (accept) {
            return new Line<>(new Point3D(x0, y0, z0), new Point3D(x1, y1, z1));
        } else {
            return null;
        }
    }
}
