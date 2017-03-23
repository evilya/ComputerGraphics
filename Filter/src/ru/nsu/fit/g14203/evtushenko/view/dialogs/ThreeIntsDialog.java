package ru.nsu.fit.g14203.evtushenko.view.dialogs;

import ru.nsu.fit.g14203.evtushenko.Controller;
import ru.nsu.fit.g14203.evtushenko.model.FilterParameters;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class ThreeIntsDialog extends JDialog {

    private SliderEditPanel firstParameter;
    private SliderEditPanel secondParameter;
    private SliderEditPanel thirdParameter;

    public ThreeIntsDialog(Frame owner, String name, Controller controller, FilterParameters filter,
                           String[] parametersName, int[] parametersMin, int[] parametersMax, boolean runBeforeConfirm) {
        super(owner, name, true);

        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        Runnable applyFilter = () -> {
            double[] params = filter.getParameters();
            params = Arrays.copyOf(params, params.length + 3);
            params[params.length - 3] = firstParameter.getValue();
            params[params.length - 2] = secondParameter.getValue();
            params[params.length - 1] = thirdParameter.getValue();
            controller.applyFilter(
                    new FilterParameters(filter.getType(), params));
        };

        c.gridwidth = 2;
        firstParameter = new SliderEditPanel(parametersMin[0],
                parametersMax[0],
                (parametersMax[0] - parametersMin[0]) / 2,
                runBeforeConfirm ? applyFilter : null);
        c.gridx = 0;
        c.gridy = 0;
        firstParameter.setBorder(BorderFactory.createTitledBorder(parametersName[0]));
        add(firstParameter, c);

        secondParameter = new SliderEditPanel(parametersMin[1],
                parametersMax[1],
                (parametersMax[1] - parametersMin[1]) / 2,
                runBeforeConfirm ? applyFilter : null);
        c.gridx = 0;
        c.gridy = 1;
        secondParameter.setBorder(BorderFactory.createTitledBorder(parametersName[1]));
        add(secondParameter, c);

        thirdParameter = new SliderEditPanel(parametersMin[2],
                parametersMax[2],
                (parametersMax[2] - parametersMin[2]) / 2,
                runBeforeConfirm ? applyFilter : null);
        c.gridx = 0;
        c.gridy = 2;
        thirdParameter.setBorder(BorderFactory.createTitledBorder(parametersName[2]));
        add(thirdParameter, c);

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
            if (!runBeforeConfirm) {
                applyFilter.run();
            }
            setVisible(false);
            dispose();
        });

        cancelButton.addActionListener(e -> {
            controller.rollback();
            setVisible(false);
            dispose();
        });

        controller.saveState();
        if (runBeforeConfirm) {
            applyFilter.run();
        }

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
        cancelButton.requestFocus();
    }
}
