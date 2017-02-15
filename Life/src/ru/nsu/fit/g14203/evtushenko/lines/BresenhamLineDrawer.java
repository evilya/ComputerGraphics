package ru.nsu.fit.g14203.evtushenko.lines;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BresenhamLineDrawer implements LineDrawer {
	private Color color;
	private BufferedImage image;

	public BresenhamLineDrawer(BufferedImage image, Color color) {
		this.image = image;
		this.color = color;
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		int offsetX, offsetY, dirX, dirY;
		int pdx = 0, pdy = 0; //describes chosen line direction

		offsetX = x2 - x1;
		offsetY = y2 - y1;

		dirX = Integer.signum(offsetX);
		dirY = Integer.signum(offsetY);

		offsetX = Math.abs(offsetX);
		offsetY = Math.abs(offsetY);

		if (offsetX > offsetY) {
			pdx = dirX;
		} else {
			pdy = dirY;
		}

		int x = x1;
		int y = y1;
		int err = Math.max(offsetX, offsetY) / 2;
		image.setRGB(x, y, color.getRGB());

		for (int t = 0; t < Math.max(offsetX, offsetY); t++) {
			err -= Math.min(offsetX, offsetY);
			if (err < 0) {
				err += Math.max(offsetX, offsetY);
				x += dirX;
				y += dirY;
			} else {
				x += pdx;
				y += pdy;
			}
			image.setRGB(x, y, color.getRGB());
		}
	}
}
