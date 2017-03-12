package ru.nsu.fit.g14203.evtushenko.model.filters;

public class SharpnessFilter extends AbstractMatrixFilter {

    @Override
    public int[][] getFilterMatrix() {
        return new int[][]{
                {0, -1, 0},
                {-1, 5, -1},
                {0, -1, 0}
        };
    }

    @Override
    public double getNormalizer() {
        return 1.;
    }

    @Override
    public int getBrightnessCorrection() {
        return 0;
    }
}
