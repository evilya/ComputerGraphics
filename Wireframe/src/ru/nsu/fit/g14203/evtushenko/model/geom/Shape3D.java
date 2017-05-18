package ru.nsu.fit.g14203.evtushenko.model.geom;

import ru.nsu.fit.g14203.evtushenko.math.Matrix;
import ru.nsu.fit.g14203.evtushenko.model.Model;
import ru.nsu.fit.g14203.evtushenko.model.ModelParameters;
import ru.nsu.fit.g14203.evtushenko.utils.MathUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.awt.geom.Point2D.distance;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;

public class Shape3D {
    private Model model;
    private List<Point2D> nodePoints;
    private List<Line<Point3D>> lines;
    private Color color = Color.BLACK;
    private float width = 1.f;
    private Point3D selfCenter;
    private Point3D position;

    private Matrix rotation = new Matrix(new double[][]{
            {1, 0, 0},
            {0, 1, 0},
            {0, 0, 1}
    });

    public Shape3D(List<Line<Point3D>> lines, Color color, float width) {
        this.lines = lines;
        selfCenter = findCenter(lines);
        position = new Point3D(0, 0, 0);
        this.color = color;
        this.width = width;
    }

    public Shape3D(List<Point2D> points, Model model) {
        this.nodePoints = points;
        this.model = model;
        this.lines = rotateSpline(points);
        selfCenter = findCenter(lines);
        position = new Point3D(0, 0, 0);
        addBase();
    }

    public Shape3D(List<Point2D> lines, Color color, Model model) {
        this(lines, model);
        this.color = color;
    }

    public Shape3D(List<Point2D> lines, Color color, float width, Model model) {
        this(lines, color, model);
        this.width = width;
    }

    public List<Line<Point3D>> getLines() {
        return lines
                .stream()
                .map(l -> {
                    Point3D start = move(rotate(l.getStart()));
                    Point3D end = move(rotate(l.getEnd()));
                    return new Line<>(start, end, l.getColor());
                }).collect(Collectors.toList());
    }

    public Color getColor() {
        return color;
    }

    public float getWidth() {
        return width;
    }

    public Point3D getPosition() {
        return position;
    }

    public void update() {
        lines = rotateSpline(nodePoints);
        selfCenter = findCenter(lines);
        addBase();
    }

    public List<Point2D> getNodePoints() {
        return nodePoints;
    }

    private Point3D move(Point3D p) {
        return new Point3D(p.getX() + position.getX() - selfCenter.getX(),
                p.getY() + position.getY() - selfCenter.getY(),
                p.getZ() + position.getZ() - selfCenter.getZ());
    }

    public void rotate(double angleX, double angleY) {
        Matrix xRotate = new Matrix(new double[][]{
                {cos(angleX), -sin(angleX), 0},
                {sin(angleX), cos(angleX), 0},
                {0, 0, 1, 0}
        });
        Matrix yRotate = new Matrix(new double[][]{
                {cos(angleY), 0, sin(angleY)},
                {0, 1, 0},
                {-sin(angleY), 0, cos(angleY)}
        });
        rotation = yRotate.multiply(xRotate).multiply(rotation);
    }

    private Point3D findCenter(final List<Line<Point3D>> lines) {
        final Function<ToDoubleFunction<Point3D>, Stream<Double>> streamGenerator = (f) -> lines.stream()
                .flatMap(l -> Stream.of(l.getEnd(), l.getStart()))
                .flatMap(p -> Stream.of(f.applyAsDouble(p)));

        double xMax = streamGenerator.apply(Point3D::getX)
                .max(Double::compareTo).orElse(0.);
        double xMin = streamGenerator.apply(Point3D::getX)
                .min(Double::compareTo).orElse(0.);
        double yMax = streamGenerator.apply(Point3D::getY)
                .max(Double::compareTo).orElse(0.);
        double yMin = streamGenerator.apply(Point3D::getY)
                .min(Double::compareTo).orElse(0.);
        double zMax = streamGenerator.apply(Point3D::getZ)
                .max(Double::compareTo).orElse(0.);
        double zMin = streamGenerator.apply(Point3D::getZ)
                .min(Double::compareTo).orElse(0.);

        return new Point3D((xMax + xMin) / 2,
                (yMax + yMin) / 2,
                (zMax + zMin) / 2);
    }

    private Point3D rotate(Point3D p) {
        double[][] res = rotation.multiply(
                new Matrix(new double[][]{
                        {p.getX() - selfCenter.getX()},
                        {p.getY() - selfCenter.getY()},
                        {p.getZ() - selfCenter.getZ()}
                })).getMatrix();
        return new Point3D(res[0][0] + selfCenter.getX(),
                res[1][0] + selfCenter.getY(),
                res[2][0] + selfCenter.getZ());

    }

    private List<Line<Point3D>> rotateSpline(List<Point2D> nodePoints) {
        ModelParameters parameters = model.getParameters();
        List<Line<Point3D>> result = new ArrayList<>();
        List<Point2D> points = getSplinePoints(nodePoints);
        double len = getSplineLen(points);
        int[] param = getTargetLengthParameters(len * parameters.getA(),
                len * parameters.getB(),
                points);
        double angleDelta = (parameters.getD() - parameters.getC())
                / (parameters.getM() * parameters.getK());
        double pDelta = ((double) (param[1] - param[0])) / (parameters.getN() * parameters.getK());
        for (int v = 0; v < parameters.getM() * parameters.getK(); v++) {
            double angle = (v * angleDelta) + parameters.getC();
            double nextAngle = ((v + 1) * angleDelta) + parameters.getC();
            for (int i = 0; i <= parameters.getN() * parameters.getK(); i += parameters.getK()) {
                int p = MathUtils.constraint((int) (Math.floor(i * pDelta) + param[0]),0, points.size()-1);
                result.add(new Line<>(
                        new Point3D(points.get(p).getY() * cos(angle),
                                points.get(p).getY() * sin(angle),
                                points.get(p).getX()),
                        new Point3D(points.get(p).getY() * cos(nextAngle),
                                points.get(p).getY() * sin(nextAngle),
                                points.get(p).getX())));
            }

            if (v % parameters.getK() == 0) {
                for (int i = 0; i < parameters.getN() * parameters.getK(); i++) {
                    int p = MathUtils.constraint((int) (Math.floor(i * pDelta) + param[0]), 0, points.size()-1);
                    int nextP = MathUtils.constraint((int) (Math.floor((i + 1) * pDelta) + param[0]), 0, points.size()-1);
                    result.add(new Line<>(
                            new Point3D(points.get(p).getY() * cos(angle),
                                    points.get(p).getY() * sin(angle),
                                    points.get(p).getX()),
                            new Point3D(points.get(nextP).getY() * cos(angle),
                                    points.get(nextP).getY() * sin(angle),
                                    points.get(nextP).getX())));
                }
            }
        }
        double angle = (parameters.getM() * parameters.getK() * angleDelta) + parameters.getC();
        for (int i = 0; i < parameters.getN() * parameters.getK(); i++) {
            int p = MathUtils.constraint((int) (Math.floor(i * pDelta) + param[0]), 0, points.size()-1);
            int nextP = MathUtils.constraint((int) (Math.floor((i + 1) * pDelta) + param[0]), 0, points.size()-1);
            result.add(new Line<>(
                    new Point3D(points.get(p).getY() * cos(angle),
                            points.get(p).getY() * sin(angle),
                            points.get(p).getX()),
                    new Point3D(points.get(nextP).getY() * cos(angle),
                            points.get(nextP).getY() * sin(angle),
                            points.get(nextP).getX())));
        }
        return result;
    }

    private int[] getTargetLengthParameters(double startOffset, double endOffset, List<Point2D> points) {
        double len = 0.;
        boolean startFound = false;
        int[] result = new int[2];
        for (int i = 0; i < points.size() - 1; i++) {
            Point2D begin = points.get(i);
            Point2D end = points.get(i + 1);
            len += distance(begin.getX(), begin.getY(), end.getX(), end.getY());
            if (!startFound && len >= startOffset) {
                result[0] = i;
                startFound = true;
            }
            if (startFound && len >= endOffset) {
                result[1] = i;
                break;
            }
        }
        return result;
    }

    public static List<Point2D> getSplinePoints(List<Point2D> nodePoints) {
        List<Point2D> points = new ArrayList<>();

        Matrix m = new Matrix(new double[][]{
                {-1., 3., -3., 1.},
                {3., -6., 3., 0.},
                {-3., 0., 3., 0.},
                {1., 4., 1., 0.}
        });
        m = m.multiply(1. / 6);
        for (int i = 1; i < nodePoints.size() - 2; i++) {
            Matrix pX = new Matrix(new double[][]{
                    {nodePoints.get(i - 1).getX()},
                    {nodePoints.get(i).getX()},
                    {nodePoints.get(i + 1).getX()},
                    {nodePoints.get(i + 2).getX()},
            });
            Matrix pY = new Matrix(new double[][]{
                    {nodePoints.get(i - 1).getY()},
                    {nodePoints.get(i).getY()},
                    {nodePoints.get(i + 1).getY()},
                    {nodePoints.get(i + 2).getY()},
            });
            Matrix mX = m.multiply(pX);
            Matrix mY = m.multiply(pY);
            for (double t = 0.; t < 1.; t += 1. / 100) {
                Matrix mT = new Matrix(new double[][]{
                        {pow(t, 3),
                                pow(t, 2),
                                t,
                                1
                        }
                });
                double x = mT.multiply(mX).getMatrix()[0][0];
                double y = mT.multiply(mY).getMatrix()[0][0];
                points.add(new Point2D(x, y));
            }
        }
        return points;
    }

    private double getSplineLen(List<Point2D> points) {
        double len = 0.;
        for (int i = 0; i < points.size() - 1; i++) {
            Point2D begin = points.get(i);
            Point2D end = points.get(i + 1);
            len += distance(begin.getX(), begin.getY(), end.getX(), end.getY());
        }
        return len;
    }

    private void addBase() {
        lines.add(new Line<>(selfCenter,
                selfCenter.shifted(0.5, 0, 0),
                Color.RED));
        lines.add(new Line<>(selfCenter,
                selfCenter.shifted(0, 0.5, 0),
                Color.GREEN));
        lines.add(new Line<>(selfCenter,
                selfCenter.shifted(0, 0, 0.5),
                Color.BLUE));
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
