package ru.nsu.fit.g14203.evtushenko;

import ru.nsu.fit.g14203.evtushenko.model.Cell;
import ru.nsu.fit.g14203.evtushenko.model.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

public class InitView extends JPanel {
	private BufferedImage img;
	private Graphics2D graphics;
	private HexagonDrawer hexagonDrawer;
	private Model model;
	private int cellSize;
	private int lineThickness;
	private int width;
	private int height;
	private int offsetX;
	private int offsetY;

	public InitView(Model model) {
		setModel(model);

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				int maxX = getParent().getWidth();
				int maxY = getParent().getHeight();
				if (img.getRGB(e.getX(), e.getY()) == Color.BLACK.getRGB()) {
					return;
				}
				if (e.getY() >= 0 && e.getY() < maxY && e.getX() >= 0 && e.getX() < maxX) {
					hexagonDrawer.fill(e.getX(), e.getY(), Color.RED);
					Position hex = pixelToHex(e.getX(), e.getY());
					model.getPrevCells()[hex.getY()][hex.getX()].setAlive(true);
					System.out.println(hex.getX() + " " + hex.getY());
					repaint();
				}
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				int rgb = img.getRGB(x, y);
				if (rgb == Color.BLACK.getRGB()) {
					return;
				}
				System.out.println("CLICK: " + x + " " + y + " " + rgb);
				hexagonDrawer.fill(e.getX(), e.getY(), Color.RED);

				Position hex = pixelToHex(x, y);
				System.out.println(hex.getX() + " " + hex.getY());
				repaint();
			}
		});
	}

	public void update() {
		Cell[][] cells = model.getPrevCells();
		for (int j = 0; j < model.getHeight(); j++) {
			int dy = (cellSize + offsetY) * j;
			for (int i = 0; i < model.getWidth() - (j % 2); i++) {
				int dx = lineThickness / 2 + offsetX * (i * 2 + (j % 2));
				if (cells[j][i].isAlive()) {
					hexagonDrawer.fill(offsetX + dx, cellSize + dy, Color.RED);
				}
			}
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(img, 0, 0, null);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}

	public int getCellSize() {
		return hexagonDrawer.getSize();
	}

	public void setSize(int size) {
		hexagonDrawer.setSize(size);
		repaint();
	}

	private Position pixelToHex(int x, int y) {
		int dy = 3 * cellSize / 2;
		int dx = offsetX;

		int q = y / dy * 2 + (y % dy > offsetY ? 1 : 0);
		int p = x / dx;
		int a = 0, b = 0;

		int zeroX = x % dx;
		int zeroY = y % dy;

		switch (q % 4) {
			case 0:
				if (p % 2 == 0) {
					if (zeroY < zeroX * (-offsetY) / offsetX + offsetY) {
						a = p / 2 - 1;
						b = q / 2 - 1;
					} else {
						a = p / 2;
						b = q / 2;
					}
				} else {
					if (zeroY < zeroX * offsetY / offsetX) {
						a = p / 2;
						b = q / 2 - 1;
					} else {
						a = p / 2;
						b = q / 2;
					}
				}
				break;
			case 1:
				a = p / 2;
				b = q / 2;
				break;
			case 2:
				if (p % 2 == 0) {
					if (zeroY < zeroX * offsetY / offsetX) {
						a = p / 2;
						b = q / 2 - 1;
					} else {
						a = p / 2 - 1;
						b = q / 2;
					}
				} else {
					if (zeroY < zeroX * (-offsetY) / offsetX + offsetY) {
						a = p / 2;
						b = q / 2 - 1;
					} else {
						a = p / 2;
						b = q / 2;
					}
				}
				break;
			case 3:
				a = (p - 1) / 2;
				b = q / 2;
				break;
		}
		return new Position(a, b);
	}

	public void setModel(Model model) {
		this.model = model;
		this.offsetX = model.getCellSize() * 866 / 1000;
		this.offsetY = model.getCellSize() / 2;
		this.lineThickness = model.getLineThickness();
		this.height = offsetY * 3 * model.getHeight() + 2 * offsetY;
		this.width = offsetX * 2 * model.getWidth() + lineThickness;
		this.cellSize = model.getCellSize();

		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		graphics = img.createGraphics();
		hexagonDrawer = new HexagonDrawer(img, graphics, lineThickness, cellSize);

		for (int j = 0; j < model.getHeight(); j++) {
			int dy = (cellSize + offsetY) * j;
			for (int i = 0; i < model.getWidth() - (j % 2); i++) {
				int dx = lineThickness / 2 + offsetX * (i * 2 + (j % 2));
				hexagonDrawer.draw(offsetX + dx, cellSize + dy);
			}
		}
	}

}
