package ru.nsu.fit.g14203.evtushenko.view;

import ru.nsu.fit.g14203.evtushenko.model.Absorption;

import java.awt.*;
import java.util.Iterator;
import java.util.List;

public class OnePlotZone extends PlotZone<Absorption> {

    private List<Absorption> points;

    public OnePlotZone(double minX, double maxX, double minY, double maxY) {
        super(minX, maxX, minY, maxY);
    }

    @Override
    public void setPoints(List<Absorption> points) {
        this.points = points;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Iterator<Absorption> iter = points.iterator();
        Absorption start = iter.next();
        Absorption end;
        while (iter.hasNext()) {
            end = iter.next();
            int x1 = (int) ((double) start.getX() / getMaxX() * getWidth());
            int y1 = (int) ((1 - (start.getValue() / getMaxY())) * getHeight());
            int x2 = (int) ((double) end.getX() / getMaxX() * getWidth());
            int y2 = (int) ((1 - (end.getValue() / getMaxY())) * getHeight());
            g.drawLine(x1, y1, x2, y2);
            start = end;
        }
    }

}
