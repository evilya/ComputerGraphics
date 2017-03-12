package ru.nsu.fit.g14203.evtushenko.model.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RobertsFilter implements Filter {

    private int threshold;

    public RobertsFilter(double[] parameters) {
        threshold = (int) parameters[0];
    }

    @Override
    public BufferedImage apply(BufferedImage source) {
        source = new BlackAndWhiteFilter().apply(source);
        int width = source.getWidth();
        int height = source.getHeight();

        BufferedImage result = new BufferedImage(width, height, source.getType());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int gray = 0;
                if (x + 1 < width && y + 1 < height) {
                    Color topLeft = new Color(source.getRGB(x, y));
                    Color topRight = new Color(source.getRGB(x + 1, y));
                    Color bottomLeft = new Color(source.getRGB(x, y + 1));
                    Color bottomRight = new Color(source.getRGB(x + 1, y + 1));
                    gray = Math.abs(topLeft.getRed() - bottomRight.getRed())
                            + Math.abs(topRight.getRed() - bottomLeft.getRed());

                }
                gray = gray > threshold ? 255 : 0;
                int resultRgb = new Color(gray, gray, gray).getRGB();
                result.setRGB(x, y, resultRgb);
            }
        }
        return result;
    }
}
