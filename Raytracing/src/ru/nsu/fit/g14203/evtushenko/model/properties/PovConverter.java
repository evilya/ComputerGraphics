package ru.nsu.fit.g14203.evtushenko.model.properties;

import ru.nsu.fit.g14203.evtushenko.model.Matrix;
import ru.nsu.fit.g14203.evtushenko.model.observer.ObservableBase;
import ru.nsu.fit.g14203.evtushenko.model.observer.ObserverEvent;


public class PovConverter extends ObservableBase implements Cloneable {
    private double zF;
    private double zN;
    private double sW;
    private double sH;
    private Matrix matrix;

    public PovConverter() {
    }

    public PovConverter(double zF, double zN, double sW, double sH) {
        this.zF = zF;
        this.zN = zN;
        this.sW = sW;
        this.sH = sH;
        update();
    }

    @Override
    public PovConverter clone() throws CloneNotSupportedException {
        PovConverter povConverter = (PovConverter) super.clone();
        povConverter.zF = zF;
        povConverter.zN = zN;
        povConverter.sW = sW;
        povConverter.sH = sH;
        povConverter.matrix = matrix.clone();
        return povConverter;
    }

    private void update() {
        double zf = -getzN();
        double zb = -getzF();
        double sw = getsW();
        double sh = getsH();
        matrix = new Matrix(4, 4, new double[]{
                2 * zf / sw, 0, 0, 0,
                0, 2 * zf / sh, 0, 0,
                0, 0, zf / (zb - zf), -zf * zb / (zb - zf),
                0, 0, 1, 0
        });
    }

    public Matrix getMatrix() {
        return matrix;
    }

    private double getzF() {
        return zF;
    }

    public double getzN() {
        return zN;
    }

    public void setzN(double zN) {
        this.zN = zN;
        update();
        notifyObservers(Event.ZN);
    }

    public double getsW() {
        return sW;
    }

    public double getsH() {
        return sH;
    }

    public enum Event implements ObserverEvent {
        ZF,
        ZN,
        SWSH,
    }
}
