package ru.nsu.fit.g14203.evtushenko.model.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

import static ru.nsu.fit.g14203.evtushenko.utils.MathUtils.constraint;

public abstract class AbstractDitheringFilter implements Filter {

    private int redColors;
    private int greenColors;
    private int blueColors;


    public AbstractDitheringFilter(double[] parameters) {
        redColors = (int) parameters[0];
        greenColors = (int) parameters[1];
        blueColors = (int) parameters[2];
    }

    @Override
    public abstract BufferedImage apply(BufferedImage bufferedImage);

    public Color findClosestPaletteColor(int r, int g, int b) {
        int redDiameter = 256 / (redColors - 1);
        int greenDiameter = 256 / (greenColors - 1);
        int blueDiameter = 256 / (blueColors - 1);

        r = (r + redDiameter / 2) / redDiameter * redDiameter;
        g = (g + blueDiameter / 2) / blueDiameter * blueDiameter;
        b = (b + greenDiameter / 2) / greenDiameter * greenDiameter;


        return new Color(constraint(r), constraint(g), constraint(b));
    }

    public int getRedColors() {
        return redColors;
    }

    public int getGreenColors() {
        return greenColors;
    }

    public int getBlueColors() {
        return blueColors;
    }
}
