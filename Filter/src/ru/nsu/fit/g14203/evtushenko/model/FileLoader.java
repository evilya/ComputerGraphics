package ru.nsu.fit.g14203.evtushenko.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileLoader {

    private final List<Absorption> absorptionPoints = new ArrayList<>();

    private final List<Emission> emissionPoints = new ArrayList<>();

    private double[] absorption = new double[101];
    private int[][] emission = new int[101][3];

    public FileLoader(String path) {
        try (Scanner scanner = new Scanner(new File(path))) {
            readAbsorptions(scanner);
            readEmissions(scanner);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Absorption prevAbs = new Absorption(-1, 0.0);
        for (Absorption cur : absorptionPoints) {
            if (cur.x != prevAbs.x) {
                absorption[cur.x] = cur.value;
                for (int i = 1; i < cur.x - prevAbs.x; i++) {
                    double increment = (cur.value - prevAbs.value) / (cur.x - prevAbs.x);
                    absorption[i + prevAbs.x] = prevAbs.value + i * increment;
                }
            }
            prevAbs = cur;
        }
        Emission prevEm = new Emission(-1, 0, 0, 0);
        for (Emission cur : emissionPoints) {
            if (cur.x != prevEm.x) {
                emission[cur.x][0] = cur.red;
                emission[cur.x][1] = cur.green;
                emission[cur.x][2] = cur.blue;
                for (int i = 1; i < cur.x - prevEm.x; i++) {
                    double redIncrement = (double) (cur.red - prevEm.red) / (cur.x - prevEm.x);
                    double greenIncrement = (double)(cur.green - prevEm.green) / (cur.x - prevEm.x);
                    double blueIncrement = (double)(cur.blue - prevEm.blue) / (cur.x - prevEm.x);
                    emission[i + prevEm.x][0] = (int) (prevEm.red + i * redIncrement);
                    emission[i + prevEm.x][1] = (int) (prevEm.green + i * greenIncrement);
                    emission[i + prevEm.x][2] = (int) (prevEm.blue + i * blueIncrement);
                }
            }
            prevEm = cur;
        }
    }

    public List<Absorption> getAbsorptionPoints() {
        return absorptionPoints;
    }

    public List<Emission> getEmissionPoints() {
        return emissionPoints;
    }

    public double[] getAbsorption() {
        return absorption;
    }

    public int[][] getEmission() {
        return emission;
    }

    private void readAbsorptions(Scanner scanner) {
        int numOfAbs = Integer.parseInt(readNextNumbers(scanner, 1)[0]);
        if (numOfAbs < 2) {
            throw new IllegalArgumentException();
        }
        Absorption absorption = readNextAbsorption(scanner);
        if (absorption.x != 0) {
            throw new IllegalArgumentException();
        }
        absorptionPoints.add(absorption);
        for (int i = 0; i < numOfAbs - 2; i++) {
            absorptionPoints.add(readNextAbsorption(scanner));
        }
        absorption = readNextAbsorption(scanner);
        if (absorption.x != 100) {
            throw new IllegalArgumentException();
        }
        absorptionPoints.add(absorption);
    }

    private void readEmissions(Scanner scanner) {
        int numOfEmissions = Integer.parseInt(readNextNumbers(scanner, 1)[0]);
        if (numOfEmissions < 2) {
            throw new IllegalArgumentException();
        }
        Emission emission = readNextEmission(scanner);
        if (emission.x != 0) {
            throw new IllegalArgumentException();
        }
        emissionPoints.add(emission);
        for (int i = 0; i < numOfEmissions - 2; i++) {
            emissionPoints.add(readNextEmission(scanner));
        }
        emission = readNextEmission(scanner);
        if (emission.x != 100) {
            throw new IllegalArgumentException();
        }
        emissionPoints.add(emission);
    }

    private Absorption readNextAbsorption(Scanner scanner) {
        String[] numbers = readNextNumbers(scanner, 2);
        int x = Integer.parseInt(numbers[0]);
        double value = Double.parseDouble(numbers[1]);
        if (x < 0 || x > 100 || value < 0 || value > 1) {
            throw new IllegalArgumentException();
        }
        return new Absorption(x, value);
    }

    private Emission readNextEmission(Scanner scanner) {
        String[] numbers = readNextNumbers(scanner, 4);
        int x = Integer.parseInt(numbers[0]);
        int red = Integer.parseInt(numbers[1]);
        int green = Integer.parseInt(numbers[2]);
        int blue = Integer.parseInt(numbers[3]);
        if (x < 0 || x > 100
                || red < 0 || red > 255
                || green < 0 || green > 255
                || blue < 0 || blue > 255) {
            throw new IllegalArgumentException();
        }
        return new Emission(x, red, green, blue);
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
