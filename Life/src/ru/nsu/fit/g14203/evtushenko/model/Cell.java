package ru.nsu.fit.g14203.evtushenko.model;

public class Cell {
	private boolean alive;
	private double impact;

	public boolean isAlive() {
		return alive;
	}

	public double getImpact() {
		return impact;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public void setImpact(double impact) {
		this.impact = impact;
	}
}
