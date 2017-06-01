package ru.nsu.fit.g14203.evtushenko.model;

import ru.nsu.fit.g14203.evtushenko.model.observer.Observable;
import ru.nsu.fit.g14203.evtushenko.model.observer.ObservableBase;
import ru.nsu.fit.g14203.evtushenko.model.observer.ObserverEvent;
import ru.nsu.fit.g14203.evtushenko.model.shapes.LightSource;
import ru.nsu.fit.g14203.evtushenko.model.shapes.Primitive;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class Scene extends ObservableBase implements Observable {
    private final World world = new World();
    private final List<Primitive> primitives = new ArrayList<>();
    private final List<LightSource> lightSources = new ArrayList<>();
    private final List<Primitive> rotatedPrimitives = new ArrayList<>();
    private final List<LightSource> rotatedLightSources = new ArrayList<>();
    private Color diffusedLightColor = Color.WHITE;

    public Scene() {
    }

    public void clear() {
        primitives.clear();
        lightSources.clear();
        rotatedPrimitives.clear();
        rotatedLightSources.clear();
        notifyObservers(Event.SCENE);
    }

    public List<Primitive> getPrimitives() {
        return primitives;
    }

    public void addPrimitive(Primitive primitive) {
        primitives.add(primitive);
        notifyObservers(Event.SCENE);
    }

    public World getWorld() {
        return world;
    }

    public List<LightSource> getLightSources() {
        return lightSources;
    }

    public void addLightSource(LightSource lightSource) {
        lightSources.add(lightSource);
        notifyObservers(Event.LIGHT_SRC);
    }

    public Color getDiffusedLightColor() {
        return diffusedLightColor;
    }

    public void setDiffusedLightColor(Color diffusedLightColor) {
        this.diffusedLightColor = diffusedLightColor;
        notifyObservers(Event.LIGHT_COLOR);
    }

    public List<Primitive> getRotatedPrimitives() {
        return rotatedPrimitives;
    }

    public List<LightSource> getRotatedLightSources() {
        return rotatedLightSources;
    }

    public void rotate(Matrix transformMatrix) {
        rotatedPrimitives.clear();
        rotatedLightSources.clear();

        rotatedPrimitives.addAll(primitives.stream()
                .filter(Objects::nonNull)
                .map(e -> e.rotate(transformMatrix))
                .collect(Collectors.toList()));

        rotatedLightSources.addAll(lightSources.stream()
                .filter(Objects::nonNull)
                .map(e -> e.rotate(transformMatrix))
                .collect(Collectors.toList()));
    }

    public enum Event implements ObserverEvent {
        SCENE,
        LIGHT_COLOR,
        LIGHT_SRC,
    }
}
