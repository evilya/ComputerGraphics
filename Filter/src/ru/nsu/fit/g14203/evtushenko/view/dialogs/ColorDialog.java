package ru.nsu.fit.g14203.evtushenko.view.dialogs;

import ru.nsu.fit.g14203.evtushenko.Controller;
import ru.nsu.fit.g14203.evtushenko.model.FilterParameters;

import javax.swing.*;
import java.awt.*;

public class ColorDialog extends JDialog {

    private SliderEditPanel redEdit;
    private SliderEditPanel greenEdit;
    private SliderEditPanel blueEdit;

    public ColorDialog(Frame owner, String name, Controller controller, FilterParameters.FilterType type) {
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
                                new double[]{redEdit.getValue(),
                                        greenEdit.getValue(),
                                        blueEdit.getValue()}));

        c.gridwidth = 2;
        redEdit = new SliderEditPanel(2, 256, 2, applyFilter);
        c.gridx = 0;
        c.gridy = 0;
        redEdit.setBorder(BorderFactory.createTitledBorder("Red colors"));
        add(redEdit, c);

        greenEdit = new SliderEditPanel(2, 256, 2, applyFilter);
        c.gridx = 0;
        c.gridy = 1;
        greenEdit.setBorder(BorderFactory.createTitledBorder("Green colors"));
        add(greenEdit, c);

        blueEdit = new SliderEditPanel(2, 256, 2, applyFilter);
        c.gridx = 0;
        c.gridy = 2;
        blueEdit.setBorder(BorderFactory.createTitledBorder("Blue colors"));
        add(blueEdit, c);

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
