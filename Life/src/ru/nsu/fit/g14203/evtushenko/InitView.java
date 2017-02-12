package ru.nsu.fit.g14203.evtushenko;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class InitView extends JPanel {

	public InitView()
	{
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				JOptionPane.showMessageDialog(InitView.this,
						"Clicked on "+e.getX()+","+e.getY());
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawLine(0, 0, getBounds().width-1, getBounds().height-1);
		g.drawLine(0, getBounds().height-1, getBounds().width-1, 0);
	}
}
