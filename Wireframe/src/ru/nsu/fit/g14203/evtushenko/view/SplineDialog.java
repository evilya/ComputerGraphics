package ru.nsu.fit.g14203.evtushenko.view;

import ru.nsu.fit.g14203.evtushenko.model.Axis;
import ru.nsu.fit.g14203.evtushenko.model.Model;
import ru.nsu.fit.g14203.evtushenko.model.ModelParameters;
import ru.nsu.fit.g14203.evtushenko.model.PovConverter;
import ru.nsu.fit.g14203.evtushenko.model.geom.Point3D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static java.lang.Math.PI;

public class SplineDialog extends JDialog {
    private final Model model;
    private JPanel contentPane;
    private JComboBox<String> shapeComboBox;
    private JButton addButton;
    private JButton deleteButton;
    private JCheckBox rotateShapeCheckBox;
    private JSpinner aSpinner;
    private JButton colorButton;
    private JSpinner bSpinner;
    private JSpinner cSpinner;
    private JSpinner dSpinner;
    private JSpinner nSpinner;
    private JSpinner kSpinner;
    private JSpinner znSpinner;
    private JSpinner zfSpinner;
    private JSpinner swSpinner;
    private JSpinner shSpinner;
    private JSpinner mSpinner;
    private JSpinner cxSpinner;
    private JSpinner cySpinner;
    private JSpinner czSpinner;
    private JPanel splinePanel;
    private ModelParameters parameters;
    private PovConverter povConverter;

    public SplineDialog(Model model) {
        this.model = model;
        parameters = model.getParameters();
        povConverter = model.getPovConverter();
        $$$setupUI$$$();
        setContentPane(contentPane);
        setModal(false);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        shapeComboBox.addItem("None");
        for (int i = 0; i < model.getSourceShapes().size(); i++) {
            shapeComboBox.addItem((i + 1) + "");
        }

        shapeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int chosenShape = shapeComboBox.getSelectedIndex() - 1;
                model.setChosenShapeIndex(chosenShape);
                if (chosenShape >= 0) {
                    cxSpinner.setEnabled(true);
                    cySpinner.setEnabled(true);
                    czSpinner.setEnabled(true);
                } else {
                    cxSpinner.setEnabled(false);
                    cySpinner.setEnabled(false);
                    czSpinner.setEnabled(false);
                }
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        aSpinner.addChangeListener(e -> {
            parameters.setA((Double) ((JSpinner) e.getSource()).getValue());
            bSpinner.setModel(
                    new SpinnerNumberModel((double) bSpinner.getValue(),
                            (double) aSpinner.getValue(),
                            1.,
                            0.01));
        });
        bSpinner.addChangeListener(e -> {
            parameters.setB((Double) ((JSpinner) e.getSource()).getValue());
            aSpinner.setModel(
                    new SpinnerNumberModel((double) aSpinner.getValue(),
                            0,
                            (double) bSpinner.getValue(),
                            0.01));
        });
        cSpinner.addChangeListener(e -> {
            parameters.setC((Double) ((JSpinner) e.getSource()).getValue());
            dSpinner.setModel(new SpinnerNumberModel(
                    (double) dSpinner.getValue(),
                    (double) cSpinner.getValue(),
                    PI * 2,
                    PI / 100)
            );
        });
        dSpinner.addChangeListener(e -> {
            parameters.setD((Double) ((JSpinner) e.getSource()).getValue());
            cSpinner.setModel(
                    new SpinnerNumberModel((double) cSpinner.getValue(),
                            0.,
                            (double) dSpinner.getValue(),
                            PI / 100));
        });

        mSpinner.addChangeListener(e -> parameters.setM((Integer) ((JSpinner) e.getSource()).getValue()));
        nSpinner.addChangeListener(e -> parameters.setN((Integer) ((JSpinner) e.getSource()).getValue()));
        kSpinner.addChangeListener(e -> parameters.setK((Integer) ((JSpinner) e.getSource()).getValue()));

        zfSpinner.addChangeListener(e -> povConverter.setzB((Double) ((JSpinner) e.getSource()).getValue()));
        znSpinner.addChangeListener(e -> povConverter.setzF((Double) ((JSpinner) e.getSource()).getValue()));
        swSpinner.addChangeListener(e -> povConverter.setsW((Double) ((JSpinner) e.getSource()).getValue()));
        shSpinner.addChangeListener(e -> povConverter.setsH((Double) ((JSpinner) e.getSource()).getValue()));

        cxSpinner.addChangeListener(e -> {
            model.moveSelectedShape(Axis.X, (Double) ((JSpinner) e.getSource()).getValue());
        });

        cySpinner.addChangeListener(e -> {
            model.moveSelectedShape(Axis.Y, (Double) ((JSpinner) e.getSource()).getValue());
        });

        czSpinner.addChangeListener(e -> {
            model.moveSelectedShape(Axis.Z, (Double) ((JSpinner) e.getSource()).getValue());
        });

        rotateShapeCheckBox.addActionListener(e -> model.setRotateShape(((JCheckBox) e.getSource()).isSelected()));


        pack();
        setVisible(true);
    }

    private void updatePositionSpinners(boolean enabled) {
        cxSpinner.setEnabled(enabled);
        cySpinner.setEnabled(enabled);
        czSpinner.setEnabled(enabled);
        if (enabled) {
            Point3D position = model.getChosenShapePosition();
            cxSpinner.getModel().setValue(position.getX());
            cySpinner.getModel().setValue(position.getY());
            czSpinner.getModel().setValue(position.getZ());
        }
    }


    private void onCancel() {
        dispose();
    }

    private void createUIComponents() {
        aSpinner = new JSpinner(new SpinnerNumberModel(parameters.getA(), 0, 1, 0.01));
        bSpinner = new JSpinner(new SpinnerNumberModel(parameters.getB(), 0, 1, 0.01));
        cSpinner = new JSpinner(new SpinnerNumberModel(parameters.getC(), 0, PI * 2, PI / 100));
        dSpinner = new JSpinner(new SpinnerNumberModel(parameters.getD(), 0, PI * 2, PI / 100));
        mSpinner = new JSpinner(new SpinnerNumberModel(parameters.getM(), 1, 20, 1));
        nSpinner = new JSpinner(new SpinnerNumberModel(parameters.getN(), 1, 20, 1));
        kSpinner = new JSpinner(new SpinnerNumberModel(parameters.getK(), 1, 10, 1));
        swSpinner = new JSpinner(new SpinnerNumberModel(povConverter.getsW(), 0.1, 2, 0.1));
        shSpinner = new JSpinner(new SpinnerNumberModel(povConverter.getsH(), 0.1, 2, 0.1));
        znSpinner = new JSpinner(new SpinnerNumberModel(povConverter.getzF(), -100, 100, 0.1));
        zfSpinner = new JSpinner(new SpinnerNumberModel(povConverter.getzB(), -100, 100, 0.1));
        cxSpinner = new JSpinner(new SpinnerNumberModel(0, -100, 100, 1.));
        cySpinner = new JSpinner(new SpinnerNumberModel(0, -100, 100, 1.));
        czSpinner = new JSpinner(new SpinnerNumberModel(0, -100, 100, 1.));
        splinePanel = new SplineEditView(model);
        updatePositionSpinners(model.getChosenShapeIndex() != -1);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        contentPane = new JPanel();
        contentPane.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1));
        contentPane.setAutoscrolls(false);
        contentPane.setMinimumSize(new Dimension(300, 300));
        contentPane.setPreferredSize(new Dimension(600, 400));
        contentPane.add(splinePanel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        contentPane.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        shapeComboBox = new JComboBox();
        panel1.add(shapeComboBox);
        addButton = new JButton();
        addButton.setText("Add");
        panel1.add(addButton);
        deleteButton = new JButton();
        deleteButton.setText("Delete");
        panel1.add(deleteButton);
        rotateShapeCheckBox = new JCheckBox();
        rotateShapeCheckBox.setText("Rotate shape");
        panel1.add(rotateShapeCheckBox);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        contentPane.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setHorizontalAlignment(4);
        label1.setHorizontalTextPosition(0);
        label1.setText("a");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(label1, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(aSpinner, gbc);
        final JLabel label2 = new JLabel();
        label2.setHorizontalAlignment(4);
        label2.setHorizontalTextPosition(0);
        label2.setText("b");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(label2, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(bSpinner, gbc);
        final JLabel label3 = new JLabel();
        label3.setHorizontalAlignment(4);
        label3.setHorizontalTextPosition(0);
        label3.setText("c");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(label3, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(cSpinner, gbc);
        final JLabel label4 = new JLabel();
        label4.setHorizontalAlignment(4);
        label4.setHorizontalTextPosition(0);
        label4.setText("d");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(label4, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(dSpinner, gbc);
        final JLabel label5 = new JLabel();
        label5.setHorizontalAlignment(4);
        label5.setHorizontalTextPosition(0);
        label5.setText("zn");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(label5, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(znSpinner, gbc);
        final JLabel label6 = new JLabel();
        label6.setHorizontalAlignment(4);
        label6.setHorizontalTextPosition(0);
        label6.setText("zf");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(label6, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(zfSpinner, gbc);
        final JLabel label7 = new JLabel();
        label7.setHorizontalAlignment(4);
        label7.setHorizontalTextPosition(0);
        label7.setText("sw");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(label7, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(swSpinner, gbc);
        final JLabel label8 = new JLabel();
        label8.setHorizontalAlignment(4);
        label8.setHorizontalTextPosition(0);
        label8.setText("sh");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(label8, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(shSpinner, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(mSpinner, gbc);
        final JLabel label9 = new JLabel();
        label9.setHorizontalAlignment(4);
        label9.setHorizontalTextPosition(0);
        label9.setText("m");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(label9, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(nSpinner, gbc);
        final JLabel label10 = new JLabel();
        label10.setHorizontalAlignment(4);
        label10.setHorizontalTextPosition(0);
        label10.setText("n");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(label10, gbc);
        final JLabel label11 = new JLabel();
        label11.setHorizontalAlignment(4);
        label11.setHorizontalTextPosition(0);
        label11.setText("k");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(label11, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(kSpinner, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(cxSpinner, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(cySpinner, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(czSpinner, gbc);
        final JLabel label12 = new JLabel();
        label12.setHorizontalAlignment(4);
        label12.setHorizontalTextPosition(0);
        label12.setText("cX");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(label12, gbc);
        final JLabel label13 = new JLabel();
        label13.setHorizontalAlignment(4);
        label13.setHorizontalTextPosition(0);
        label13.setText("cY");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(label13, gbc);
        final JLabel label14 = new JLabel();
        label14.setHorizontalAlignment(4);
        label14.setHorizontalTextPosition(0);
        label14.setText("cZ");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(label14, gbc);
        colorButton = new JButton();
        colorButton.setText("Color");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.gridheight = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        gbc.insets = new Insets(2, 2, 2, 2);
        panel2.add(colorButton, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
