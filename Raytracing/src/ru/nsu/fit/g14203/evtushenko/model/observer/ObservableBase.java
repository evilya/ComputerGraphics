package ru.nsu.fit.g14203.evtushenko.model.observer;

import java.util.HashMap;
import java.util.Map;

public class ObservableBase implements Observable {
    private Map<ObserverEvent, Map<Integer, Observer>> observers;

    protected ObservableBase() {
        clear();
    }

    private void clear() {
        observers = new HashMap<>();
    }

    @Override
    public int addObserver(ObserverEvent event, Observer handler) {
        Map<Integer, Observer> list = observers.computeIfAbsent(event, k -> new HashMap<>());
        list.put(handler.hashCode(), handler);

        return handler.hashCode();
    }

    @Override
    public void notifyObservers(ObserverEvent event) {
        Map<Integer, Observer> handlers = observers.get(event);
        if (handlers == null) {
            return;
        }

        for (Observer handler : handlers.values()) {
            handler.handle();
        }
    }

}
