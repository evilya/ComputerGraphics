package ru.nsu.fit.g14203.evtushenko.view;

import ru.nsu.fit.g14203.evtushenko.EventType;
import ru.nsu.fit.g14203.evtushenko.Observer;
import ru.nsu.fit.g14203.evtushenko.model.Model;
import ru.nsu.fit.g14203.evtushenko.model.geom.Shape2D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class View extends JPanel implements Observer {
    private final int SIZE = 600;
    private Model model;
    private List<Shape2D> shapes;

    public View(Model model) {
        this.model = model;
        MouseCoordinates c = new MouseCoordinates();
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                c.deltaX = c.startX - e.getX();
                c.deltaY = c.startY - e.getY();
                double angleX = 2. * Math.PI * c.deltaX / 400;
                double angleY = 2. * Math.PI * c.deltaY / 400;
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
                model.zoom(sum);

            }
        });
        model.update();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (shapes != null) {
            int width = (int) (SIZE * model.getPovConverter().getsW());
            int height = (int) (SIZE * model.getPovConverter().getsH());
            BufferedImage image = new BufferedImage(width,
                    height,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setColor(model.getBackgroundColor());
            graphics.fillRect(0, 0, width, height);
            shapes.stream()
                    .flatMap(s -> {
                        graphics.setColor(s.getColor());
                        graphics.setStroke(new BasicStroke(s.getWidth()));
                        return s.getLines().stream();
                    })
                    .filter(Objects::nonNull)
                    .forEach(l -> {
                        Color prev = null;
                        Color color = l.getColor();
                        if (color != null) {
                            prev = graphics.getColor();
                            graphics.setColor(color);
                        }
                        int x1 = (int) (width * (l.getStart().getX() + 0.5));
                        int y1 = (int) (height * (l.getStart().getY() + 0.5));
                        int x2 = (int) (width * (l.getEnd().getX() + 0.5));
                        int y2 = (int) (height * (l.getEnd().getY() + 0.5));
                        graphics.drawLine(x1, y1, x2, y2);
                        if (color != null) {
                            graphics.setColor(prev);
                        }
                    });
            g.drawImage(image, 10, 10, null);
        }

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension((int) (SIZE * model.getPovConverter().getsW() + 10),
                (int) (SIZE * model.getPovConverter().getsH() + 10));
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
                SwingUtilities.invokeLater(() -> getParent().revalidate());
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