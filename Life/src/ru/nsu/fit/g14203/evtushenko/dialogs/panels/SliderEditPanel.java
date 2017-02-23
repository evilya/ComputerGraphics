package ru.nsu.fit.g14203.evtushenko.dialogs.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;

public class SliderEditPanel extends JPanel {

	private final JSlider slider;
	private final JFormattedTextField field;

	public SliderEditPanel(int min, int max, int initial) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		slider = new JSlider(min, max, initial);
		field = new JFormattedTextField(new DecimalFormat("#"));

		slider.addChangeListener(e -> field.setText(slider.getValue() + ""));

		field.setSize(new Dimension(75, 30));
		field.setHorizontalAlignment(SwingConstants.CENTER);
		field.setText(initial+"");
		field.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				slider.setValue(Integer.parseInt(field.getText()));
			}
		});
		add(slider);
		add(field);
	}

	public int getValue(){
		return slider.getValue();
	}
}
