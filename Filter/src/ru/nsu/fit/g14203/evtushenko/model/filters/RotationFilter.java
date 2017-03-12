package ru.nsu.fit.g14203.evtushenko.model.filters;

import java.awt.image.BufferedImage;

public class RotationFilter implements Filter {

    private double angle;

    public RotationFilter(double[] parameters) {
        angle = Math.toRadians(parameters[0]);
    }

    @Override
    public BufferedImage apply(BufferedImage source) {
        int width = source.getWidth();
        int height = source.getHeight();
        BufferedImage result = new BufferedImage(width, height, source.getType());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int sourceX = (int) (Math.cos(angle) * (x - width / 2) + Math.sin(angle) * (y - height / 2) + width / 2);
                int sourceY = (int) (-Math.sin(angle) * (x - width / 2) + Math.cos(angle) * (y - height / 2) + height / 2);
                if (sourceX < 0 || sourceX >= width || sourceY < 0 || sourceY >= height) {
                    continue;
                }
                result.setRGB(x, y, source.getRGB(sourceX, sourceY));
            }
        }
        return result;
    }

}
