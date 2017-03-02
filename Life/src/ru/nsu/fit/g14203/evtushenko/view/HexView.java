package ru.nsu.fit.g14203.evtushenko.view;

import ru.nsu.fit.g14203.evtushenko.EventType;
import ru.nsu.fit.g14203.evtushenko.Observer;
import ru.nsu.fit.g14203.evtushenko.model.Model;
import ru.nsu.fit.g14203.evtushenko.model.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

public class HexView extends JPanel implements Observer {
	private final Color backgroundColor = Color.WHITE;
	private final Color cellColor = Color.GREEN;
	private final Color borderColor = Color.BLACK;
	private final Font impactFont = new Font("TimesRoman", Font.PLAIN, 12);
	private final DecimalFormat impactFormat = new DecimalFormat("#.#");

	private Model model;
	private BufferedImage img;
	private HexagonDrawer hexagonDrawer;

	private int width;
	private int height;
	private int cellSize;
	private int lineThickness;
	private int offsetX;
	private int offsetY;
	private boolean showImpact = false;

	private int lastFillX = -1;
	private int lastFillY = -1;

	public HexView(Model model) {
		this.model = model;
		updateSize();
		setBackground(backgroundColor);
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (model.isXorFill()) {
					checkAndFillXor(e);
				} else {
					checkAndFill(e);
				}
			}

		});
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				lastFillX = -1;
				lastFillY = -1;
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (model.isXorFill()) {
					checkAndFillXor(e);
				} else {
					checkAndFill(e);
				}
			}
		});
	}

	public void updateCells() {
		for (int j = 0; j < model.getHeight(); j++) {
			int dy = (cellSize + offsetY) * j;
			for (int i = 0; i < model.getWidth() - (j % 2); i++) {
				int dx = lineThickness / 2 + offsetX * (i * 2 + (j % 2));
				if (model.getCellState(i, j)) {
					hexagonDrawer.fill(offsetX + dx, cellSize + dy, cellColor);
				} else {
					if (img.getRGB(offsetX + dx, cellSize + dy) == cellColor.getRGB()) {
						hexagonDrawer.fill(offsetX + dx, cellSize + dy, backgroundColor);
					}
				}
			}
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(img, 0, 0, null);
		g.setColor(Color.BLACK);
		g.setFont(impactFont);
		if (showImpact && cellSize >= 20) {
			model.updateImpacts();
			for (int j = 0; j < model.getHeight(); j++) {
				int dy = (cellSize + offsetY) * j;
				for (int i = 0; i < model.getWidth() - (j % 2); i++) {
					int dx = lineThickness / 2 + offsetX * (i * 2 + (j % 2));
					String impact = impactFormat.format(model.getCellImpact(i, j));
					int len = impact.replaceAll(",", "").length();
					g.drawString(impact,
							offsetX + dx - len * 4,
							cellSize + dy + 6);
				}
			}
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}

	public void updateSize() {
		this.cellSize = model.getCellSize();
		this.lineThickness = model.getLineThickness();

		this.offsetX = cellSize * 866 / 1000; //sin(60)
		this.offsetY = cellSize / 2; //sin(30)

		this.height = (cellSize + offsetY) * model.getHeight() + cellSize / 2 + lineThickness + 5;
		this.width = offsetX * 2 * model.getWidth() + lineThickness;

		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = img.createGraphics();
		hexagonDrawer = new HexagonDrawer(img, graphics, lineThickness, cellSize);

		for (int j = 0; j < model.getHeight(); j++) {
			int dy = (cellSize + offsetY) * j + lineThickness / 2;
			for (int i = 0; i < model.getWidth() - (j % 2); i++) {
				int dx = lineThickness / 2 + offsetX * (i * 2 + (j % 2));
				hexagonDrawer.draw(offsetX + dx, cellSize + dy);
			}
		}
		updateCells();
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
				if (p == 0) {
					a = -1;
				} else {
					a = (p - 1) / 2;
				}
				b = q / 2;
				break;
		}
		return new Position(a, b);
	}

	private void checkAndFill(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		System.out.println(x + " " + y);
		if (x >= width || x < 0
				|| y >= height || y < 0
				|| img.getRGB(x, y) == borderColor.getRGB()) {
			return;
		}
		Position hex = pixelToHex(e.getX(), e.getY());
		x = hex.getX();
		y = hex.getY();
		int maxX = model.getWidth();
		int maxY = model.getHeight();

		if (x < maxX - y % 2 && x >= 0 && y >= 0 && y < maxY) {
			hexagonDrawer.fill(e.getX(), e.getY(), cellColor);
			model.setCellState(hex.getX(), hex.getY(), true);
			repaint();
		}
	}

	private void checkAndFillXor(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if (x >= width || x < 0
				|| y >= height || y < 0
				|| img.getRGB(x, y) == borderColor.getRGB()) {
			return;
		}
		Position hex = pixelToHex(e.getX(), e.getY());
		x = hex.getX();
		y = hex.getY();
		int maxX = model.getWidth();
		int maxY = model.getHeight();

		if (x < maxX - y % 2 && x >= 0 && y >= 0 && y < maxY && (x != lastFillX || y != lastFillY)) {
			if (model.getCellState(x, y)) {
				hexagonDrawer.fill(e.getX(), e.getY(), backgroundColor);
				model.setCellState(x, y, false);
			} else {
				hexagonDrawer.fill(e.getX(), e.getY(), cellColor);
				model.setCellState(x, y, true);
			}
			lastFillX = x;
			lastFillY = y;
			repaint();
		}
	}


	@Override
	public void handle(EventType type) {
		switch (type) {
			case RESIZE:
				updateSize();
				break;
			case UPDATE_CELLS:
				updateCells();
				break;
		}
		repaint();
	}

	public boolean isShowImpact() {
		return showImpact;
	}

	public void setShowImpact(boolean showImpact) {
		this.showImpact = showImpact;
		repaint();
	}
}
