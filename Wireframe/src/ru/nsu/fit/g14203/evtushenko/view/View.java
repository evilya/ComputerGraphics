package ru.nsu.fit.g14203.evtushenko.view;

import ru.nsu.fit.g14203.evtushenko.EventType;
import ru.nsu.fit.g14203.evtushenko.Observer;
import ru.nsu.fit.g14203.evtushenko.model.Model;
import ru.nsu.fit.g14203.evtushenko.model.Point2D;
import ru.nsu.fit.g14203.evtushenko.model.Shape;
import ru.nsu.fit.g14203.evtushenko.utils.MathUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class View extends JPanel implements Observer {
    private Model model;
    private List<Shape<Point2D>> shapes;

    public View(Model model) {
        this.model = model;
        MouseCoordinates c = new MouseCoordinates();
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                c.deltaX = c.startX - e.getX();
                c.deltaY = c.startY - e.getY();
                double angleX = 2. * Math.PI * c.deltaX / 800;
                double angleY = 2. * Math.PI * c.deltaY / 800;
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
            int sum = 1;
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                sum += e.getWheelRotation();
                System.out.println(sum);
                model.zoom(sum);
            }
        });
        model.update();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (shapes != null) {
            shapes.stream()
                    .flatMap(s -> {
                        g.setColor(s.getColor());
                        ((Graphics2D) g).setStroke(new BasicStroke(s.getWidth()));
                        return s.getLines().stream();
                    })
                    .filter(Objects::nonNull)
                    .forEach(l -> {
                        Color prev = null;
                        Color color = l.getColor();
                        if (color != null){
                            prev = g.getColor();
                            g.setColor(color);
                        }
                        int panelSize = Math.min(getWidth(), getHeight());
                        int x1 = (int) (panelSize * (l.getStart().getX() + 0.5));
                        int y1 = (int) (panelSize * (l.getStart().getY() + 0.5));
                        int x2 = (int) (panelSize * (l.getEnd().getX() + 0.5));
                        int y2 = (int) (panelSize * (l.getEnd().getY() + 0.5));
                        g.drawLine(x1, y1, x2, y2);
                        if (color != null) {
                            g.setColor(prev);
                        }
                    });
        }
    }

    @Override
    public void handle(EventType type) {
        switch (type) {
            case REPAINT:
                model.update();
                break;
            case READY:
                shapes = new ArrayList<>(model.getResultShapes());
                SwingUtilities.invokeLater(this::repaint);
                break;
            default:
        }
    }

    private class MouseCoordinates {
        private int startX;
        private int startY;
        private int deltaX;
        private int deltaY;
    }
}