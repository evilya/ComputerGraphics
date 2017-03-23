package ru.nsu.fit.g14203.evtushenko.view;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public abstract class PlotZone<T> extends JPanel {
    private List<T> points;
    private double maxX;
    private double maxY;

    private int height = 150;
    private int width = 350;

    public PlotZone(double maxX, double maxY) {
        this.maxX = maxX;
        this.maxY = maxY;
        setBorder(BorderFactory.createMatteBorder(0, 1, 1, 0, Color.BLACK));
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    public List<T> getPoints() {
        return points;
    }

    public void setPoints(List<T> points) {
        this.points = points;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    ;

    public double getMaxX() {
        return maxX;
    }

    public double getMaxY() {
        return maxY;
    }
}
