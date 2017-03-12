package ru.nsu.fit.g14203.evtushenko.model.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class NegativeFilter implements Filter {
    @Override
    public BufferedImage apply(BufferedImage source) {
        BufferedImage result = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        for (int x = 0; x < source.getWidth(); x++) {
            for (int y = 0; y < source.getHeight(); y++) {
                int sourceRgb = source.getRGB(x, y);
                Color color = new Color(sourceRgb);
                int resultRgb = new Color(255 - color.getRed(),
                        255 - color.getGreen(),
                        255 - color.getBlue(),
                        color.getAlpha())
                        .getRGB();
                result.setRGB(x, y, resultRgb);
            }
        }
        return result;
    }
}
