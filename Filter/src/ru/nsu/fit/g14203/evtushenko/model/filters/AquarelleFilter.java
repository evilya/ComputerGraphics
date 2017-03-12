package ru.nsu.fit.g14203.evtushenko.model.filters;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AquarelleFilter implements Filter {
    @Override
    public BufferedImage apply(BufferedImage source) {
        int width = source.getWidth();
        int height = source.getHeight();
        BufferedImage result = new BufferedImage(width, height, source.getType());
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                List<Color> neighbors = new ArrayList<>();
                for (int i = -2; i <= 2; i++) {
                    for (int j = -2; j <= 2; j++) {
                        if (x + i >= 0 && x + i < width && y + j >= 0 && y + j < height) {
                            neighbors.add(new Color(source.getRGB(x + i, y + j)));
                        }
                    }
                }
                neighbors.sort(Comparator.comparingInt(Color::getRed));
                int resultRed = neighbors.get(neighbors.size() / 2).getRed();
                neighbors.sort(Comparator.comparingInt(Color::getGreen));
                int resultGreen = neighbors.get(neighbors.size() / 2).getGreen();
                neighbors.sort(Comparator.comparingInt(Color::getBlue));
                int resultBlue = neighbors.get(neighbors.size() / 2).getBlue();
                result.setRGB(x, y, new Color(resultRed, resultGreen, resultBlue).getRGB());
            }
        }
        Filter filter = new SharpnessFilter();
        result = filter.apply(result);
        return result;
    }
}
