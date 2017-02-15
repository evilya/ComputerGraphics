package ru.nsu.fit.g14203.evtushenko;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class InitView extends JPanel {
	private BufferedImage img;
	private Graphics2D graphics;
	private HexagonDrawer hexagonDrawer;

	public InitView() {

		img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
		graphics = img.createGraphics();

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				System.out.println("LUL");
				hexagonDrawer.fill(e.getX(), e.getY(), Color.RED);
				repaint();
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int size = 25;
		hexagonDrawer = new HexagonDrawer(img, graphics, 1, size);
		int offsetX = hexagonDrawer.getOffsetX();
		int offsetY = hexagonDrawer.getOffsetY();
		for (int j = 0; j < 10; j++) {
			int dy = (size + offsetY) * j;
			for (int i = 0; i < 10 - (j % 2); i++) {
				int dx = offsetX * i * 2 + (j % 2) * offsetX;
				hexagonDrawer.draw(25 + dx, 25 + dy);
			}
		}
		g.drawImage(img, 0, 0, null);
	}
}
