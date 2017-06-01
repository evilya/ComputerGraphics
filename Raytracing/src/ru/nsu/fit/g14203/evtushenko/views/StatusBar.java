package ru.nsu.fit.g14203.evtushenko.views;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;


public class StatusBar extends JLabel {

    public StatusBar() {
        Border border = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
        setBorder(border);
        setMessage("");
        setVisible(true);
    }

    public void setMessage(String str) {
        setText(str + "\0");
    }
}
