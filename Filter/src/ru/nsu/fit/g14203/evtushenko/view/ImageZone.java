package ru.nsu.fit.g14203.evtushenko.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageZone extends JPanel {

    private BufferedImage image;
    private int x;
    private int y;
    private int sizeW;
    private int sizeH;
    private boolean showChosen = false;


    public ImageZone() {
        setBorder(BorderFactory.createDashedBorder(Color.BLACK, 1.f, 5.f, 2.f, true));
        image = new BufferedImage(350, 350, BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
        if (showChosen) {
            g.setXORMode(Color.WHITE);
            g.drawLine(x - sizeW / 2, y - sizeH / 2, x - sizeW / 2, y + sizeH / 2 - 1);
            g.drawLine(x - sizeW / 2, y - sizeH / 2, x + sizeW / 2 - 1, y - sizeH / 2);
            g.drawLine(x + sizeW / 2 - 1, y + sizeH / 2 - 1, x - sizeW / 2, y + sizeH / 2 - 1);
            g.drawLine(x + sizeW / 2 - 1, y + sizeH / 2 - 1, x + sizeW / 2 - 1, y - sizeH / 2);
            g.setPaintMode();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(350, 350);
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setChosen(int x, int y, int sizeW, int sizeH) {
        this.x = x;
        this.y = y;
        this.sizeW = sizeW;
        this.sizeH = sizeH;
    }

    public void setShowChosen(boolean showChosen) {
        this.showChosen = showChosen;
    }
}
