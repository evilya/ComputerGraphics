package ru.nsu.fit.g14203.evtushenko.model.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

import static ru.nsu.fit.g14203.evtushenko.utils.MathUtils.constraint;

public abstract class AbstractMatrixFilter implements Filter {

    public abstract int[][] getFilterMatrix();

    public abstract double getNormalizer();

    public abstract int getBrightnessCorrection();

    @Override
    public BufferedImage apply(BufferedImage source) {

        int[][] filterMatrix = getFilterMatrix();
        double normalizer = getNormalizer();
        int brightnessCorrection = getBrightnessCorrection();

        int width = source.getWidth();
        int height = source.getHeight();
        int matrixOffset = filterMatrix.length / 2;

        BufferedImage result = new BufferedImage(width, height, source.getType());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int resultRed = 0;
                int resultGreen = 0;
                int resultBlue = 0;
                for (int i = 0; i < filterMatrix.length; i++) {
                    for (int j = 0; j < filterMatrix[i].length; j++) {
                        if (filterMatrix[i][j] != 0
                                && y + i - matrixOffset >= 0
                                && y + i - matrixOffset < height
                                && x + j - matrixOffset >= 0
                                && x + j - matrixOffset < width) {
                            Color sourceColor = new Color(source.getRGB(x + j - matrixOffset, y + i - matrixOffset));
                            int r = sourceColor.getRed();
                            int g = sourceColor.getGreen();
                            int b = sourceColor.getBlue();
                            resultRed += filterMatrix[i][j] * r;
                            resultGreen += filterMatrix[i][j] * g;
                            resultBlue += filterMatrix[i][j] * b;
                        }
                    }
                }
                resultRed = constraint((int) (resultRed * normalizer + brightnessCorrection), 0, 255);
                resultGreen = constraint((int) (resultGreen * normalizer + brightnessCorrection), 0, 255);
                resultBlue = constraint((int) (resultBlue * normalizer + brightnessCorrection), 0, 255);

                int resultRgb = new Color(resultRed, resultGreen, resultBlue).getRGB();
                result.setRGB(x, y, resultRgb);
            }
        }
        return result;
    }
}
