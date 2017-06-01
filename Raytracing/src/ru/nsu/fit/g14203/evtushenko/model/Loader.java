package ru.nsu.fit.g14203.evtushenko.model;

import ru.nsu.fit.g14203.evtushenko.model.properties.Application;
import ru.nsu.fit.g14203.evtushenko.model.properties.ColorRate;
import ru.nsu.fit.g14203.evtushenko.model.properties.OpticalParameters;
import ru.nsu.fit.g14203.evtushenko.model.shapes.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Loader {
    private final Application application;
    private final File sceneFile;

    public Loader(Application application, File sceneFile) {
        this.application = application;
        this.sceneFile = sceneFile;
    }

    private static String ignoreComments(String line) {
        int index = line.indexOf("//");
        if (index == -1) {
            return line;
        }
        return line.substring(0, index);
    }

    private static String[] nextNumbers(Scanner scanner) {
        String line = "";
        while (line.isEmpty()) {
            if (!scanner.hasNextLine()) {
                return null;
            }
            line = ignoreComments(scanner.nextLine()).trim();
        }
        return line.replaceAll("[ ]{2,}", " ").split(" ");
    }

    public void load() throws IOException {
        if (sceneFile == null) {
            throw new IOException("Bad path");
        }
        loadScene();
    }

    private void loadScene() throws IOException {
        Scene scene = application.getScene();
        scene.clear();
        try (Scanner scanner = new Scanner(sceneFile, "UTF-8")) {
            String[] strData = nextNumbers(scanner);
            int Ar = Integer.parseInt(strData[0]);
            int Ag = Integer.parseInt(strData[1]);
            int Ab = Integer.parseInt(strData[2]);

            scene.setDiffusedLightColor(new Color(Ar, Ag, Ab));

            strData = nextNumbers(scanner);
            int NL = Integer.parseInt(strData[0]);

            for (int i = 0; i < NL; ++i) {
                strData = nextNumbers(scanner);
                double x = Double.parseDouble(strData[0]);
                double y = Double.parseDouble(strData[1]);
                double z = Double.parseDouble(strData[2]);

                int r = Integer.parseInt(strData[3]);
                int g = Integer.parseInt(strData[4]);
                int b = Integer.parseInt(strData[5]);

                LightSource lightSource = new LightSource(new Point3D(x, y, z), new Color(r, g, b));
                scene.addLightSource(lightSource);
            }

            while ((strData = nextNumbers(scanner)) != null) {
                String type = strData[0].toUpperCase();

                Primitive primitive;
                switch (type) {
                    case "TRIANGLE":
                        primitive = parseTriangle(scanner);
                        break;

                    case "QUADRANGLE":
                        primitive = parseQuadrangle(scanner);
                        break;

                    default:
                        throw new IOException();
                }

                scene.addPrimitive(primitive);
            }


        } catch (Exception e) {
            throw new IOException("Incorrect file", e);
        }

        Point3D min = scene.getPrimitives().get(0).getLines().get(0).getMinPoint();
        Point3D max = min;
        for (Primitive primitive : scene.getPrimitives()) {
            for (Line line : primitive.getLines()) {
                min = Point3D.createMin(min, line.getMinPoint());
                max = Point3D.createMax(max, line.getMaxPoint());
            }
        }

        double x = (max.getX() + min.getX()) / 2;
        double y = (max.getY() + min.getY()) / 2;
        double z = (max.getZ() + min.getZ()) / 2;

        List<Primitive> primitiveList = new ArrayList<>();
        for (Primitive primitive : scene.getPrimitives()) {
            if (primitive instanceof Triangle) {
                Triangle triangle = (Triangle) primitive;
                Point3D a = triangle.getA();
                Point3D b = triangle.getB();
                Point3D c = triangle.getC();

                primitiveList.add(new Triangle(
                        new Point3D(a.getX() - x, a.getY() - y, a.getZ() - z),
                        new Point3D(b.getX() - x, b.getY() - y, b.getZ() - z),
                        new Point3D(c.getX() - x, c.getY() - y, c.getZ() - z),
                        triangle.getOpticalParameters()
                ));
            } else if (primitive instanceof Quadrangle) {
                Quadrangle quadrangle = (Quadrangle) primitive;
                Point3D a = quadrangle.getA();
                Point3D b = quadrangle.getB();
                Point3D c = quadrangle.getC();
                Point3D d = quadrangle.getD();

                primitiveList.add(new Quadrangle(
                        new Point3D(a.getX() - x, a.getY() - y, a.getZ() - z),
                        new Point3D(b.getX() - x, b.getY() - y, b.getZ() - z),
                        new Point3D(c.getX() - x, c.getY() - y, c.getZ() - z),
                        new Point3D(d.getX() - x, d.getY() - y, d.getZ() - z),
                        quadrangle.getOpticalParameters()
                ));
            } else {
                primitiveList.add(primitive);
            }
        }

        List<LightSource> lightSourceList = new ArrayList<>();
        for (LightSource lightSource : scene.getLightSources()) {
            Point3D pos = lightSource.getPosition();
            lightSourceList.add(new LightSource(new Point3D(pos.getX() - x, pos.getY() - y, pos.getZ() - z), lightSource.getColor()));
        }

        scene.clear();
        for (Primitive primitive : primitiveList) {
            scene.addPrimitive(primitive);
        }

        for (LightSource lightSource : lightSourceList) {
            scene.addLightSource(lightSource);
        }
    }

    private Primitive parseTriangle(Scanner scanner) {
        String[] strData = nextNumbers(scanner);
        double x = Double.parseDouble(strData[0]);
        double y = Double.parseDouble(strData[1]);
        double z = Double.parseDouble(strData[2]);
        Point3D a = new Point3D(x, y, z);

        strData = nextNumbers(scanner);
        x = Double.parseDouble(strData[0]);
        y = Double.parseDouble(strData[1]);
        z = Double.parseDouble(strData[2]);
        Point3D b = new Point3D(x, y, z);

        strData = nextNumbers(scanner);
        x = Double.parseDouble(strData[0]);
        y = Double.parseDouble(strData[1]);
        z = Double.parseDouble(strData[2]);
        Point3D c = new Point3D(x, y, z);

        strData = nextNumbers(scanner);
        double rRatio = Double.parseDouble(strData[0]);
        double gRatio = Double.parseDouble(strData[1]);
        double bRatio = Double.parseDouble(strData[2]);
        ColorRate diffusionRatio = new ColorRate(rRatio, gRatio, bRatio);

        rRatio = Double.parseDouble(strData[3]);
        gRatio = Double.parseDouble(strData[4]);
        bRatio = Double.parseDouble(strData[5]);
        ColorRate specularRatio = new ColorRate(rRatio, gRatio, bRatio);

        double power = Double.parseDouble(strData[6]);

        OpticalParameters opticalParameters = new OpticalParameters(diffusionRatio, specularRatio, power);

        return new Triangle(a, b, c, opticalParameters);
    }

    private Primitive parseQuadrangle(Scanner scanner) {
        String[] strData = nextNumbers(scanner);
        double x = Double.parseDouble(strData[0]);
        double y = Double.parseDouble(strData[1]);
        double z = Double.parseDouble(strData[2]);
        Point3D a = new Point3D(x, y, z);

        strData = nextNumbers(scanner);
        x = Double.parseDouble(strData[0]);
        y = Double.parseDouble(strData[1]);
        z = Double.parseDouble(strData[2]);
        Point3D b = new Point3D(x, y, z);

        strData = nextNumbers(scanner);
        x = Double.parseDouble(strData[0]);
        y = Double.parseDouble(strData[1]);
        z = Double.parseDouble(strData[2]);
        Point3D c = new Point3D(x, y, z);

        strData = nextNumbers(scanner);
        x = Double.parseDouble(strData[0]);
        y = Double.parseDouble(strData[1]);
        z = Double.parseDouble(strData[2]);
        Point3D d = new Point3D(x, y, z);

        strData = nextNumbers(scanner);
        double rRatio = Double.parseDouble(strData[0]);
        double gRatio = Double.parseDouble(strData[1]);
        double bRatio = Double.parseDouble(strData[2]);
        ColorRate diffusionRatio = new ColorRate(rRatio, gRatio, bRatio);

        rRatio = Double.parseDouble(strData[3]);
        gRatio = Double.parseDouble(strData[4]);
        bRatio = Double.parseDouble(strData[5]);
        ColorRate specularRatio = new ColorRate(rRatio, gRatio, bRatio);

        double power = Double.parseDouble(strData[6]);

        OpticalParameters opticalParameters = new OpticalParameters(diffusionRatio, specularRatio, power);

        return new Quadrangle(a, b, c, d, opticalParameters);
    }

}
