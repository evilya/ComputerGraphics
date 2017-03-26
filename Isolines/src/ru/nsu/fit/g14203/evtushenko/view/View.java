package ru.nsu.fit.g14203.evtushenko.view;

import ru.nsu.fit.g14203.evtushenko.Controller;
import ru.nsu.fit.g14203.evtushenko.EventType;
import ru.nsu.fit.g14203.evtushenko.Observer;

import javax.swing.*;
import java.awt.*;

public class View extends JPanel implements Observer {

    private Controller controller;

    public View(Controller controller) {
        this.controller = controller;
        setLayout(new GridBagLayout());
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    public void handle(EventType type) {
        switch (type) {

        }
    }
}
