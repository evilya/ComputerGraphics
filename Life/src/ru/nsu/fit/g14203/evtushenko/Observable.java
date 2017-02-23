package ru.nsu.fit.g14203.evtushenko;

import java.util.ArrayList;
import java.util.List;

public class Observable {

	private List<Observer> observers = new ArrayList<>();

	public void addObserver(Observer observer) {
		observers.add(observer);
	}

	public void removeObserver(Observer observer) {
		observers.remove(observer);
	}

	public void notifyObservers(EventType type) {
		observers.forEach(o -> o.handle(type));
	}

}
