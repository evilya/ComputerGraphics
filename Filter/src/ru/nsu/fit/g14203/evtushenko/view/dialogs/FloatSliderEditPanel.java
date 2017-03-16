package ru.nsu.fit.g14203.evtushenko.view.dialogs;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class FloatSliderEditPanel extends JPanel {

    private final double tick;
    private final JSlider slider;
    private final JFormattedTextField field;

    public FloatSliderEditPanel(double min, double max, double initial, double tick, Runnable listener) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.tick = tick;

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat format = new DecimalFormat("#0.0", symbols);
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setMaximum(max);
        formatter.setMinimum(min);

        slider = new JSlider((int) (min / tick), (int) (max / tick), (int) (initial / tick));
        field = new JFormattedTextField(formatter);

        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar()) && '-' != e.getKeyChar() && '.' != e.getKeyChar()) {
                    e.consume();
                }
            }
        });
        slider.addChangeListener(e -> {
            field.setValue(slider.getValue()*tick);
            listener.run();
        });

        field.setSize(new Dimension(75, 30));
        field.setHorizontalAlignment(SwingConstants.CENTER);
        field.setValue(initial);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                try {
                    double value = Double.parseDouble(field.getText());
                    slider.setValue((int) Math.round(value/tick));
                    listener.run();
                } catch (NumberFormatException ex) {
                    slider.setValue((int) (initial/tick));
                    field.setText(initial + "");
                }
            }
        });
        add(slider);
        add(field);
    }

    public double getValue() {
        return slider.getValue() * tick;
    }
}
