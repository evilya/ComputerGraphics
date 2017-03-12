package ru.nsu.fit.g14203.evtushenko.model.filters;

import java.awt.image.BufferedImage;

public class ZoomFilter implements Filter {
    @Override
    public BufferedImage apply(BufferedImage source) {
        int width = source.getWidth();
        int height = source.getHeight();
        BufferedImage result = new BufferedImage(width, height, source.getType());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = source.getRGB(x / 2 + width / 4, y / 2 + height / 4);
                result.setRGB(x, y, rgb);
            }
        }
        return result;
    }
}
