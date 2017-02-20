package ru.nsu.fit.g14203.evtushenko.model;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Model {
	public static void main(String[] args) {
		try {
			Model a = new Model();
			a.update();
			a.update();
			for (int y = 0; y < a.height; y++) {
				if (y % 2 == 1) {
					System.out.print("  ");
				}
				for (int x = 0; x < a.width - y % 2; x++) {
					System.out.printf("%.1f ", a.prevCells[y][x].getImpact());
				}
				System.out.println();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private double firstImpact = 1.;
	private double secondImpact = 0.3;
	private double liveBegin = 2.;
	private double liveEnd = 3.3;
	private double birthBegin = 2.3;
	private double birthEnd = 2.9;


	private int width;
	private int height;
	private int cellSize;
	private int lineThickness;

	private Cell[][] prevCells;

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

	public Model() throws IOException {
		try (Scanner scanner = new Scanner(new FileReader("data/in.txt"))) {
			width = scanner.nextInt();
			height = scanner.nextInt();

			if (width <= 0 || height <= 0) {
				throw new IOException("Incorrect field size");
			}

			initField();

			lineThickness = scanner.nextInt();
			cellSize = scanner.nextInt();

			int aliveCount = scanner.nextInt();
			if (aliveCount < 0) {
				throw new IOException("Incorrect number of alive cells");
			}
			for (int i = 0; i < aliveCount; i++) {
				int x = scanner.nextInt();
				int y = scanner.nextInt();
				if (x < 0 || x + y % 2 >= width || y < 0 || y >= height) {
					throw new IOException("Incorrect alive cell position");
				}
				prevCells[y][x].setAlive(true);
			}
		}

	}

	private void initField() {
		prevCells = new Cell[height][];
		for (int y = 0; y < height; y++) {
			prevCells[y] = new Cell[width - y % 2];
		}
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width - y % 2; x++) {
				prevCells[y][x] = new Cell();
			}
		}
	}

	public void update() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width - y % 2; x++) {
				calculateCellImpact(x, y);
			}
		}
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width - y % 2; x++) {
				double impact = prevCells[y][x].getImpact();
				boolean alive = prevCells[y][x].isAlive();
				if (alive && !(liveBegin <= impact && impact <= liveEnd)) {
					prevCells[y][x].setAlive(false);
				} else if (!alive && birthBegin <= impact && impact <= birthEnd) {
					prevCells[y][x].setAlive(true);
				}
			}
		}
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
			if (prevCells[curY][curX].isAlive()) {
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
			if (prevCells[curY][curX].isAlive()) {
				aliveCount++;
			}
		}
		impact += aliveCount * secondImpact;
		prevCells[y][x].setImpact(impact);
	}
}
