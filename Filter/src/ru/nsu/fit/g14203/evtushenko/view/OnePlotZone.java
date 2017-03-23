package ru.nsu.fit.g14203.evtushenko.view;

import ru.nsu.fit.g14203.evtushenko.model.Absorption;

import java.awt.*;
import java.util.Iterator;
import java.util.List;

public class OnePlotZone extends PlotZone<Absorption> {

    public OnePlotZone(double maxX, double maxY) {
        super(maxX, maxY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        List<Absorption> points = getPoints();
        if (points != null) {
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

}
