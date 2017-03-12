package ru.nsu.fit.g14203.evtushenko.model.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GammaFilter implements Filter {

    private double value;

    public GammaFilter(double[] parameters) {
        value = parameters[0];
    }

    @Override
    public BufferedImage apply(BufferedImage source) {
        int width = source.getWidth();
        int height = source.getHeight();
        BufferedImage result = new BufferedImage(width, height, source.getType());
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color color = new Color(source.getRGB(x,y));
                int newRed = (int) (255 * Math.pow((double)color.getRed()/255, value));
                int newGreen = (int) (255 * Math.pow((double)color.getGreen()/255, value));
                int newBlue = (int) (255 * Math.pow((double)color.getBlue()/255, value));
                result.setRGB(x, y, new Color(newRed, newGreen, newBlue).getRGB());
            }
        }
        return result;
    }
}
