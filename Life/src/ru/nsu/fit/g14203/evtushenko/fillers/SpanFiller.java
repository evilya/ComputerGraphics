package ru.nsu.fit.g14203.evtushenko.fillers;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;

public class SpanFiller implements Filler {

	private final BufferedImage image;
	private Deque<Span> spanStack = new ArrayDeque<>();

	public SpanFiller(BufferedImage image) {
		this.image = image;
	}

	@Override
	public void fill(int x, int y, Color color) {
		int newColor = color.getRGB();
		int oldColor = image.getRGB(x, y);

		if (newColor == oldColor) {
			return;
		}

		Span span = findSpan(x, y, oldColor);
		spanStack.addLast(span);

		while (!spanStack.isEmpty()) {
			span = spanStack.removeLast();
			fillSpan(span, newColor);
			for (int offsetY = -1; offsetY <= 1; offsetY += 2) {
				if (span.getY() > 0 && span.getY() < image.getHeight()) {
					for (int curX = span.getLeftX(); curX <= span.getRightX(); curX++) {
						Span nextSpan = findSpan(curX, span.getY() + offsetY, oldColor);
						if (nextSpan != null) {
							curX = nextSpan.getRightX() + 2; //because +1 is silly
							spanStack.addLast(nextSpan);
						}
					}
				}
			}
		}
	}

	private Span findSpan(int x, int y, int oldColorRGB) {
		int x0 = x;
		int x1 = x;
		while (x0 >= 0 && image.getRGB(x0, y) == oldColorRGB) {
			x0--;
		}
		while (x1 < image.getWidth() && image.getRGB(x1, y) == oldColorRGB) {
			x1++;
		}
		if (x0 == x1) {
			return null;
		}
		return new Span(x0 + 1, x1 - 1, y);
	}

	private void fillSpan(Span span, int rgb) {
		int y = span.getY();
		for (int x = span.getLeftX(); x <= span.getRightX(); x++) {
			image.setRGB(x, y, rgb);
		}
	}

	private static final class Span {
		private final int leftX;
		private final int rightX;
		private final int y;

		public Span(int leftX, int rightX, int y) {
			this.leftX = leftX;
			this.rightX = rightX;
			this.y = y;
		}

		public int getLeftX() {
			return leftX;
		}

		public int getRightX() {
			return rightX;
		}

		public int getY() {
			return y;
		}
	}
}
