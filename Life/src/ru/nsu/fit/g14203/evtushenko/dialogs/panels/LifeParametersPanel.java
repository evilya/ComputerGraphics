package ru.nsu.fit.g14203.evtushenko.dialogs.panels;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class LifeParametersPanel extends JPanel {
	private final JFormattedTextField lifeBeginField;
	private final JFormattedTextField lifeEndField;
	private final JFormattedTextField birthBeginField;
	private final JFormattedTextField birthEndField;
	private final JFormattedTextField firstImpactField;
	private final JFormattedTextField secondImpactField;

	public LifeParametersPanel(double lifeBegin,
	                           double lifeEnd,
	                           double birthBegin,
	                           double birthEnd,
	                           double firstImpact,
	                           double secondImpact) {
		super();
		setLayout(new GridLayout(2, 6, 5, 5));

		 KeyAdapter numbersAdapter = new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (!(Character.isDigit(e.getKeyChar()) || e.getKeyChar() == '.')){
					e.consume();
				}
			}
		};

		JLabel lifeBeginLabel = new JLabel("Life Begin", JLabel.RIGHT);
		JLabel lifeEndLabel = new JLabel("Life End", JLabel.RIGHT);
		JLabel birthBeginLabel = new JLabel("Birth Begin", JLabel.RIGHT);
		JLabel birthEndLabel = new JLabel("Birth End", JLabel.RIGHT);
		JLabel firstImpactLabel = new JLabel("First Impact", JLabel.RIGHT);
		JLabel secondImpactLabel = new JLabel("Second Impact", JLabel.RIGHT);


		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		DecimalFormat format = new DecimalFormat("#0.0", symbols);
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setMaximum(10.f);
		formatter.setMinimum(0.f);

		lifeBeginField = new JFormattedTextField(formatter);
		lifeEndField = new JFormattedTextField(formatter);
		birthBeginField = new JFormattedTextField(formatter);
		birthEndField = new JFormattedTextField(formatter);
		firstImpactField = new JFormattedTextField(formatter);
		secondImpactField = new JFormattedTextField(formatter);

		lifeBeginField.addKeyListener(numbersAdapter);
		lifeEndField.addKeyListener(numbersAdapter);
		birthBeginField.addKeyListener(numbersAdapter);
		birthEndField.addKeyListener(numbersAdapter);
		firstImpactField.addKeyListener(numbersAdapter);
		secondImpactField.addKeyListener(numbersAdapter);

		lifeBeginField.setValue(lifeBegin);
		lifeEndField.setValue(lifeEnd);
		birthBeginField.setValue(birthBegin);
		birthEndField.setValue(birthEnd);
		firstImpactField.setValue(firstImpact);
		secondImpactField.setValue(secondImpact);

		add(lifeBeginLabel);
		add(lifeBeginField);

		add(birthBeginLabel);
		add(birthBeginField);

		add(firstImpactLabel);
		add(firstImpactField);

		add(lifeEndLabel);
		add(lifeEndField);

		add(birthEndLabel);
		add(birthEndField);

		add(secondImpactLabel);
		add(secondImpactField);
	}

	public float getLifeBeginField() {
		return Float.parseFloat(lifeBeginField.getText());

	}

	public float getLifeEndField() {
		return Float.parseFloat(lifeEndField.getText());
	}

	public float getBirthBeginField() {
		return Float.parseFloat(birthBeginField.getText());
	}

	public float getBirthEndField() {
		return Float.parseFloat(birthEndField.getText());
	}

	public float getFirstImpactField() {
		return Float.parseFloat(firstImpactField.getText());
	}

	public float getSecondImpactField() {
		return Float.parseFloat(secondImpactField.getText());
	}
}
