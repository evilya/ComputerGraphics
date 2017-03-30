package ru.nsu.fit.g14203.evtushenko.model;

import java.awt.*;

public class Config {
    private int k;
    private int m;
    private Color[] colors;

    public Config(int k, int m, Color[] colors) {
        this.k = k;
        this.m = m;
        this.colors = colors;
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
}
