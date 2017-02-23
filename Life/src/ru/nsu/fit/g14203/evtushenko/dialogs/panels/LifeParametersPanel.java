package ru.nsu.fit.g14203.evtushenko.dialogs.panels;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class LifeParametersPanel extends JPanel {
	private final JFormattedTextField lifeBeginField;
	private final JFormattedTextField lifeEndField;
	private final JFormattedTextField birthBeginField;
	private final JFormattedTextField birthEndField;
	private final JFormattedTextField firstImpactField;
	private final JFormattedTextField secondImpactField;

	public LifeParametersPanel(float lifeBegin,
	                           float lifeEnd,
	                           float birthBegin,
	                           float birthEnd,
	                           float firstImpact,
	                           float secondImpact) {
		super();
		setLayout(new GridLayout(2, 6, 5, 5));

		JLabel lifeBeginLabel = new JLabel("Life Begin", JLabel.RIGHT);
		JLabel lifeEndLabel = new JLabel("Life End", JLabel.RIGHT);
		JLabel birthBeginLabel = new JLabel("Birth Begin", JLabel.RIGHT);
		JLabel birthEndLabel = new JLabel("Birth End", JLabel.RIGHT);
		JLabel firstImpactLabel = new JLabel("First Impact", JLabel.RIGHT);
		JLabel secondImpactLabel = new JLabel("Second Impact", JLabel.RIGHT);


		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		symbols.setMinusSign(' ');
		DecimalFormat format = new DecimalFormat("#0.0#", symbols);

		lifeBeginField = new JFormattedTextField(format);
		lifeEndField = new JFormattedTextField(format);
		birthBeginField = new JFormattedTextField(format);
		birthEndField = new JFormattedTextField(format);
		firstImpactField = new JFormattedTextField(format);
		secondImpactField = new JFormattedTextField(format);

		lifeBeginField.setText(lifeBegin + "");
		lifeEndField.setText(lifeEnd + "");
		birthBeginField.setText(birthBegin + "");
		birthEndField.setText(birthEnd + "");
		firstImpactField.setText(firstImpact + "");
		secondImpactField.setText(secondImpact + "");

		add(lifeBeginLabel);
		add(lifeBeginField);

		add(lifeEndLabel);
		add(lifeEndField);

		add(birthBeginLabel);
		add(birthBeginField);

		add(birthEndLabel);
		add(birthEndField);

		add(firstImpactLabel);
		add(firstImpactField);

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
