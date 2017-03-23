package ru.nsu.fit.g14203.evtushenko.view;

import ru.nsu.fit.g14203.evtushenko.Controller;
import ru.nsu.fit.g14203.evtushenko.EventType;
import ru.nsu.fit.g14203.evtushenko.Observer;
import ru.nsu.fit.g14203.evtushenko.model.Absorption;
import ru.nsu.fit.g14203.evtushenko.model.Emission;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

public class View extends JPanel implements Observer {

    private Controller controller;

    private ImageZone zoneA;
    private ImageZone zoneB;
    private ImageZone zoneC;

    private PlotZone<Absorption> plotA;
    private PlotZone<Emission> plotB;


    private double scale;

    private boolean choosePart;

    public View(Controller controller) {
        this.controller = controller;
        setLayout(new GridBagLayout());

        zoneA = new ImageZone();
        zoneB = new ImageZone();
        zoneC = new ImageZone();

        plotA = new OnePlotZone(100., 1.);
        plotB = new ThreePlotZone(100., 255.);


        zoneA.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (choosePart) {
                    updateChosen(e.getX(), e.getY());
                    zoneA.setShowChosen(true);
                    zoneA.repaint();
                }
            }
        });

        zoneA.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (choosePart) {
                    updateChosen(e.getX(), e.getY());
                    zoneA.repaint();
                }
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;

        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets(5, 5, 5, 5);

        c.gridx = 0;
        add(zoneA, c);

        c.gridx = 1;
        add(zoneB, c);

        c.gridx = 2;
        add(zoneC, c);

        c.gridy = 1;

        c.gridx = 0;
        add(plotA, c);

        c.gridx = 1;
        add(plotB, c);
    }

    private void updateChosen(int x, int y) {
        int width = zoneA.getImage().getWidth(null);
        int height = zoneA.getImage().getHeight(null);
        int sizeW = Math.min((int) (350 * scale), width);
        int sizeH = Math.min((int) (350 * scale), height);
        if (x < sizeW / 2) {
            x = sizeW / 2;
        } else if (x > width - (sizeW + 1) / 2) {
            x = width - (sizeW + 1) / 2;
        }
        if (y < sizeH / 2) {
            y = sizeH / 2;
        } else if (y > height - (sizeH + 1) / 2) {
            y = height - (sizeH + 1) / 2;
        }
        zoneA.setChosen(x, y, sizeW, sizeH);
        controller.chooseImagePart((int) Math.round(x / scale), (int) Math.round(y / scale));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    public void handle(EventType type) {
        switch (type) {
            case A:
                BufferedImage image = controller.getImageA();
                if (image != null) {
                    image = fitImage(image);
                }
                zoneA.setImage(image);
                break;
            case B:
                zoneB.setImage(controller.getImageB());
                break;
            case C:
                zoneC.setImage(controller.getImageC());
                break;
            case PLOT:
                plotA.setPoints(controller.getAbsorptionPoints());
                plotB.setPoints(controller.getEmissionPoints());
                break;
        }
    }

    private BufferedImage fitImage(BufferedImage image) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        if (width > 350 || height > 350) {
            scale = 350. / Math.max(height, width);
            BufferedImage resized = new BufferedImage((int) (scale * width),
                    (int) (scale * height),
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = resized.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.scale(scale, scale);
            g.drawImage(image, 0, 0, null);
            g.dispose();
            return resized;
        } else {
            scale = 1.;
            return image;
        }
    }

    public void setChoosePart(boolean choosePart) {
        this.choosePart = choosePart;
        zoneA.setShowChosen(choosePart);
    }

}
