package ru.nsu.fit.g14203.evtushenko.view;

import ru.nsu.fit.g14203.evtushenko.model.Model;

import javax.swing.*;
import java.awt.*;

public class SplineEditView extends JPanel {
    private final Model model;
    private final BasicStroke dashedStroke = new BasicStroke(1.f,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_ROUND,
            0,
            new float[]{1},
            0);

    public SplineEditView(Model model) {
        this.model = model;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        g.setColor(Color.WHITE);
        g.fillRect(1, 1, getWidth() - 2, getHeight() - 2);


        ((Graphics2D)g).setStroke(dashedStroke);
        g.setColor(Color.BLACK);
        g.drawLine((getWidth() - 1)/2, 0, (getWidth() - 1)/2, getHeight());
        g.drawLine(0, (getHeight()-1)/2, getWidth(), (getHeight()-1)/2);
    }
}
