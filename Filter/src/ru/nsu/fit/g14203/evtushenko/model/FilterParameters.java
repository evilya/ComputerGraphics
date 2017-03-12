package ru.nsu.fit.g14203.evtushenko.model;

public class FilterParameters {
    private FilterType type;
    private double[] parameters;

    public FilterParameters(FilterType type, double[] parameters) {
        this.type = type;
        this.parameters = parameters;
    }

    public FilterParameters(FilterType type) {
        this.type = type;
    }

    public FilterType getType() {
        return type;
    }

    public double[] getParameters() {
        return parameters;
    }

    public enum FilterType {

        BLACK_AND_WHITE, NEGATIVE, SHARPNESS, STAMPING, BLUR, FLOYD_STEINBERG, ORDERED_DITHERING, AQUARELLE,
        ROBERTS, ROTATION, ZOOM, GAMMA;
    }
}
