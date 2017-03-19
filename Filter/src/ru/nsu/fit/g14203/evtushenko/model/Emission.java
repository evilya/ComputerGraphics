package ru.nsu.fit.g14203.evtushenko.model;

public  class Emission {
    Emission(int x, int red, int green, int blue) {
        this.x = x;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    int x;
    int red;
    int green;
    int blue;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }
}