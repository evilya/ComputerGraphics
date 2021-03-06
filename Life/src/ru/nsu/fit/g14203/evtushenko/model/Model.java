package ru.nsu.fit.g14203.evtushenko.model;

import ru.nsu.fit.g14203.evtushenko.EventType;
import ru.nsu.fit.g14203.evtushenko.Observable;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.stream.Stream;

public class Model extends Observable {
	private double firstImpact = 1.;
	private double secondImpact = 0.3;
	private double liveBegin = 2.;
	private double liveEnd = 3.3;
	private double birthBegin = 2.3;
	private double birthEnd = 2.9;


	private boolean xorFill = false;
	private int width;
	private int height;
	private int cellSize;
	private int lineThickness;

	private Cell[][] cells;

	private int[][][] firstImpactOffsets = {
			{{1, 0}, {0, -1}, {-1, -1},
					{-1, 0}, {-1, 1}, {0, 1}},
			{{1, 0}, {1, -1}, {0, -1},
					{-1, 0}, {0, 1}, {1, 1}}
	};

	private int[][][] secondImpactOffsets = {
			{{-2, -1}, {-2, 1}, {0, -2},
					{0, 2}, {1, -1}, {1, 1}},
			{{-1, -1}, {-1, 1}, {0, -2},
					{0, 2}, {2, 1}, {2, -1}}
	};

	public Model() {
		width = 10;
		height = 7;
		lineThickness = 2;
		cellSize = 25;
		createEmptyField(width, height);
	}

	public void loadFromFile(String file) throws IOException {
		try (Scanner scanner = new Scanner(new File(file))) {
			int[] values = getNextInts(scanner);
			width = values[0];
			height = values[1];
			if (width <= 0 || height <= 0) {
				throw new IOException("Incorrect field size");
			}
			createEmptyField(width, height);

			values = getNextInts(scanner);
			lineThickness = values[0];

			values = getNextInts(scanner);
			cellSize = values[0];

			values = getNextInts(scanner);
			int aliveCount = values[0];
			if (aliveCount < 0) {
				throw new IOException("Incorrect number of alive cells");
			}
			for (int i = 0; i < aliveCount; i++) {
				values = getNextInts(scanner);
				int x = values[0];
				int y = values[1];
				if (x < 0 || x + y % 2 >= width || y < 0 || y >= height) {
					throw new IOException("Incorrect alive cell position");
				}
				cells[y][x].setAlive(true);
			}
			notifyObservers(EventType.RESIZE);

		}

//		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
//			String line = reader.readLine();
//			Scanner scanner = new Scanner(line);
//			width = scanner.nextInt();
//			height = scanner.nextInt();
//
//			if (width <= 0 || height <= 0) {
//				throw new IOException("Incorrect field size");
//			}
//
//			createEmptyField(width, height);
//
//			line = reader.readLine();
//			scanner = new Scanner(line);
//			lineThickness = scanner.nextInt();
//
//			line = reader.readLine();
//			scanner = new Scanner(line);
//			cellSize = scanner.nextInt();
//
//			line = reader.readLine();
//			scanner = new Scanner(line);
//			int aliveCount = scanner.nextInt();
//			if (aliveCount < 0) {
//				throw new IOException("Incorrect number of alive cells");
//			}
//			for (int i = 0; i < aliveCount; i++) {
//				line = reader.readLine();
//				scanner = new Scanner(line);
//				int x = scanner.nextInt();
//				int y = scanner.nextInt();
//				if (x < 0 || x + y % 2 >= width || y < 0 || y >= height) {
//					throw new IOException("Incorrect alive cell position");
//				}
//				cells[y][x].setAlive(true);
//			}
//			notifyObservers(EventType.RESIZE);
//		}

	}

	private int[] getNextInts(Scanner scanner) {
		String line;
		do {
			line = scanner.nextLine();
			int commentStart = line.indexOf("//");
			if (commentStart != -1) {
				line = line.substring(0, commentStart).trim();
			}
		} while ("".equals(line));
		String[] values = line.split(" ");
		return Stream.of(values).mapToInt(Integer::parseInt).toArray();
	}

	public void createEmptyField(int width, int height) {
		this.width = width;
		this.height = height;
		cells = new Cell[height][];
		for (int y = 0; y < height; y++) {
			cells[y] = new Cell[width - y % 2];
		}
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width - y % 2; x++) {
				cells[y][x] = new Cell();
			}
		}
		notifyObservers(EventType.RESIZE);
	}

	public Cell[][] getCells() {
		return cells;
	}

	public void setCellState(int x, int y, boolean alive) {
		if (x < 0 || x + y % 2 >= width || y < 0 || y >= height) {
			throw new RuntimeException("Incorrect cell position");
		}
		cells[y][x].setAlive(alive);
		updateImpacts();
		notifyObservers(EventType.UPDATE_CELLS);
	}

	public boolean getCellState(int x, int y) {
		if (x < 0 || x + y % 2 >= width || y < 0 || y >= height) {
			throw new RuntimeException("Incorrect cell position");
		}
		return cells[y][x].isAlive();
	}

	public double getCellImpact(int x, int y) {
		if (x < 0 || x + y % 2 >= width || y < 0 || y >= height) {
			throw new RuntimeException("Incorrect cell position");
		}
		return cells[y][x].getImpact();
	}

	public void updateImpacts() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width - y % 2; x++) {
				calculateCellImpact(x, y);
			}
		}
	}

	public void next() {
		updateImpacts();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width - y % 2; x++) {
				double impact = cells[y][x].getImpact();
				boolean alive = cells[y][x].isAlive();
				if (alive && !(liveBegin <= impact && impact <= liveEnd)) {
					cells[y][x].setAlive(false);
				} else if (!alive && birthBegin <= impact && impact <= birthEnd) {
					cells[y][x].setAlive(true);
				}
			}
		}
		notifyObservers(EventType.UPDATE_CELLS);
	}

	public void clear() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width - y % 2; x++) {
				cells[y][x].setAlive(false);
			}
		}
		updateImpacts();
		notifyObservers(EventType.UPDATE_CELLS);
	}

	private void calculateCellImpact(final int x, final int y) {
		int odd = y % 2;
		int aliveCount = 0;
		double impact = 0.;
		for (int i = 0; i < firstImpactOffsets[odd].length; i++) {
			int[] offset = firstImpactOffsets[odd][i];
			int curX = x + offset[0];
			int curY = y + offset[1];
			if (curX + curY % 2 >= width || curX < 0 || curY >= height || curY < 0) {
				continue;
			}
			if (cells[curY][curX].isAlive()) {
				aliveCount++;
			}
		}
		impact += firstImpact * aliveCount;
		aliveCount = 0;
		for (int i = 0; i < secondImpactOffsets[odd].length; i++) {
			int[] offset = secondImpactOffsets[odd][i];
			int curX = x + offset[0];
			int curY = y + offset[1];
			if (curX + curY % 2 >= width || curX < 0 || curY >= height || curY < 0) {
				continue;
			}
			if (cells[curY][curX].isAlive()) {
				aliveCount++;
			}
		}
		impact += aliveCount * secondImpact;
		cells[y][x].setImpact(impact);
	}

	public double getFirstImpact() {
		return firstImpact;
	}

	public void setFirstImpact(double firstImpact) {
		this.firstImpact = firstImpact;
	}

	public double getSecondImpact() {
		return secondImpact;
	}

	public void setSecondImpact(double secondImpact) {
		this.secondImpact = secondImpact;
	}

	public double getLiveBegin() {
		return liveBegin;
	}

	public void setLiveBegin(double liveBegin) {
		this.liveBegin = liveBegin;
	}

	public double getLiveEnd() {
		return liveEnd;
	}

	public void setLiveEnd(double liveEnd) {
		this.liveEnd = liveEnd;
	}

	public double getBirthBegin() {
		return birthBegin;
	}

	public void setBirthBegin(double birthBegin) {
		this.birthBegin = birthBegin;
	}

	public double getBirthEnd() {
		return birthEnd;
	}

	public void setBirthEnd(double birthEnd) {
		this.birthEnd = birthEnd;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setWidthHeight(int newWidth, int newHeight) {
		Cell[][] newCells = new Cell[newHeight][];
		for (int y = 0; y < newHeight; y++) {
			newCells[y] = new Cell[newWidth - y % 2];
		}
		for (int y = 0; y < newHeight; y++) {
			for (int x = 0; x < newWidth - y % 2; x++) {
				if (y < height && x < width - y % 2) {
					newCells[y][x] = cells[y][x];
				} else {
					newCells[y][x] = new Cell();
				}
			}
		}
		cells = newCells;
		this.width = newWidth;
		this.height = newHeight;
		notifyObservers(EventType.RESIZE);
	}

	public int getCellSize() {
		return cellSize;
	}

	public void setCellSize(int cellSize) {
		this.cellSize = cellSize;
		notifyObservers(EventType.RESIZE);
	}

	public int getLineThickness() {
		return lineThickness;
	}

	public void setLineThickness(int lineThickness) {
		this.lineThickness = lineThickness;
		notifyObservers(EventType.RESIZE);
	}

	public boolean isXorFill() {
		return xorFill;
	}

	public void setXorFill(boolean xorFill) {
		this.xorFill = xorFill;
	}

}
