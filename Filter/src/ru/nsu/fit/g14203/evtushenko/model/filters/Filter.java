package ru.nsu.fit.g14203.evtushenko.model.filters;

import java.awt.image.BufferedImage;
import java.util.function.UnaryOperator;

public interface Filter extends UnaryOperator<BufferedImage> {

    @Override
    BufferedImage apply(BufferedImage bufferedImage);
}
