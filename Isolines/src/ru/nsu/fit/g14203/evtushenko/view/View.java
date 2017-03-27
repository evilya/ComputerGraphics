package ru.nsu.fit.g14203.evtushenko.view;

import ru.nsu.fit.g14203.evtushenko.EventType;
import ru.nsu.fit.g14203.evtushenko.Observer;
import ru.nsu.fit.g14203.evtushenko.model.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class View extends JPanel implements Observer {

    private final Model model;
    private double fMin = Double.MAX_VALUE;
    private double fMax = Double.MIN_VALUE;

    public View(Model model) {
        this.model = model;
        setLayout(new GridBagLayout());
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        drawFunction(image);
        g.drawImage(image, 0, 0, null);
    }

    @Override
    public void handle(EventType type) {
        switch (type) {

        }
    }

    private void drawFunction(BufferedImage image) {
        int a = -10;
        int b = 10;
        int dx = b - a;
        int c = -10;
        int d = 10;
        int dy = d - c;
        updateMinMax();
        for (int v = 0; v < getHeight(); v++) {
            double y = (double) dy * v / getHeight() + a;
            for (int u = 0; u < getWidth(); u++) {
                double x = (double) dx * u / getWidth() + c;
                double value = model.value(x, y);
                double m = (value - fMin) / (fMax - fMin);
                m = m > 1. ? 1. : m;
                m = m < 0. ? 0. : m;
                image.setRGB(u, v, new Color((int) (255 * m), (int) (255 * m), (int) (255 * m)).getRGB());
                if (u == 0 || u == getWidth() - 1 || v == 0 || v == getHeight() - 1) {
                    image.setRGB(u, v, Color.RED.getRGB());
                }
            }
        }
    }

    private void updateMinMax() {
        int a = -10;
        int b = 10;
        int dx = b - a;
        int c = -10;
        int d = 10;
        int dy = d - c;
        for (int v = 0; v < getHeight(); v++) {
            double y = (double) dy * v / getHeight() + c;
            for (int u = 0; u < getWidth(); u++) {
                double x = (double) dx * u / getWidth() + a;
                double value = model.value(x, y);
                fMax = value > fMax ? value : fMax;
                fMin = value < fMin ? value : fMin;
            }
        }
        System.out.println(fMin + " " + fMax);
    }
}
