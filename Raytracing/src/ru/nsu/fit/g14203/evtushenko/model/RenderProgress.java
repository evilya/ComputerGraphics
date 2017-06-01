package ru.nsu.fit.g14203.evtushenko.model;

import java.awt.image.BufferedImage;


public class RenderProgress {
    private final State state;
    private double percent;
    private BufferedImage image;

    public RenderProgress(double percent, State state) {
        this.percent = percent;
        this.state = state;
    }

    public RenderProgress(State state) {
        this.state = state;
    }

    public RenderProgress(double percent, State state, BufferedImage image) {
        this.percent = percent;
        this.state = state;
        this.image = image;
    }

    public double getPercent() {
        return percent;
    }

    public State getState() {
        return state;
    }

    public BufferedImage getImage() {
        return image;
    }

    public enum State {
        RENDERING,
        READY,
        CANCELED,
    }
}
