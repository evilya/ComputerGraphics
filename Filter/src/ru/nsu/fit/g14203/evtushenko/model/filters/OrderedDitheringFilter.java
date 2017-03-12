package ru.nsu.fit.g14203.evtushenko.model.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class OrderedDitheringFilter extends AbstractDitheringFilter {

    private int matrixSize = 4;
    private int[][] matrix;

    public OrderedDitheringFilter(double[] parameters) {
        super(parameters);
        matrix = new int[][]{
                {0, 8, 2, 10},
                {12, 4, 14, 6},
                {3, 11, 1, 9},
                {15, 7, 13, 5},
        };
    }


    @Override
    public BufferedImage apply(BufferedImage source) {
        createMatrix(16);
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
        BufferedImage sourceCopy =
                new BufferedImage(source.getColorModel(),
                        source.copyData(null),
                        source.isAlphaPremultiplied(),
                        null);
        int width = source.getWidth();
        int height = source.getHeight();
        BufferedImage result = new BufferedImage(width, height, source.getType());


        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color oldColor = new Color(sourceCopy.getRGB(x, y));
                int oldRed = oldColor.getRed();
                int oldGreen = oldColor.getGreen();
                int oldBlue = oldColor.getBlue();

                int newRed =
                        ((double) oldRed / 255) >
                                ((double) matrix[y % matrixSize][x % matrixSize] / (matrixSize * matrixSize)) ? 255 : 0;

                int newGreen =
                        ((double) oldGreen / 255) >
                                ((double) matrix[y % matrixSize][x % matrixSize] / (matrixSize * matrixSize))? 255 : 0;

                int newBlue =
                        ((double) oldBlue / 255) >
                                ((double) matrix[y % matrixSize][x % matrixSize] / (matrixSize * matrixSize)) ? 255 : 0;

                Color newColor = new Color(newRed, newGreen, newBlue);
                result.setRGB(x, y, newColor.getRGB());
            }
        }
        return result;
    }

    private void createMatrix(int size) {
        while (matrixSize < size) {
            int[][] result = new int[matrixSize * 2][matrixSize * 2];
            for (int i = 0; i < matrixSize; i++) {
                for (int j = 0; j < matrixSize; j++) {
                    result[i][j] = 4 * matrix[i][j];
                }
            }
            for (int i = 0; i < matrixSize; i++) {
                for (int j = 0; j < matrixSize; j++) {
                    result[i + matrixSize][j] = 4 * matrix[i][j] + 3;
                }
            }
            for (int i = 0; i < matrixSize; i++) {
                for (int j = 0; j < matrixSize; j++) {
                    result[i + matrixSize][j + matrixSize] = 4 * matrix[i][j] + 1;
                }
            }
            for (int i = 0; i < matrixSize; i++) {
                for (int j = 0; j < matrixSize; j++) {
                    result[i][j + matrixSize] = 4 * matrix[i][j] + 2;
                }
            }
            matrixSize *= 2;
            matrix = result;
        }
    }
}
