package ru.nsu.fit.g14203.evtushenko.model.properties;

import ru.nsu.fit.g14203.evtushenko.model.Point3D;
import ru.nsu.fit.g14203.evtushenko.model.Scene;
import ru.nsu.fit.g14203.evtushenko.model.observer.ObservableBase;
import ru.nsu.fit.g14203.evtushenko.model.observer.ObserverEvent;

import java.awt.*;


public class Application extends ObservableBase {
    private final Render render;
    private final Scene scene;
    private final Color backgroundColor;
    private final boolean clippingEnabled = true;


    public Application() {
        CameraConverter cameraConverter = new CameraConverter(new Point3D(-10, 0, 0),
                new Point3D(10, 0, 0),
                new Point3D(0, 1, 0));
        PovConverter povConverter = new PovConverter(15, 5, 10, 10);
        render = new Render(cameraConverter, povConverter);
        scene = new Scene();
        backgroundColor = Color.GRAY;
    }

    public boolean isClippingEnabled() {
        return clippingEnabled;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Scene getScene() {
        return scene;
    }

    public Render getRender() {
        return render;
    }

    public enum Event implements ObserverEvent {
        BG_COLOR,
        CLIPPING,
        BOX,
    }
}
