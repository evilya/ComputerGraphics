package ru.nsu.fit.g14203.evtushenko.view;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

public class EditPanel extends JPanel {

    private final JFormattedTextField field;

    public EditPanel(int min, int max, int initial) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        NumberFormatter formatter = new NumberFormatter();
        formatter.setMaximum(max);
        formatter.setMinimum(min);
        formatter.setFormat(new DecimalFormat("#;-#"));

        field = new JFormattedTextField(formatter);

//        field.setSize(new Dimension(75, 18));
        field.setHorizontalAlignment(SwingConstants.CENTER);
        field.setValue(initial);

        add(field);
    }

    public int getValue() {
        return Integer.parseInt(field.getText());
    }
}
