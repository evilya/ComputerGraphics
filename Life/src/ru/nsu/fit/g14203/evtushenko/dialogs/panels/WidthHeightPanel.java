package ru.nsu.fit.g14203.evtushenko.dialogs.panels;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

public class WidthHeightPanel extends JPanel {

	private final JFormattedTextField widthField;
	private final JFormattedTextField heightField;

	public WidthHeightPanel(int width, int height) {
		super();
		setLayout(new GridLayout(2, 2, 5, 5));

		JLabel widthLabel = new JLabel("Width:");
		JLabel heightLabel = new JLabel("Height:");

		KeyAdapter keyAdapter = new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (!Character.isDigit(e.getKeyChar())){
					e.consume();
				}
			}
		};

		NumberFormatter formatter = new NumberFormatter();
		formatter.setMinimum(1);
		formatter.setMaximum(100);
		widthField = new JFormattedTextField(formatter);
		heightField = new JFormattedTextField(formatter);

		widthField.addKeyListener(keyAdapter);
		heightField.addKeyListener(keyAdapter);

		widthField.setValue(width);
		heightField.setValue(height);

		add(widthLabel);
		add(widthField);
		add(heightLabel);
		add(heightField);

	}

	public int getWidthValue() {
		return Integer.parseInt(widthField.getText());
	}

	public int getHeightValue() {
		return Integer.parseInt(heightField.getText());
	}
}
