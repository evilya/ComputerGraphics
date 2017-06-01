package ru.nsu.fit.g14203.evtushenko.model.properties;


public class OpticalParameters {
    private final ColorRate diffusion;
    private final ColorRate specularity;
    private final double power;

    public OpticalParameters(ColorRate diffusion, ColorRate specularity, double power) {
        this.diffusion = diffusion;
        this.specularity = specularity;
        this.power = power;
    }

    public ColorRate getD() {
        return diffusion;
    }

    public ColorRate getS() {
        return specularity;
    }

}
