package ru.nsu.fit.g14203.evtushenko.model.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SobelFilter implements Filter {

    private int threshold;

    public SobelFilter(double[] parameters) {
        threshold = (int) parameters[0];
    }

    @Override
    public BufferedImage apply(BufferedImage source) {
        source = new BlackAndWhiteFilter().apply(source);
        BufferedImage vertical = new Vertical().apply(source);
        BufferedImage horizontal = new Horizontal().apply(source);
        for (int x = 0; x < source.getWidth(); x++) {
            for (int y = 0; y < source.getHeight(); y++) {
                Color colorHorizontal = new Color(horizontal.getRGB(x, y));
                Color colorVertical = new Color(vertical.getRGB(x, y));
//                int gray = Math.sqrt(
//                        Math.pow(colorHorizontal.getRed(), 2) + Math.pow(colorVertical.getRed(), 2))
//                        > threshold ? 255 : 0;
                int gray = colorHorizontal.getRed() + colorVertical.getRed()
                        > threshold ? 255 : 0;
                horizontal.setRGB(x, y, new Color(gray, gray, gray).getRGB());
            }
        }
        return horizontal;
    }

    private class Horizontal extends AbstractMatrixFilter {

        @Override
        public int[][] getFilterMatrix() {
            return new int[][]{
                    {-1, -2, -1},
                    {0, 0, 0},
                    {1, 2, 1}
            };
        }

        @Override
        public double getNormalizer() {
            return 1.;
        }

        @Override
        public int getBrightnessCorrection() {
            return 0;
        }
    }

    private class Vertical extends AbstractMatrixFilter {

        @Override
        public int[][] getFilterMatrix() {
            return new int[][]{
                    {-1, 0, 1},
                    {-2, 0, 2},
                    {-1, 0, 1}
            };
        }

        @Override
        public double getNormalizer() {
            return 1.;
        }

        @Override
        public int getBrightnessCorrection() {
            return 0;
        }
    }
}
