package ru.nsu.fit.g14203.evtushenko.view.dialogs;

import ru.nsu.fit.g14203.evtushenko.Controller;
import ru.nsu.fit.g14203.evtushenko.model.FilterParameters;

import javax.swing.*;
import java.awt.*;

public class SingleFloatParameterDialog extends JDialog {
    private FloatSliderEditPanel sliderEditPanel;

    public SingleFloatParameterDialog(Frame owner,
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

        sliderEditPanel = new FloatSliderEditPanel(minValue, maxValue, initialValue, 0.1, applyFilter);
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
            setVisible(false);
            dispose();
        });


        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
        cancelButton.requestFocus();
    }
}
