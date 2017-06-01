package ru.nsu.fit.g14203.evtushenko.model.shapes;

import ru.nsu.fit.g14203.evtushenko.model.Intersection;
import ru.nsu.fit.g14203.evtushenko.model.Matrix;
import ru.nsu.fit.g14203.evtushenko.model.World;
import ru.nsu.fit.g14203.evtushenko.model.properties.OpticalParameters;

import java.util.List;

public interface Primitive {
    World getWorld();

    Intersection getIntersection(Matrix from, Matrix direction);

    List<Line> getLines();

    OpticalParameters getOpticalParameters();

    Primitive rotate(Matrix transform);
}
