package ru.nsu.fit.g14203.evtushenko.model;

public class Charge {
    double x;
    double y;
    double z;
    double q;

    public Charge(double x, double y, double z, double q) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.q = q;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getQ() {
        return q;
    }
}
