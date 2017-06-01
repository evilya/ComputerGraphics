package ru.nsu.fit.g14203.evtushenko.model.observer;

public interface Observable {
    int addObserver(ObserverEvent event, Observer handler);

    void notifyObservers(ObserverEvent event);

}
