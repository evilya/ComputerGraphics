package ru.nsu.fit.g14203.evtushenko.model.filters;

public class BlurFilter extends AbstractMatrixFilter {
    @Override
    public int[][] getFilterMatrix() {
        return new int[][]{
                {1, 2, 1},
                {2, 4, 2},
                {1, 2, 1}
        };
    }

    @Override
    public double getNormalizer() {
        return 1. / 16;
    }

    @Override
    public int getBrightnessCorrection() {
        return 0;
    }
}
