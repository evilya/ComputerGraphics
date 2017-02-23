package ru.nsu.fit.g14203.evtushenko.dialogs.panels;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class WidthHeightPanel extends JPanel {

	private final JFormattedTextField widthField;
	private final JFormattedTextField heightField;

	public WidthHeightPanel(int width, int height) {
		super();
		setLayout(new GridLayout(2, 2, 5, 5));

		JLabel widthLabel = new JLabel("Width:");
		JLabel heightLabel = new JLabel("Height:");

		widthField = new JFormattedTextField(new DecimalFormat("#"));
		heightField = new JFormattedTextField(new DecimalFormat("#"));

		widthField.setText(width+"");
		heightField.setText(height+"");

		add(widthLabel);
		add(widthField);
		add(heightLabel);
		add(heightField);

	}

	public int getWidthValue(){
		return Integer.parseInt(widthField.getText());
	}

	public int getHeightValue(){
		return Integer.parseInt(heightField.getText());
	}
}
