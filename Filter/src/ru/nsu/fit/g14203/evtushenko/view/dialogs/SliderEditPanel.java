package ru.nsu.fit.g14203.evtushenko.view.dialogs;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SliderEditPanel extends JPanel {

    private final JSlider slider;
    private final JFormattedTextField field;

    public SliderEditPanel(int min, int max, int initial, Runnable listener) {
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
                if (!Character.isDigit(e.getKeyChar()) && '-' != e.getKeyChar()) {
                    e.consume();
                }
            }
        });
        slider.addChangeListener(e -> {
            field.setValue(slider.getValue());
            if (listener != null) {
                listener.run();
            }
        });

        field.setSize(new Dimension(75, 30));
        field.setHorizontalAlignment(SwingConstants.CENTER);
        field.setValue(initial);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                try {
                    int value = Integer.parseInt(field.getText());
                    slider.setValue(value);
                    if (listener != null) {
                        listener.run();
                    }
                } catch (NumberFormatException ex) {
                    slider.setValue(initial);
                    field.setText(initial + "");
                }
            }
        });
        add(slider);
        add(field);
    }

    public int getValue() {
        return slider.getValue();
    }
}
