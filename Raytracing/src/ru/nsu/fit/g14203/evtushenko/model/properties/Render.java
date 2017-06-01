package ru.nsu.fit.g14203.evtushenko.model.properties;

import java.awt.*;


public class Render implements Cloneable {
    private Color ambient = new Color(30, 30, 30);
    private double gamma = 1.;
    private int depth = 3;
    private CameraConverter cameraConverter = new CameraConverter();
    private PovConverter povConverter = new PovConverter();

    public Render(CameraConverter cameraConverter, PovConverter povConverter) {
        this.cameraConverter = cameraConverter;
        this.povConverter = povConverter;
    }

    @Override
    public Render clone() throws CloneNotSupportedException {
        Render render = (Render) super.clone();
        render.ambient = ambient;
        render.gamma = gamma;
        render.depth = depth;
        render.cameraConverter = cameraConverter.clone();
        render.povConverter = povConverter.clone();

        return render;
    }

    public Color getAmbient() {
        return ambient;
    }

    public int getDepth() {
        return depth;
    }

    public CameraConverter getCameraConverter() {
        return cameraConverter;
    }

    public PovConverter getPovConverter() {
        return povConverter;
    }
}
