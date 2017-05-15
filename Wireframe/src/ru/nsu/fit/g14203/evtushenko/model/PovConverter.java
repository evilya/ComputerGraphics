package ru.nsu.fit.g14203.evtushenko.model;

import ru.nsu.fit.g14203.evtushenko.math.Matrix;
import sun.misc.resources.Messages_pt_BR;

public class PovConverter {
    private final Model model;
    private double sW = 1.;
    private double sH = 1.;
    private double zB = 11.5;
    private double zF = 1;

    public PovConverter(Model model) {
        this.model = model;
    }

    private Matrix ortho = new Matrix(new double[][]{
            {zB / (zB - zF), 0, 0, -zB * zF / (zB - zF)},
            {0, 2 * zF / sW, 0, 0},
            {0, 0, 2 * zF / sH, 0},
            {1, 0, 0, 0}
    });

    public double getsW() {
        return sW;
    }

    public void setsW(double sW) {
        this.sW = sW;
        reinitialize();
    }

    public double getsH() {
        return sH;
    }

    public void setsH(double sH) {
        this.sH = sH;
        reinitialize();
    }

    public double getzB() {
        return zB;
    }

    public void setzB(double zB) {
        this.zB = zB;
        reinitialize();
    }

    public double getzF() {
        return zF;
    }

    public void setzF(double zF) {
        this.zF = zF;
        reinitialize();
    }

    public Matrix getMatrix() {
        return ortho;
    }

    private void reinitialize() {
        ortho = new Matrix(new double[][]{
                {zB / (zB - zF), 0, 0, -zB * zF / (zB - zF)},
                {0, 2 * zF / sW, 0, 0},
                {0, 0, 2 * zF / sH, 0},
                {1, 0, 0, 0}
        });
        model.update();
    }
}
