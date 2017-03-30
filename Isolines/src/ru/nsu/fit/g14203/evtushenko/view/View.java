package ru.nsu.fit.g14203.evtushenko.view;

import ru.nsu.fit.g14203.evtushenko.model.Config;
import ru.nsu.fit.g14203.evtushenko.model.FileLoader;
import ru.nsu.fit.g14203.evtushenko.utils.MathUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static ru.nsu.fit.g14203.evtushenko.utils.MathUtils.constraint;

public class View extends JPanel {
    private final int BOTTOM_LEGEND_PADDING = 3;
    private final List<String> labels;
    private BiFunction<Double, Double, Double> function =
            (x, y) -> x * x * Math.sin(x) + y * y * Math.cos(y);
    private double fMin = Double.MAX_VALUE;
    private double fMax = Double.MIN_VALUE;
    private Color[] colors;
    private double colorLength;
    private List<List<Point>> isolinePoints;
    private BufferedImage image;
    private boolean interpolation;
    private int a, b, c, d;
    private int xGrowth, yGrowth;
    private int m, k;

    public View() {
        setLayout(new GridBagLayout());
        setParameters(-10, 10, -10, 10, 10, 10);
        Config config =
                FileLoader.readConfig(
                        "FIT_14203_Evtushenko_Ilya_Isolines_Data/config.txt");
        k = config.getK();
        m = config.getM();
        colors = config.getColors();
        calculateMinMax();
        int n = colors.length - 1;
        double[] isolineLevels = new double[n];
        isolinePoints = new ArrayList<>(n);
        double delta = (fMax - fMin) / (colors.length);
        for (int i = 0; i < isolineLevels.length; i++) {
            isolineLevels[i] = fMin + delta * (i + 1);
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        labels = DoubleStream.of(isolineLevels)
                .mapToObj(decimalFormat::format)
                .collect(Collectors.toList());
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(new Color(image.getRGB(e.getX(), e.getY())));
            }
        });

        double[][] nodes = calculateFunctionInNodes();
        for (int i = 0; i < isolineLevels.length; i++) {
            isolinePoints.add(i, buildIsoline(isolineLevels[i], nodes));
        }
    }

    public void setParameters(int a, int b, int c, int d, int k, int m) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.k = k;
        this.m = m;
        xGrowth = b - a;
        yGrowth = d - c;
    }

    public void setInterpolation(boolean interpolation) {
        this.interpolation = interpolation;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        drawLegend(image);
        drawLabels((Graphics2D) g);
        drawFunction(image);
        g.drawImage(image, 0, 0, null);
        for (List<Point> points : isolinePoints) {
            for (int i = 0; i < points.size(); i += 2) {
                Point from = points.get(i);
                Point to = points.get(i + 1);
                int x1 = (int) (getPlotWidth() * (from.getX() - a) / xGrowth + 0.5);
                int x2 = (int) (getPlotWidth() * (to.getX() - a) / xGrowth + 0.5);
                int y1 = (int) (getPlotHeight() * (from.getY() - c) / yGrowth + 0.5);
                int y2 = (int) (getPlotHeight() * (to.getY() - c) / yGrowth + 0.5);
                g.drawLine(x1,
                        MathUtils.constraint(y1, 0, getPlotHeight() - 1),
                        x2,
                        MathUtils.constraint(y2, 0, getPlotHeight() - 1));
            }
        }
    }

    private double[][] calculateFunctionInNodes() {
        double[][] result = new double[m][k];
        double dx = (double) xGrowth / (k - 1);
        double dy = (double) yGrowth / (m - 1);
        for (int j = 0; j < m; j++) {
            double y = c + dy * j;
            for (int i = 0; i < k; i++) {
                double x = a + dx * i;
                result[j][i] = function.apply(x, y);
            }
        }
        return result;
    }

    private List<Point> buildIsoline(double level, double[][] nodes) {
        List<Point> result = new ArrayList<>();
        double dx = (double) xGrowth / (k - 1);
        double dy = (double) yGrowth / (m - 1);
        for (int j = 0; j < m - 1; j++) {
            for (int i = 0; i < k - 1; i++) {
                BitSet bs = new BitSet(4);
                if (nodes[j][i] > level)
                    bs.set(0);
                if (nodes[j][i + 1] > level)
                    bs.set(1);
                if (nodes[j + 1][i + 1] > level)
                    bs.set(2);
                if (nodes[j + 1][i] > level)
                    bs.set(3);
                if (bs.cardinality() >= 3) {
                    bs.flip(0, 4);
                }
                switch (bs.cardinality()) {
                    case 0:
                        break;
                    case 1:
                        int node = bs.nextSetBit(0);
                        double x, y, nextX, nextY;
                        switch (node) {
                            case 0:
                                x = a + dx * i;
                                y = c + dy * j;
                                nextX = x + Math.abs((nodes[j][i] - level) / (nodes[j][i] - nodes[j][i + 1])) * dx;
                                result.add(new Point(nextX, y));
                                nextY = y + Math.abs((nodes[j][i] - level) / (nodes[j][i] - nodes[j + 1][i])) * dy;
                                result.add(new Point(x, nextY));
                                break;
                            case 1:
                                x = a + dx * (i + 1);
                                y = c + dy * j;
                                nextX = x - Math.abs((nodes[j][i + 1] - level) / (nodes[j][i + 1] - nodes[j][i])) * dx;
                                result.add(new Point(nextX, y));
                                nextY = y + Math.abs((nodes[j][i + 1] - level) / (nodes[j][i + 1] - nodes[j + 1][i + 1])) * dy;
                                result.add(new Point(x, nextY));
                                break;
                            case 2:
                                x = a + dx * (i + 1);
                                y = c + dy * (j + 1);
                                nextX = x - Math.abs((nodes[j + 1][i + 1] - level) / (nodes[j + 1][i + 1] - nodes[j + 1][i])) * dx;
                                result.add(new Point(nextX, y));
                                nextY = y - Math.abs((nodes[j + 1][i + 1] - level) / (nodes[j + 1][i + 1] - nodes[j][i + 1])) * dy;
                                result.add(new Point(x, nextY));
                                break;
                            case 3:
                                x = a + dx * i;
                                y = c + dy * (j + 1);
                                nextX = x + Math.abs((nodes[j + 1][i] - level) / (nodes[j + 1][i] - nodes[j + 1][i + 1])) * dx;
                                result.add(new Point(nextX, y));
                                nextY = y - Math.abs((nodes[j + 1][i] - level) / (nodes[j + 1][i] - nodes[j][i])) * dy;
                                result.add(new Point(x, nextY));
                                break;
                        }
                        break;
                    case 2:
                        int node1 = bs.nextSetBit(0);
                        int node2 = bs.nextSetBit(node1 + 1);
                        if ((node2 - node1) % 2 == 1) {
                            if (node1 == 0 && node2 == 1) {
                                y = c + dy * j;
                                x = a + dx * i;
                                nextY = y + interpolationCoeff(i, j, i, j + 1, nodes, level) * dy;
                                result.add(new Point(x, nextY));
                                x = a + dx * (i + 1);
                                nextY = y + interpolationCoeff(i + 1, j, i + 1, j + 1, nodes, level) * dy;
                                result.add(new Point(x, nextY));
                            } else if (node1 == 2 && node2 == 3) {
                                y = c + dy * (j + 1);
                                x = a + dx * i;
                                nextY = y - interpolationCoeff(i, j + 1, i, j, nodes, level) * dy;
                                result.add(new Point(x, nextY));
                                x = a + dx * (i + 1);
                                nextY = y - interpolationCoeff(i + 1, j + 1, i + 1, j, nodes, level) * dy;
                                result.add(new Point(x, nextY));
                            } else if (node1 == 0 && node2 == 3) {
                                x = a + dx * i;
                                y = c + dy * j;
                                nextX = x + interpolationCoeff(i, j, i + 1, j, nodes, level) * dx;
                                result.add(new Point(nextX, y));
                                y = c + dy * (j + 1);
                                nextX = x + interpolationCoeff(i, j + 1, i + 1, j + 1, nodes, level) * dx;
                                result.add(new Point(nextX, y));
                            } else if (node1 == 1 && node2 == 2) {
                                x = a + dx * (i + 1);
                                y = c + dy * j;
                                nextX = x - interpolationCoeff(i + 1, j, i, j, nodes, level) * dx;
                                result.add(new Point(nextX, y));
                                y = c + dy * (j + 1);
                                nextX = x - interpolationCoeff(i + 1, j + 1, i, j + 1, nodes, level) * dx;
                                result.add(new Point(nextX, y));
                            }
                        } else {

                        }
                        break;
                }
            }
        }
        return result;
    }

    private double interpolationCoeff(int x1, int y1, int x2, int y2, double[][] nodes, double value) {
        return Math.abs((nodes[y1][x1] - value) / (nodes[y1][x1] - nodes[y2][x2]));
    }

    private void drawFunction(BufferedImage image) {
        for (int v = 0; v < getPlotHeight(); v++) {
            double y = (double) yGrowth * v / getPlotHeight() + a;
            for (int u = 0; u < getPlotWidth(); u++) {
                double x = (double) xGrowth * u / getPlotWidth() + c;
                double value = function.apply(x, y);
                if (interpolation) {
                    int rgb = image.getRGB(
                            (int) constraint(
                                    (getPlotWidth() - 2 * getLegendOffsetX())
                                            * (value - fMin) / (fMax - fMin)
                                            + getLegendOffsetX(),
                                    0,
                                    getPlotWidth() - 1 - getLegendOffsetX()),
                            getPlotHeight() + getLegendOffsetY());
                    image.setRGB(u, v, rgb);
                } else {
                    double colorValue = (value - fMin) * colors.length / (fMax - fMin);
                    int colorIndex = (int) constraint(colorValue, 0, colors.length - 1);
                    image.setRGB(u, v, colors[colorIndex].getRGB());
                }
            }
        }
    }

    private void drawLegend(BufferedImage image) {
        colorLength = (double) (getPlotWidth() - 2 * getLegendOffsetX()) / colors.length;
        Color resultColor;
        for (int x = getLegendOffsetX(); x < getPlotWidth() - getLegendOffsetX(); x++) {
            if (interpolation) {
                int i = (int) ((x - getLegendOffsetX()) / colorLength - 0.5);
                Color fromColor = colors[Math.max(i, 0)];
                Color toColor = colors[Math.min(i, colors.length - 2) + 1];
                double fromX = (i + 0.5) * colorLength;
                double toX = fromX + colorLength;
                double l1 = (x - getLegendOffsetX() - fromX) / colorLength;
                double l2 = (toX - x + getLegendOffsetX()) / colorLength;
                int newRed =
                        (int) (toColor.getRed() * l1
                                + fromColor.getRed() * l2);

                int newGreen =
                        (int) (toColor.getGreen() * l1
                                + fromColor.getGreen() * l2);

                int newBlue =
                        (int) (toColor.getBlue() * l1
                                + fromColor.getBlue() * l2);

                resultColor = new Color(constraint(newRed),
                        constraint(newGreen),
                        constraint(newBlue));
            } else {
                resultColor = colors[constraint((int) ((x - getLegendOffsetX()) / colorLength), 0, colors.length - 1)];
            }
            for (int y = getPlotHeight() + getLegendOffsetY(); y < getHeight() - BOTTOM_LEGEND_PADDING * getLegendOffsetY(); y++) {
                image.setRGB(x, y, resultColor.getRGB());
            }
        }
    }

    private void drawLabels(Graphics2D g) {
        FontMetrics fontMetrics = g.getFontMetrics();
        int sumLabelsLength = labels.stream()
                .mapToInt(fontMetrics::stringWidth)
                .sum();
        double legendOffset = getLegendOffsetX();
        int bottomSpace = BOTTOM_LEGEND_PADDING * getLegendOffsetY();
        if (bottomSpace > fontMetrics.getHeight() &&
                sumLabelsLength < getWidth() - 2 * legendOffset - 2 * colorLength) {
            for (int i = 0; i < labels.size(); i++) {
                String label = labels.get(i);
                g.drawString(label,
                        (int) (legendOffset + (i + 1) * colorLength - 0.5 * fontMetrics.stringWidth(label)),
                        (int) (getHeight() - 0.5 * (bottomSpace - fontMetrics.getHeight())));
            }
        }
    }

    private void calculateMinMax() {
        GraphicsDevice[] screenDevices = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getScreenDevices();
        int width = Stream.of(screenDevices)
                .mapToInt(d -> d.getDisplayMode().getWidth())
                .max()
                .orElse(1920);
        int height = Stream.of(screenDevices)
                .mapToInt(d -> d.getDisplayMode().getHeight())
                .max()
                .orElse(1080);
        for (int v = 0; v < height; v++) {
            double y = (double) yGrowth * v / height + c;
            for (int u = 0; u < width; u++) {
                double x = (double) xGrowth * u / width + a;
                double value = function.apply(x, y);
                fMax = value > fMax ? value : fMax;
                fMin = value < fMin ? value : fMin;
            }
        }
        System.out.println(fMin + " " + fMax);
    }

    private int getPlotWidth() {
        return (int) (getWidth() * 1.);
    }

    private int getPlotHeight() {
        return (int) (getHeight() * 0.8);
    }

    private int getLegendOffsetX() {
        return (int) (getPlotWidth() * 0.1);
    }

    private int getLegendOffsetY() {
        return (int) ((getHeight() - getPlotHeight()) * 0.1);
    }
}