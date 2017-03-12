package ru.nsu.fit.g14203.evtushenko.model.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

import static ru.nsu.fit.g14203.evtushenko.utils.MathUtils.constraint;

public class FloydSteinbergFilter extends AbstractDitheringFilter {

    public FloydSteinbergFilter(double[] parameters) {
        super(parameters);
    }

    @Override
    public BufferedImage apply(BufferedImage source) {
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

                Color newColor = findClosestPaletteColor(oldRed, oldGreen, oldBlue);
                int newRed = newColor.getRed();
                int newGreen = newColor.getGreen();
                int newBlue = newColor.getBlue();

                result.setRGB(x, y, newColor.getRGB());

                int redError = oldRed - newRed;
                int greenError = oldGreen - newGreen;
                int blueError = oldBlue - newBlue;

                if (x + 1 < width) {
                    updateError(sourceCopy, x + 1, y, redError, greenError, blueError, 7);
                }
                if (x - 1 >= 0 && y + 1 < height) {
                    updateError(sourceCopy, x - 1, y + 1, redError, greenError, blueError, 3);
                }
                if (y + 1 < height) {
                    updateError(sourceCopy, x, y + 1, redError, greenError, blueError, 5);
                }
                if (x + 1 < width && y + 1 < height) {
                    updateError(sourceCopy, x + 1, y + 1, redError, greenError, blueError, 1);
                }
            }
        }
        return result;
    }

    private void updateError(BufferedImage image, int x, int y, int redError, int greenError, int blueError, int mult) {
        Color oldColor = new Color(image.getRGB(x, y));
        image.setRGB(x, y,
                new Color(constraint(oldColor.getRed() + redError * mult / 16),
                        constraint(oldColor.getGreen() + greenError * mult / 16),
                        constraint(oldColor.getBlue() + blueError * mult / 16)).getRGB());
    }

}
