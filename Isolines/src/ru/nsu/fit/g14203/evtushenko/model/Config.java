package ru.nsu.fit.g14203.evtushenko.model;

import java.awt.*;

public class Config {
    private int k;
    private int m;
    private Color[] colors;
    private Color borderColor;

    public Config(int k, int m, Color[] colors, Color borderColor) {
        this.k = k;
        this.m = m;
        this.colors = colors;
        this.borderColor = borderColor;
    }

    public int getK() {
        return k;
    }

    public int getM() {
        return m;
    }

    public Color[] getColors() {
        return colors;
    }

    public Color getBorderColor() {
        return borderColor;
    }
}
