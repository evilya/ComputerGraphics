package ru.nsu.fit.g14203.evtushenko.view;

import ru.nsu.fit.g14203.evtushenko.model.Config;
import ru.nsu.fit.g14203.evtushenko.utils.MathUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static ru.nsu.fit.g14203.evtushenko.utils.MathUtils.constraint;

public class View extends JPanel {
    private final Color DEFAULT_COLOR = Color.LIGHT_GRAY;
    private final DecimalFormat decimalFormat = new DecimalFormat("#.#");
    private final int BOTTOM_LEGEND_PADDING = 3;
    private final JLabel statusLabel;
    private Color borderColor;
    private List<String> labels;
    private BiFunction<Double, Double, Double> function =
            (x, y) -> x * x + y * y;
//    private BiFunction<Double, Double, Double> function =
//                return Math.sqrt((x * x) + (y * y)) * Math.sin(Math.sqrt((x * x) + (y * y)));
//            };

    private double[][] nodes;
    private double fMin = Double.MAX_VALUE;
    private double fMax = Double.MIN_VALUE;
    private Color[] colors;
    private double colorLength;
    private List<List<Point>> basicIsolinePoints;
    private List<List<Point>> extraIsolinePoints;
    private BufferedImage image;
    private boolean interpolation, grid, isolines, loaded, points;
    private int a, b, c, d;
    private int xGrowth, yGrowth;
    private int m, k;


    public View(JLabel statusLabel) {
        this.statusLabel = statusLabel;
        setParameters(-10, 10, -10, 10, 10, 10);
        calculateMinMax();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (loaded) {
                    double x = (double) xGrowth * e.getX() / getPlotWidth() + c;
                    double y = (double) yGrowth * e.getY() / getPlotHeight() + a;
                    double value = function.apply(x, y);
//                    System.out.println(x + " " + y + " " + value);
                    extraIsolinePoints.clear();
                    extraIsolinePoints.add(buildIsoline(value, nodes));
                    repaint();
                }
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (loaded) {
                    double x = (double) xGrowth * e.getX() / getPlotWidth() + c;
                    double y = (double) yGrowth * e.getY() / getPlotHeight() + a;
                    double value = function.apply(x, y);
                    View.this.statusLabel.setText(String.format("x=%.2f, y=%.2f, f(x,y)=%.4f", x, y, value));
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (loaded) {
                    double x = (double) xGrowth * e.getX() / getPlotWidth() + c;
                    double y = (double) yGrowth * e.getY() / getPlotHeight() + a;
                    double value = function.apply(x, y);
                    View.this.statusLabel.setText(String.format("x=%.2f, y=%.2f, f(x,y)=%.4f", x, y, value));
                    extraIsolinePoints.clear();
                    extraIsolinePoints.add(buildIsoline(value, nodes));
                    repaint();
                }
            }
        });
    }

    public void setConfig(Config config) {
        k = config.getK();
        m = config.getM();
        colors = config.getColors();
        borderColor = config.getBorderColor();
        int n = colors.length - 1;
        double[] isolineLevels = new double[n];
        basicIsolinePoints = new ArrayList<>(n);
        extraIsolinePoints = new ArrayList<>();
        double delta = (fMax - fMin) / (colors.length);
        for (int i = 0; i < isolineLevels.length; i++) {
            isolineLevels[i] = fMin + delta * (i + 1);
        }

        labels = DoubleStream.of(isolineLevels)
                .mapToObj(decimalFormat::format)
                .collect(Collectors.toList());


        nodes = calculateFunctionInNodes();
        for (int i = 0; i < isolineLevels.length; i++) {
            basicIsolinePoints.add(i, buildIsoline(isolineLevels[i], nodes));
        }
        loaded = true;
        repaint();
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
        if (loaded) {
            int n = colors.length - 1;
            double[] isolineLevels = new double[n];
            basicIsolinePoints = new ArrayList<>(n);
            extraIsolinePoints = new ArrayList<>();
            double delta = (fMax - fMin) / (colors.length);
            for (int i = 0; i < isolineLevels.length; i++) {
                isolineLevels[i] = fMin + delta * (i + 1);
            }

            labels = DoubleStream.of(isolineLevels)
                    .mapToObj(decimalFormat::format)
                    .collect(Collectors.toList());


            nodes = calculateFunctionInNodes();
            for (int i = 0; i < isolineLevels.length; i++) {
                basicIsolinePoints.add(i, buildIsoline(isolineLevels[i], nodes));
            }
        }
        repaint();
    }

    public void setInterpolation(boolean interpolation) {
        this.interpolation = interpolation;
        repaint();
    }

    public void setGrid(boolean grid) {
        this.grid = grid;
        repaint();
    }

    public void setIsolines(boolean isolines) {
        this.isolines = isolines;
        repaint();
    }

    public void setPoints(boolean points) {
        this.points = points;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        drawLegend(image);
        drawFunction(image);
        g.drawImage(image, 0, 0, null);
        if (loaded) {
            drawLabels(g);
            drawGrid(g);
            drawIsolines(g);
            drawPoints(g);
        }
    }

    private void drawPoints(Graphics g) {
        if (points) {
            g.setColor(Color.RED);
            for (List<Point> points : Stream.of(basicIsolinePoints, extraIsolinePoints)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList())) {
                for (int i = 0; i < points.size(); i += 2) {
                    Point from = points.get(i);
                    Point to = points.get(i + 1);
                    int x1 = (int) (getPlotWidth() * (from.getX() - a) / xGrowth + 0.5);
                    int x2 = (int) (getPlotWidth() * (to.getX() - a) / xGrowth + 0.5);
                    int y1 = (int) (getPlotHeight() * (from.getY() - c) / yGrowth + 0.5);
                    int y2 = (int) (getPlotHeight() * (to.getY() - c) / yGrowth + 0.5);
                    g.drawOval(x1 - 1, y1 - 1, 2, 2);
                    g.drawOval(x2 - 1, y2 - 1, 2, 2);
                }
            }
        }
    }

    private void drawGrid(Graphics g) {
        if (grid) {
            g.setColor(Color.BLACK);
            double dx = (double) xGrowth / (k - 1);
            double dy = (double) yGrowth / (m - 1);
            for (int j = 1; j < m - 1; j++) {
                int y1 = (int) (getPlotHeight() * (dy * j) / yGrowth + 0.5);
                g.drawLine(0, y1, getPlotWidth(), y1);
            }
            for (int i = 1; i < k - 1; i++) {
                int x1 = (int) (getPlotWidth() * (dx * i) / xGrowth + 0.5);
                g.drawLine(x1, 0, x1, getPlotHeight() - 1);
            }
        }
    }

    private void drawIsolines(Graphics g) {
        if (isolines) {
            g.setColor(borderColor);
            for (List<Point> points : Stream.of(basicIsolinePoints, extraIsolinePoints)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList())) {
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
                double x = a + dx * i;
                double y = c + dy * j;
                Point upperPoint = null;
                Point lowerPoint = null;
                Point leftPoint = null;
                Point rightPoint = null;
                if (MathUtils.isInRange(level, nodes[j][i], nodes[j][i + 1])) {
                    double interpolationCoeff = interpolationCoeff(i, j, i + 1, j, nodes, level);
                    upperPoint =
                            new Point(x + dx
                                    * interpolationCoeff,
                                    y);

                }
                if (MathUtils.isInRange(level, nodes[j][i + 1], nodes[j + 1][i + 1])) {
                    rightPoint =
                            new Point(x + dx,
                                    y + dy
                                            * interpolationCoeff(i + 1, j, i + 1, j + 1, nodes, level));
                }
                if (MathUtils.isInRange(level, nodes[j + 1][i], nodes[j + 1][i + 1])) {
                    lowerPoint =
                            new Point(x + dx
                                    * interpolationCoeff(i, j + 1, i + 1, j + 1, nodes, level),
                                    y + dy);
                }
                if (MathUtils.isInRange(level, nodes[j][i], nodes[j + 1][i])) {
                    leftPoint =
                            new Point(x,
                                    y + dy
                                            * interpolationCoeff(i, j, i, j + 1, nodes, level));
                }
                Set<Point> points =
                        Stream.of(leftPoint, rightPoint, lowerPoint, upperPoint)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toSet());
                if (points.size() == 4) {
                    double centerValue = function.apply(x + 0.5 * dx, y + 0.5 * dy);
                    if (nodes[i][j] >= level && centerValue >= level || nodes[i][j] < level && centerValue < level) {
                        result.add(upperPoint);
                        result.add(rightPoint);
                        result.add(leftPoint);
                        result.add(lowerPoint);
                    } else {
                        result.add(upperPoint);
                        result.add(leftPoint);
                        result.add(rightPoint);
                        result.add(lowerPoint);
                    }
                } else if (points.size() % 2 == 0) {
                    result.addAll(points);
                }
            }
        }
        return result;
    }

    private double interpolationCoeff(int x1, int y1, int x2, int y2, double[][] nodes, double value) {
        return (value - nodes[y1][x1])
                / (nodes[y2][x2] - nodes[y1][x1]);

    }

    private void drawFunction(BufferedImage image) {
        if (loaded) {
            for (int v = 0; v < getPlotHeight(); v++) {
                double y = (double) yGrowth * v / getPlotHeight() + c;
                for (int u = 0; u < getPlotWidth(); u++) {
                    double x = (double) xGrowth * u / getPlotWidth() + a;
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
        } else {
            Graphics2D g = image.createGraphics();
            g.setColor(DEFAULT_COLOR);
            g.fillRect(0, 0, getPlotWidth(), getPlotHeight());
            g.setColor(Color.BLACK);
            g.setFont(new Font("TimesRoman", Font.BOLD, 22));
            FontMetrics fm = g.getFontMetrics();
            String message = "No document loaded";
            int textWidth = fm.stringWidth(message);
            int textHeight = fm.getHeight();
            if (textHeight < getPlotHeight() - 10 && textWidth < getPlotWidth() - 20) {
                g.drawString(message, (getPlotWidth() - textWidth) / 2, (getPlotHeight() - textHeight) / 2);
            }
            g.dispose();
        }
    }

    private void drawLegend(BufferedImage image) {
        if (loaded) {
            colorLength = (double) (getPlotWidth() - 2 * getLegendOffsetX()) / colors.length;
            Color resultColor;
            for (int x = getLegendOffsetX(); x < getPlotWidth() - getLegendOffsetX(); x++) {
                if (interpolation) {
                    resultColor = getInterpolatedColor(x);
                } else {
                    resultColor = colors[
                            constraint(
                                    (int) ((x - getLegendOffsetX()) / colorLength),
                                    0,
                                    colors.length - 1)];
                }
                for (int y = getPlotHeight() + getLegendOffsetY();
                     y < getHeight() - BOTTOM_LEGEND_PADDING * getLegendOffsetY();
                     y++) {
                    image.setRGB(x, y, resultColor.getRGB());
                }
            }
        } else {
            for (int x = getLegendOffsetX(); x < getPlotWidth() - getLegendOffsetX(); x++) {
                for (int y = getPlotHeight() + getLegendOffsetY();
                     y < getHeight() - BOTTOM_LEGEND_PADDING * getLegendOffsetY();
                     y++) {
                    image.setRGB(x, y, DEFAULT_COLOR.getRGB());
                }
            }
        }
    }

    private Color getInterpolatedColor(int x) {
        Color resultColor;
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
        return resultColor;
    }

    private void drawLabels(Graphics g) {
        g.setColor(Color.BLACK);
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
//        System.out.println(fMin + " " + fMax);
    }

    private int getPlotWidth() {
        return (int) (getWidth() * 1.);
    }

    private int getPlotHeight() {
        return (int) ((getHeight()) * 0.8);
    }

    private int getLegendOffsetX() {
        return (int) (getPlotWidth() * 0.1);
    }

    private int getLegendOffsetY() {
        return (int) ((getHeight() - getPlotHeight()) * 0.1);
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public int getC() {
        return c;
    }

    public int getD() {
        return d;
    }

    public int getM() {
        return m;
    }

    public int getK() {
        return k;
    }
}