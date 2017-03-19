package ru.nsu.fit.g14203.evtushenko.model;

public class Absorption {

    public Absorption(int x, double value) {
        this.x = x;
        this.value = value;
    }

    int x;
    double value;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
