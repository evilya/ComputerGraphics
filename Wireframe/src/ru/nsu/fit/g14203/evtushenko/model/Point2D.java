package ru.nsu.fit.g14203.evtushenko.model;

public class Point2D {
    private double x, y;

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void move(Point2D offset){
        x += offset.getX();
        y += offset.getY();
    }
}
