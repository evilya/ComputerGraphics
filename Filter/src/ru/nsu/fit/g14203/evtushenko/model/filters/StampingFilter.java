package ru.nsu.fit.g14203.evtushenko.model.filters;

import java.awt.image.BufferedImage;

public class StampingFilter extends AbstractMatrixFilter {
    @Override
    public int[][] getFilterMatrix() {
        return new int[][]{
                {0, 1, 0},
                {-1, 0, 1},
                {0, -1, 0}
        };
    }

    @Override
    public BufferedImage apply(BufferedImage source) {
        source = new BlackAndWhiteFilter().apply(source);
        return super.apply(source);
    }

    @Override
    public double getNormalizer() {
        return 1.;
    }

    @Override
    public int getBrightnessCorrection() {
        return 128;
    }
}
