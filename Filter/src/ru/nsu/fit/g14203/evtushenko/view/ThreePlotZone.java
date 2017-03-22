package ru.nsu.fit.g14203.evtushenko.view;

import ru.nsu.fit.g14203.evtushenko.model.Emission;

import java.awt.*;
import java.util.Iterator;
import java.util.List;

public class ThreePlotZone extends PlotZone<Emission> {

    public ThreePlotZone(double maxX, double maxY) {
        super(maxX, maxY);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        List<Emission> points = getPoints();
        if (points != null) {
            Iterator<Emission> iter = points.iterator();
            Emission start = iter.next();
            Emission end;
            while (iter.hasNext()) {
                end = iter.next();

                int x1 = (int) ((double) start.getX() / getMaxX() * getWidth());
                int y1 = (int) ((1 - start.getRed() / getMaxY()) * getHeight());
                int x2 = (int) ((double) end.getX() / getMaxX() * getWidth());
                int y2 = (int) ((1 - end.getRed() / getMaxY()) * getHeight());
                g.setColor(Color.RED);
                g.drawLine(x1, y1, x2, y2);

                x1 = (int) ((double) start.getX() / getMaxX() * getWidth());
                y1 = (int) ((1 - start.getGreen() / getMaxY()) * getHeight());
                x2 = (int) ((double) end.getX() / getMaxX() * getWidth());
                y2 = (int) ((1 - end.getGreen() / getMaxY()) * getHeight());
                g.setColor(Color.GREEN);
                g.drawLine(x1 + 1, y1, x2 + 1, y2);

                x1 = (int) ((double) start.getX() / getMaxX() * getWidth());
                y1 = (int) ((1 - start.getBlue() / getMaxY()) * getHeight());
                x2 = (int) ((double) end.getX() / getMaxX() * getWidth());
                y2 = (int) ((1 - end.getBlue() / getMaxY()) * getHeight());
                g.setColor(Color.BLUE);
                g.drawLine(x1 + 2, y1, x2 + 2, y2);

                start = end;
            }
        }
    }
}
