package ru.nsu.fit.g14203.evtushenko.view;

import ru.nsu.fit.g14203.evtushenko.EventType;
import ru.nsu.fit.g14203.evtushenko.Observer;
import ru.nsu.fit.g14203.evtushenko.model.ColoredLine2D;
import ru.nsu.fit.g14203.evtushenko.model.Matrix;
import ru.nsu.fit.g14203.evtushenko.model.Model;
import ru.nsu.fit.g14203.evtushenko.utils.MathUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;

import static java.lang.Math.pow;

public class View extends JPanel implements Observer {
    private Model model;
    private List<ColoredLine2D> lines;

    public View(Model model) {
        this.model = model;
        MouseCoordinates c = new MouseCoordinates();
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                c.deltaX = c.startX - e.getX();
                c.deltaY = c.startY - e.getY();
                double angleX = 2. * Math.PI * c.deltaX / getWidth();
                double angleY = 2. * Math.PI * c.deltaY / getHeight();
                model.rotate(angleX, angleY);
                c.startX = e.getX();
                c.startY = e.getY();
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                c.startX = e.getX();
                c.startY = e.getY();
            }
        });
        addMouseWheelListener(new MouseAdapter() {
            int sum = 20;
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                sum = MathUtils.constraint(sum + e.getWheelRotation(), 1, 100);
                model.zoom(sum);
                System.out.println(sum);
            }
        });
        model.update();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
//        List<Point2D> points = new ArrayList<>();
//        drawSpline(g, points);
        for (ColoredLine2D line : lines) {
            int x1 = (int) (500 * line.getX1() + 0.5);
            int y1 = (int) (500 * line.getY1() + 0.5);
            int x2 = (int) (500 * line.getX2() + 0.5);
            int y2 = (int) (500 * line.getY2() + 0.5);
            g.setColor(line.getColor());
            g.drawLine(x1, y1, x2, y2);
        }
    }


    private void drawSpline(Graphics g, List<Point2D> points) {
        Point2D[] nodePoints = new Point2D.Double[]{
                new Point2D.Double(0., 0.),
                new Point2D.Double(1., 1.),
                new Point2D.Double(2., 2.),
                new Point2D.Double(3., 3.),
                new Point2D.Double(4., 4.),
                new Point2D.Double(5., 3.),
                new Point2D.Double(4., 2.),
                new Point2D.Double(3., 3.),
                new Point2D.Double(2., 4.),
                new Point2D.Double(1., 5.),
                new Point2D.Double(0., 6.),
        };
        Matrix m = new Matrix(new double[][]{
                {-1., 3., -3., 1.},
                {3., -6., 3., 0.},
                {-3., 0., 3., 0.},
                {1., 4., 1., 0.}
        });
        m = m.multiply(1. / 6);
        for (int i = 1; i < nodePoints.length - 2; i++) {
            Matrix pX = new Matrix(new double[][]{
                    {nodePoints[i - 1].getX()},
                    {nodePoints[i].getX()},
                    {nodePoints[i + 1].getX()},
                    {nodePoints[i + 2].getX()},
            });
            Matrix pY = new Matrix(new double[][]{
                    {nodePoints[i - 1].getY()},
                    {nodePoints[i].getY()},
                    {nodePoints[i + 1].getY()},
                    {nodePoints[i + 2].getY()},
            });
            Matrix mX = m.multiplyRight(pX);
            Matrix mY = m.multiplyRight(pY);
            for (double t = 0.; t < 1.; t += 1. / 100) {
                Matrix mT = new Matrix(new double[][]{
                        {pow(t, 3),
                                pow(t, 2),
                                t,
                                1
                        }
                });
                double x = mT.multiplyRight(mX).getMatrix()[0][0];
                double y = mT.multiplyRight(mY).getMatrix()[0][0];
                points.add(new Point2D.Double(x, y));
            }
        }
        ((Graphics2D) g).setStroke(new BasicStroke(5.f));
        for (int i = 0; i < nodePoints.length; i++) {
            Point2D point = nodePoints[i];
            int x1 = (int) (getWidth() * (point.getX() - 0) / 10 + 0.5);
            int y1 = (int) (getHeight() * (point.getY() - 0) / 10 + 0.5);
            g.drawLine(x1, y1, x1, y1);
        }
        for (int i = 0; i < points.size() - 1; i++) {
            Point2D from = points.get(i);
            Point2D to = points.get(i + 1);
            int x1 = (int) (getWidth() * (from.getX() - 0) / 10 + 0.5);
            int x2 = (int) (getWidth() * (to.getX() - 0) / 10 + 0.5);
            int y1 = (int) (getHeight() * (from.getY() - 0) / 10 + 0.5);
            int y2 = (int) (getHeight() * (to.getY() - 0) / 10 + 0.5);
            g.drawLine(x1, y1, x2, y2);
        }
    }

    @Override
    public void handle(EventType type) {
        switch (type) {
            case REPAINT:
                model.update();
                SwingUtilities.invokeLater(this::repaint);
                break;
            case READY:
                lines = model.getResultLines();
                break;
            default:
        }
    }

    private class MouseCoordinates {
        private int startX;
        private int startY;
        private int deltaX;
        private int deltaY;
        private int totalX;
        private int totalY;
    }
}