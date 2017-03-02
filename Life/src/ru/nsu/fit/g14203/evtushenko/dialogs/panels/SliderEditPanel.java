package ru.nsu.fit.g14203.evtushenko.dialogs.panels;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

public class SliderEditPanel extends JPanel {

	private final JSlider slider;
	private final JFormattedTextField field;

	public SliderEditPanel(int min, int max, int initial) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		NumberFormatter formatter = new NumberFormatter();
		formatter.setMaximum(max);
		formatter.setMinimum(min);

		slider = new JSlider(min, max, initial);
		field = new JFormattedTextField(formatter);

		field.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (!Character.isDigit(e.getKeyChar())){
					e.consume();
				}
			}
		});
		slider.addChangeListener(e -> field.setValue(slider.getValue()));

		field.setSize(new Dimension(75, 30));
		field.setHorizontalAlignment(SwingConstants.CENTER);
		field.setValue(initial);
		field.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				int value = Integer.parseInt(field.getText());
				slider.setValue(value);
			}
		});
		add(slider);
		add(field);
	}

	public int getValue() {
		return slider.getValue();
	}
}
