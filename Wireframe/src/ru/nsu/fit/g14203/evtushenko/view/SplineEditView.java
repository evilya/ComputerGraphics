package ru.nsu.fit.g14203.evtushenko.view;

import ru.nsu.fit.g14203.evtushenko.model.Axis;
import ru.nsu.fit.g14203.evtushenko.model.Model;
import ru.nsu.fit.g14203.evtushenko.model.geom.Point2D;
import ru.nsu.fit.g14203.evtushenko.model.geom.Shape3D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SplineEditView extends JPanel {
    private final Model model;
    private final BasicStroke dashedStroke = new BasicStroke(1.f,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_ROUND,
            0,
            new float[]{1},
            0);
    private final List<Point> viewPoints = new ArrayList<>();
    private int selectedPoint;
    private Shape3D shape;
    private double max;

    public SplineEditView(Model model) {
        this.model = model;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                for (int i = 0; i < viewPoints.size(); i++) {
                    Point point = viewPoints.get(i);
                    if (Math.sqrt(Math.pow(point.x - e.getX(), 2) + Math.pow(point.y - e.getY(), 2)) < 4) {
                        selectedPoint = i;
                        return;
                    }
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (shape != null && e.getButton() == MouseEvent.BUTTON3) {
                    double x = (2 * max * (e.getX() - 10.5)) / (getWidth() - 20) - max;
                    double y = 2 * max * (1 - (e.getY() - 10.5) / (getHeight() - 20)) - max;
                    shape.getNodePoints().add(new Point2D(x, y));
                    repaint();
                    model.updateShapes();
                }
                requestFocus();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                selectedPoint = -1;
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (shape != null && selectedPoint != -1) {
                    double x = (2 * max * (e.getX() - 10.5)) / (getWidth() - 20) - max;
                    double y = 2 * max * (1 - (e.getY() - 10.5) / (getHeight() - 20)) - max;
                    shape.getNodePoints().get(selectedPoint).set(x, y);
                    repaint();
                    model.updateShapes();
                }
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (shape != null && e.getKeyChar() == '\b') {
                    List<Point2D> nodePoints = shape.getNodePoints();
                    if (nodePoints.size() <= 4){
                        return;
                    }
                    nodePoints.remove(nodePoints.size() - 1);
                    repaint();
                    model.updateShapes();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        g.setColor(Color.WHITE);
        g.fillRect(1, 1, getWidth() - 2, getHeight() - 2);
        ((Graphics2D) g).setStroke(dashedStroke);
        g.setColor(Color.BLACK);
        g.drawLine((getWidth() - 1) / 2, 0, (getWidth() - 1) / 2, getHeight());
        g.drawLine(0, (getHeight() - 1) / 2, getWidth(), (getHeight() - 1) / 2);

        ((Graphics2D) g).setStroke(new BasicStroke(1.f));
        g.setColor(Color.BLACK);
        int chosen = model.getChosenShapeIndex();
        if (chosen != -1) {
            shape = model.getSourceShapes().get(chosen);
            List<Point2D> nodePoints = shape.getNodePoints();
            List<Point2D> points = Shape3D.getSplinePoints(nodePoints);

            max = Stream.concat(nodePoints.stream(), points.stream())
                    .flatMap(p -> Stream.of(Math.abs(p.getX()), Math.abs(p.getY())))
                    .max(Double::compareTo).orElse(5.);

            drawNodePoints(nodePoints, g);
            g.setColor(Color.RED);
            drawBase(nodePoints, g);
            g.setColor(Color.BLACK);
            ((Graphics2D) g).setStroke(new BasicStroke(2.f));
            drawSpline(points, g);
        } else {
            shape = null;
        }

    }

    private void drawNodePoints(List<Point2D> points, Graphics g) {
        viewPoints.clear();
        for (Point2D point : points) {
            int x = pointToPixel(Axis.X, point.getX());
            int y = pointToPixel(Axis.Y, point.getY());
            viewPoints.add(new Point(x + 4, y + 4));
            g.drawOval(x, y, 8, 8);
        }
    }

    private void drawSpline(List<Point2D> points, Graphics g) {
        for (int i = 0; i < points.size() - 1; i++) {
            int x1 = pointToPixel(Axis.X, points.get(i).getX());
            int y1 = pointToPixel(Axis.Y, points.get(i).getY());
            int x2 = pointToPixel(Axis.X, points.get(i + 1).getX());
            int y2 = pointToPixel(Axis.Y, points.get(i + 1).getY());
            g.drawLine(x1, y1, x2, y2);
        }
    }

    private void drawBase(List<Point2D> points, Graphics g) {
        for (int i = 0; i < points.size() - 1; i++) {
            int x1 = pointToPixel(Axis.X, points.get(i).getX()) + 4;
            int y1 = pointToPixel(Axis.Y, points.get(i).getY()) + 4;
            int x2 = pointToPixel(Axis.X, points.get(i + 1).getX()) + 4;
            int y2 = pointToPixel(Axis.Y, points.get(i + 1).getY()) + 4;
            g.drawLine(x1, y1, x2, y2);
        }
    }

    private int pointToPixel(Axis axis, double value) {
        switch (axis) {
            case X:
                return (int) ((getWidth() - 20) * (value + max) / (2 * max) + 10.5);
            case Y:
                return (int) ((getHeight() - 20) * (1 - (value + max) / (2 * max)) + 10.5);
            default:
                throw new RuntimeException();
        }
    }

    private static class Point {

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int x;
        int y;
    }
}
