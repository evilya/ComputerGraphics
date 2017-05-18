package ru.nsu.fit.g14203.evtushenko.model;

import ru.nsu.fit.g14203.evtushenko.EventType;
import ru.nsu.fit.g14203.evtushenko.math.Matrix;
import ru.nsu.fit.g14203.evtushenko.model.geom.*;
import ru.nsu.fit.g14203.evtushenko.utils.PointMatrixConverter;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Model extends AbstractModel {
    private int chosenShapeIndex = -1;
    private boolean rotateShape = false;

    private Shape3D box;
    private List<Shape3D> sourceShapes = new ArrayList<>();
    private List<Shape2D> resultShapes = new ArrayList<>();

    private ModelParameters parameters = new ModelParameters(this);
    private SceneRotator rotator = new SceneRotator();
    private Scaler scaler = new Scaler(this);
    private PovConverter povConverter = new PovConverter(this);
    private CameraConverter cameraConverter = new CameraConverter();
    private Color backgroundColor = Color.WHITE;


    public Model() {
        setBox(new Shape3D(new ArrayList<>(Arrays.asList(
                new Line<>(new Point3D(1, 1, 1), new Point3D(1, -1, 1)),
                new Line<>(new Point3D(-1, -1, -1), new Point3D(-1, -1, 1)),
                new Line<>(new Point3D(-1, -1, -1), new Point3D(-1, 1, -1)),
                new Line<>(new Point3D(-1, -1, -1), new Point3D(1, -1, -1)),
                new Line<>(new Point3D(1, 1, 1), new Point3D(-1, 1, 1)),
                new Line<>(new Point3D(1, 1, 1), new Point3D(1, 1, -1)),
                new Line<>(new Point3D(1, 1, -1), new Point3D(1, -1, -1)),
                new Line<>(new Point3D(1, 1, -1), new Point3D(-1, 1, -1)),
                new Line<>(new Point3D(1, -1, 1), new Point3D(-1, -1, 1)),
                new Line<>(new Point3D(1, -1, 1), new Point3D(1, -1, -1)),
                new Line<>(new Point3D(-1, 1, 1), new Point3D(-1, -1, 1)),
                new Line<>(new Point3D(-1, 1, 1), new Point3D(-1, 1, -1)))),
                Color.BLACK,
                2.f));
        addShape(new ArrayList<>(Arrays.asList(
                new Point2D(0, 1. / 10),
                new Point2D(2. / 10, 1. / 10),
                new Point2D(3. / 10, 2. / 10),
                new Point2D(7. / 10, 5. / 10),
                new Point2D(4. / 10, 3. / 10),
                new Point2D(3. / 10, 6. / 10),
                new Point2D(1. / 10, 2. / 10),
                new Point2D(9. / 10, 1. / 10)
        )));
        addShape(new ArrayList<>(Arrays.asList(
                new Point2D(1, 1),
                new Point2D(2, 1),
                new Point2D(3, 2),
                new Point2D(5, 5),
                new Point2D(4, 3),
                new Point2D(3, 2),
                new Point2D(1, 2),
                new Point2D(1, 1)
        )));
    }

    public void moveSelectedShape(Axis axis, double newValue) {
        Point3D position = sourceShapes.get(chosenShapeIndex).getPosition();
        switch (axis) {
            case X:
                position.setX(newValue);
                break;
            case Y:
                position.setY(newValue);
                break;
            case Z:
                position.setZ(newValue);
                break;
        }
        scaler.update();
        update();
    }


    public void rotate(double angleX, double angleY) {
        runInBackground(() -> {
            if (rotateShape) {
                if (chosenShapeIndex != -1) {
                    Shape3D shape = sourceShapes.get(chosenShapeIndex);
                    shape.rotate(angleX, angleY);
                    scaler.update();
                }
            } else {
                rotator.rotate(angleX, angleY);
            }
            notifyObservers(EventType.REPAINT);
        });
    }

    public void zoom(int z) {
        runInBackground(() -> {
            povConverter.setzF(Math.exp(z * 0.1));
            notifyObservers(EventType.REPAINT);
        });
    }

    public void update() {
        runInBackground(() -> {
            resultShapes.clear();
            for (Shape3D shape : sourceShapes) {
                resultShapes.add(new Shape2D(shape.getLines()
                        .stream()
                        .map(a -> convertLine(a, true))
                        .collect(Collectors.toList()),
                        shape.getColor(),
                        shape.getWidth()));
            }
            resultShapes.add(new Shape2D(box.getLines()
                    .stream()
                    .map(a -> convertLine(a, false))
                    .collect(Collectors.toList()),
                    box.getColor(),
                    box.getWidth()));
            notifyObservers(EventType.READY);
        });
    }

    private void setBox(Shape3D shape) {
        box = shape;
    }

    public Shape3D addShape(List<Point2D> points) {
        Shape3D shape = new Shape3D(points, this);
        sourceShapes.add(shape);
        scaler.update();
        return shape;
    }

    public void deleteShape(int index) {
        sourceShapes.remove(index);
        scaler.update();
    }


    private Line<Point2D> convertLine(Line<Point3D> line, boolean zoom) {
        Matrix start = PointMatrixConverter.pointToMatrix(line.getStart());
        Matrix end = PointMatrixConverter.pointToMatrix(line.getEnd());

        if (zoom) {
            start = scaler.getMatrix().multiply(start);
            end = scaler.getMatrix().multiply(end);
        }

        start = rotator.getMatrix().multiply(start);
        end = rotator.getMatrix().multiply(end);

        start = cameraConverter.getMatrix().multiply(start);
        end = cameraConverter.getMatrix().multiply(end);

        end = povConverter.getMatrix().multiply(end);
        start = povConverter.getMatrix().multiply(start);

        for (int i = 0; i < 4; i++) {
            start.getMatrix()[i][0] /= start.getMatrix()[3][0];
            end.getMatrix()[i][0] /= end.getMatrix()[3][0];
        }

        Clipper clipper = new Clipper(0, 1,
                -1, 1,
                -1, 1);
        Line<Point3D> l = clipper.getClippedLine(
                PointMatrixConverter.matrixToPoint(start),
                PointMatrixConverter.matrixToPoint(end));
        if (l != null) {
            return new Line<>(new Point2D(l.getStart().getY(), l.getStart().getZ()),
                    new Point2D(l.getEnd().getY(), l.getEnd().getZ()),
                    line.getColor());
        }
        return null;
    }

    public List<Shape2D> getResultShapes() {
        return resultShapes;
    }

    public void updateShapes() {
        runInBackground(() -> {
            sourceShapes.forEach(Shape3D::update);
            scaler.update();
            notifyObservers(EventType.REPAINT);
        });
    }

    public ModelParameters getParameters() {
        return parameters;
    }

    public PovConverter getPovConverter() {
        return povConverter;
    }

    public void setRotateShape(boolean rotateShape) {
        this.rotateShape = rotateShape;
    }

    public List<Shape3D> getSourceShapes() {
        return sourceShapes;
    }

    public int getChosenShapeIndex() {
        return chosenShapeIndex;
    }

    public void setChosenShapeIndex(int chosenShapeIndex) {
        this.chosenShapeIndex = chosenShapeIndex;
    }

    public Point3D getChosenShapePosition() {
        return sourceShapes.get(chosenShapeIndex).getPosition();
    }

    public Shape3D getSelectedShape() {
        if (chosenShapeIndex >= 0) {
            return sourceShapes.get(chosenShapeIndex);
        }
        return null;
    }

    public void init() {
        rotator.setMatrix(new Matrix(new double[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        }));
        update();
    }


    public void loadFromFile(File file) throws FileNotFoundException {
        runInBackground(() -> {
                    try (Scanner scanner = new Scanner(file)) {
                        String[] numbers = readNextNumbers(scanner, 7);
                        parameters.setN(Integer.parseInt(numbers[0]));
                        parameters.setM(Integer.parseInt(numbers[1]));
                        parameters.setK(Integer.parseInt(numbers[2]));
                        parameters.setA(Double.parseDouble(numbers[3]));
                        parameters.setB(Double.parseDouble(numbers[4]));
                        parameters.setC(Double.parseDouble(numbers[5]));
                        parameters.setD(Double.parseDouble(numbers[6]));

                        numbers = readNextNumbers(scanner, 4);

                        povConverter.setzF(Double.parseDouble(numbers[0]));
                        povConverter.setzB(Double.parseDouble(numbers[1]));
                        povConverter.setsW(Double.parseDouble(numbers[2]));
                        povConverter.setsH(Double.parseDouble(numbers[3]));

                        Matrix a = new Matrix(new double[][]{
                                {0, 0, 0, 0},
                                {0, 0, 0, 0},
                                {0, 0, 0, 0},
                                {0, 0, 0, 1},
                        });
                        for (int i = 0; i < 3; i++) {
                            numbers = readNextNumbers(scanner, 3);
                            for (int j = 0; j < 3; j++) {
                                a.getMatrix()[i][j] = Double.parseDouble(numbers[j]);
                            }
                        }
                        rotator.setMatrix(a);
                        numbers = readNextNumbers(scanner, 3);
                        backgroundColor = new Color(Integer.parseInt(numbers[0]),
                                Integer.parseInt(numbers[1]),
                                Integer.parseInt(numbers[2]));
                        numbers = readNextNumbers(scanner, 1);
                        sourceShapes.clear();
                        int numOfShapes = Integer.parseInt(numbers[0]);
//                        sourceShapes = new ArrayList<>(numOfShapes);
                        for (int s = 0; s < numOfShapes; s++) {
                            numbers = readNextNumbers(scanner, 3);
                            Color color = new Color(Integer.parseInt(numbers[0]),
                                    Integer.parseInt(numbers[1]),
                                    Integer.parseInt(numbers[2]));
                            numbers = readNextNumbers(scanner, 3);
                            double xPosition = Double.parseDouble(numbers[0]);
                            double yPosition = Double.parseDouble(numbers[1]);
                            double zPosition = Double.parseDouble(numbers[2]);
                            Matrix r = new Matrix(new double[][]{
                                    {0, 0, 0},
                                    {0, 0, 0},
                                    {0, 0, 0}
                            });
                            for (int i = 0; i < 3; i++) {
                                numbers = readNextNumbers(scanner, 3);
                                for (int j = 0; j < 3; j++) {
                                    r.getMatrix()[i][j] = Double.parseDouble(numbers[j]);
                                }
                            }
                            numbers = readNextNumbers(scanner, 1);
                            int numOfPoints = Integer.parseInt(numbers[0]);
                            List<Point2D> pts = new ArrayList<>(numOfPoints);
                            for (int p = 0; p < numOfPoints; p++) {
                                numbers = readNextNumbers(scanner, 2);
                                pts.add(new Point2D(Double.parseDouble(numbers[0]), Double.parseDouble(numbers[1])));
                            }
                            Shape3D shape = addShape(pts);
                            shape.setColor(color);
                            shape.getPosition().setX(xPosition);
                            shape.getPosition().setY(yPosition);
                            shape.getPosition().setZ(zPosition);
                            shape.setRotation(r);
                        }
                        updateShapes();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        throw new RuntimeException();
                    }
                }
        );

    }

    public void saveToFile(File file) throws IOException {
        Locale.setDefault(Locale.US);
        try (Writer writer = new FileWriter(file)) {
            writer.write(
                    String.format("%d %d %d %f %f %f %f\n",
                            parameters.getN(),
                            parameters.getM(),
                            parameters.getK(),
                            parameters.getA(),
                            parameters.getB(),
                            parameters.getC(),
                            parameters.getD()
                    )
            );
            writer.write(
                    String.format("%f %f %f %f\n",
                            povConverter.getzF(),
                            povConverter.getzB(),
                            povConverter.getsW(),
                            povConverter.getsH()
                    )
            );
            double[][] m = rotator.getMatrix().getMatrix();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    writer.write(m[i][j] + " ");
                }
                writer.write('\n');
            }
            writer.write(
                    String.format("%d %d %d\n",
                            backgroundColor.getRed(),
                            backgroundColor.getGreen(),
                            backgroundColor.getBlue()
                    )
            );
            writer.write(sourceShapes.size() + "\n");
            for (int s = 0; s < sourceShapes.size(); s++) {
                Shape3D shape = sourceShapes.get(s);
                Color color = shape.getColor();
                writer.write(
                        String.format("%d %d %d\n",
                                color.getRed(),
                                color.getGreen(),
                                color.getBlue()
                        )
                );
                Point3D position = shape.getPosition();
                writer.write(
                        String.format("%f %f %f\n",
                                position.getX(),
                                position.getY(),
                                position.getZ()
                        )
                );
                m = shape.getRotation().getMatrix();
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        writer.write(m[i][j] + " ");
                    }
                    writer.write('\n');
                }
                List<Point2D> pts = shape.getNodePoints();
                writer.write(pts.size() + "\n");
                for (Point2D pt : pts) {
                    writer.write(
                            String.format("%f %f\n",
                                    pt.getX(),
                                    pt.getY()
                            )
                    );
                }
            }
        }
    }


    public Color getBackgroundColor() {
        return backgroundColor;
    }

    private String[] readNextNumbers(Scanner scanner, int n) {
        String line;
        do {
            line = scanner.nextLine();
            int commentBegin = line.indexOf("//");
            if (commentBegin != -1) {
                line = line.substring(0, commentBegin);
            }
        } while ("".equals(line));
        String[] numbers = line.split(" ");
        if (numbers.length != n) {
            throw new IllegalArgumentException();
        }
        return numbers;
    }

}
