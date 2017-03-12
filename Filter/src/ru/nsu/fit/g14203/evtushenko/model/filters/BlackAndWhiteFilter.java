package ru.nsu.fit.g14203.evtushenko.model.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BlackAndWhiteFilter implements Filter {
    @Override
    public BufferedImage apply(BufferedImage source) {
        BufferedImage result = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < source.getWidth(); x++) {
            for (int y = 0; y < source.getHeight(); y++) {
                int sourceRgb = source.getRGB(x, y);
                Color color = new Color(sourceRgb);
                int gray = (int) (0.299 * color.getRed()
                        + 0.587 * color.getGreen()
                        + 0.144 * color.getBlue());
                gray = Math.min(gray, 255);
                int resultRgb = new Color(gray, gray, gray, color.getAlpha()).getRGB();
                result.setRGB(x, y, resultRgb);
            }
        }
        return result;
    }
}
