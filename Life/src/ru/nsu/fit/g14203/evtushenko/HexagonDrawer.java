package ru.nsu.fit.g14203.evtushenko;

import ru.nsu.fit.g14203.evtushenko.fillers.Filler;
import ru.nsu.fit.g14203.evtushenko.fillers.SpanFiller;
import ru.nsu.fit.g14203.evtushenko.lines.BresenhamLineDrawer;
import ru.nsu.fit.g14203.evtushenko.lines.LineDrawer;

import java.awt.*;
import java.awt.image.BufferedImage;

public class HexagonDrawer {
	private Graphics2D graphics;
	private LineDrawer lineDrawer;
	private Filler filler;
	private int lineThickness;
	private int size;
	private int offsetX, offsetY;

	public HexagonDrawer(BufferedImage image, Graphics2D graphics, int lineThickness, int size) {
		if (size <= 0
				|| lineThickness <= 0
				|| image == null
				|| graphics == null) {
			throw new IllegalArgumentException();
		}
		this.graphics = graphics;
		this.lineThickness = lineThickness;
		lineDrawer = new BresenhamLineDrawer(image, new Color(Integer.MIN_VALUE));
		filler = new SpanFiller(image);
		setSize(size);
	}

	public void fill(int x, int y, Color color) {
		filler.fill(x, y, color);
	}

	public void draw(int centerX, int centerY) {
		if (lineThickness > 1) {
			graphics.setStroke(new BasicStroke(lineThickness));
			graphics.setColor(new Color(Integer.MIN_VALUE));
		}
		drawLine(centerX, centerY - size, centerX + offsetX, centerY - offsetY);
		drawLine(centerX + offsetX, centerY - offsetY, centerX + offsetX, centerY + offsetY);
		drawLine(centerX + offsetX, centerY + offsetY, centerX, centerY + size);
		drawLine(centerX, centerY + size, centerX - offsetX, centerY + offsetY);
		drawLine(centerX - offsetX, centerY + offsetY, centerX - offsetX, centerY - offsetY);
		drawLine(centerX - offsetX, centerY - offsetY, centerX, centerY - size);
	}

	private void drawLine(int x1, int y1, int x2, int y2) {
		if (lineThickness > 1) {
			graphics.drawLine(x1, y1, x2, y2);
		} else if (lineThickness == 1) {
			lineDrawer.drawLine(x1, y1, x2, y2);
		}
	}

	public int getLineThickness() {
		return lineThickness;
	}

	public void setLineThickness(int lineThickness) {
		if (lineThickness <= 0) {
			throw new IllegalArgumentException("Incorrect thickness");
		}
		this.lineThickness = lineThickness;
	}

	public void setSize(int size) {
		this.size = size;
		offsetX = size * 866 / 1000;
		offsetY = size / 2;
	}

	public int getSize() {
		return size;
	}

	public int getOffsetX() {
		return offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}
}
