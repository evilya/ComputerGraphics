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
        BufferedImage result = new BufferedImage(350, 350, source.getType());

        double offsetX = (double) (width - 1) / 2.;
        double offsetY = (double) (height - 1) / 2.;
        for (int x = 0; x < 350; x++) {
            for (int y = 0; y < 350; y++) {
                int sourceX = (int) Math.round(Math.cos(angle) * ((double) x - offsetX) + Math.sin(angle) * ((double) y - offsetY) + offsetX);
                int sourceY = (int) Math.round(-Math.sin(angle) * ((double) x - offsetX) + Math.cos(angle) * ((double) y - offsetY) + offsetY);
                if (sourceX < 0 || sourceX >= width || sourceY < 0 || sourceY >= height) {
                    continue;
                }
                result.setRGB(x, y, source.getRGB(sourceX, sourceY));
            }
        }
        System.out.println(result.getRGB(0, 0));
        System.out.println(result.getRGB(0, 349));
        return result;
    }

}
