package ru.nsu.fit.g14203.evtushenko.model;

import static java.lang.Math.PI;

public class ModelParameters {
    private final Model model;
    private double a = 0.0;
    private double b = 1.;
    private double c = 0.;
    private double d = 2 * PI;
    private int n = 10;
    private int m = 10;
    private int k = 1;

    public ModelParameters(Model model) {
        this.model = model;
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
        model.updateShapes();
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
        model.updateShapes();
    }

    public double getC() {
        return c;
    }

    public void setC(double c) {
        this.c = c;
        model.updateShapes();
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
        model.updateShapes();
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
        model.updateShapes();
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
        model.updateShapes();
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
        model.updateShapes();
    }
}
