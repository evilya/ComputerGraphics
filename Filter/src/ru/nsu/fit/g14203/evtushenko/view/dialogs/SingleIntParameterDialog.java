package ru.nsu.fit.g14203.evtushenko.view.dialogs;

import ru.nsu.fit.g14203.evtushenko.Controller;
import ru.nsu.fit.g14203.evtushenko.model.FilterParameters;

import javax.swing.*;
import java.awt.*;

public class SingleIntParameterDialog extends JDialog {
    private SliderEditPanel sliderEditPanel;

    public SingleIntParameterDialog(Frame owner,
                                    String name,
                                    Controller controller,
                                    FilterParameters.FilterType type,
                                    int minValue,
                                    int maxValue,
                                    int initialValue) {
        super(owner, name, true);

        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        Runnable applyFilter = () ->
                controller.applyFilter(
                        new FilterParameters(type,
                                new double[]{sliderEditPanel.getValue()}));

        sliderEditPanel = new SliderEditPanel(minValue, maxValue, initialValue, applyFilter);
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 0;
        add(sliderEditPanel, c);

        c.gridwidth = 1;
        c.weightx = 0.5;

        JButton okButton = new JButton("Ok");
        c.gridx = 0;
        c.gridy = 3;
        add(okButton, c);

        JButton cancelButton = new JButton("Cancel");
        c.gridx = 1;
        c.gridy = 3;
        add(cancelButton, c);

        okButton.addActionListener(e -> {
            setVisible(false);
            dispose();
        });

        cancelButton.addActionListener(e -> {
            controller.rollback();
            setVisible(false);
            dispose();
        });

        controller.saveState();
        applyFilter.run();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
        cancelButton.requestFocus();
    }
}
