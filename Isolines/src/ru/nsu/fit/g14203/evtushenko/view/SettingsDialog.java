package ru.nsu.fit.g14203.evtushenko.view;

import javax.swing.*;
import java.awt.*;

public class SettingsDialog extends JDialog {

    private final EditPanel aEdit;
    private final EditPanel bEdit;
    private final EditPanel cEdit;
    private final EditPanel dEdit;
    private final EditPanel kEdit;
    private final EditPanel mEdit;


    public SettingsDialog(Frame owner, View view) {
        super(owner, "Settings", true);

        setPreferredSize(new Dimension(300, 250));
        setLocationRelativeTo(getParent());
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridy = 0;
        c.gridx = 0;

        aEdit = new EditPanel(Integer.MIN_VALUE, Integer.MAX_VALUE, view.getA());
        aEdit.setPreferredSize(new Dimension(120, 45));
        aEdit.setBorder(BorderFactory.createTitledBorder("A"));
        add(aEdit, c);

        c.gridx = 1;
        bEdit = new EditPanel(Integer.MIN_VALUE, Integer.MAX_VALUE, view.getB());
        bEdit.setPreferredSize(new Dimension(120, 45));
        bEdit.setBorder(BorderFactory.createTitledBorder("B"));
        add(bEdit, c);

        c.gridy = 1;

        c.gridx = 0;

        cEdit = new EditPanel(Integer.MIN_VALUE, Integer.MAX_VALUE, view.getC());
        cEdit.setPreferredSize(new Dimension(120, 45));
        cEdit.setBorder(BorderFactory.createTitledBorder("C"));
        add(cEdit, c);

        c.gridx = 1;
        dEdit = new EditPanel(Integer.MIN_VALUE, Integer.MAX_VALUE, view.getD());
        dEdit.setPreferredSize(new Dimension(120, 45));
        dEdit.setBorder(BorderFactory.createTitledBorder("D"));
        add(dEdit, c);

        c.gridy = 2;

        c.gridx = 0;

        kEdit = new EditPanel(2, 200, view.getK());
        kEdit.setPreferredSize(new Dimension(120, 45));
        kEdit.setBorder(BorderFactory.createTitledBorder("K"));
        add(kEdit, c);

        c.gridx = 1;
        mEdit = new EditPanel(2, 200, view.getM());
        mEdit.setPreferredSize(new Dimension(120, 45));
        mEdit.setBorder(BorderFactory.createTitledBorder("M"));
        add(mEdit, c);

        c.gridwidth = 1;
        c.gridy = 3;

        JButton okButton = new JButton("Ok");
        c.gridx = 0;
        add(okButton, c);

        okButton.addActionListener(e -> {

            int a = aEdit.getValue();
            int b = bEdit.getValue();
            int c1 = cEdit.getValue();
            int d = dEdit.getValue();
            int k = kEdit.getValue();
            int m = mEdit.getValue();
            if (a < b && c1 < d) {
                view.setParameters(a, b, c1, d, k, m);
                setVisible(false);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Incorrect parameters",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        c.gridx = 1;
        add(cancelButton, c);

        cancelButton.addActionListener(e -> {
            dispose();
        });

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
    }
}
