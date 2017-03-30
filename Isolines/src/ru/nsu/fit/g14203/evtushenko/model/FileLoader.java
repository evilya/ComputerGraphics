package ru.nsu.fit.g14203.evtushenko.model;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileLoader {
    public static Config readConfig(String pathname) {
        try (Scanner scanner = new Scanner(new File(pathname))) {
            String[] numbers = readNextNumbers(scanner, 2);
            int k = Integer.parseInt(numbers[0]);
            int m = Integer.parseInt(numbers[1]);
            numbers = readNextNumbers(scanner, 1);
            int n = Integer.parseInt(numbers[0]);
            Color[] colors = new Color[n + 1];
            for (int i = 0; i < n + 1; i++) {
                numbers = readNextNumbers(scanner, 3);
                int r = Integer.parseInt(numbers[0]);
                int g = Integer.parseInt(numbers[1]);
                int b = Integer.parseInt(numbers[2]);
                colors[i] = new Color(r, g, b);
            }
            return new Config(k, m, colors);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException();
        }
    }

    private static String[] readNextNumbers(Scanner scanner, int n) {
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
