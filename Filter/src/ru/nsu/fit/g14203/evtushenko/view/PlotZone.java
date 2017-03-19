package ru.nsu.fit.g14203.evtushenko.view;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public abstract class PlotZone<T> extends JPanel {
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;

    private int height;
    private int width;

    public PlotZone(double minX, double maxX, double minY, double maxY) {
        this.maxX = maxX;
        this.minX = minX;
        this.maxY = maxY;
        this.minY = minY;
        setBorder(BorderFactory.createMatteBorder(0, 1, 1, 0, Color.BLACK));
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(350, 150);
    }

    public abstract void setPoints(List<T> points);

    @Override
    protected abstract void paintComponent(Graphics g);

    public double getMinX() {
        return minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxY() {
        return maxY;
    }
}
