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

    private void drawNegativeLine(Graphics g, int x1, int y1, int x2, int y2) {
        int offsetX, offsetY, dirX, dirY;
        int pdx = 0, pdy = 0; //describes chosen line direction

        offsetX = x2 - x1;
        offsetY = y2 - y1;

        dirX = Integer.signum(offsetX);
        dirY = Integer.signum(offsetY);

        offsetX = Math.abs(offsetX);
        offsetY = Math.abs(offsetY);

        if (offsetX > offsetY) {
            pdx = dirX;
        } else {
            pdy = dirY;
        }

        int x = x1;
        int y = y1;
        int err = Math.max(offsetX, offsetY) / 2;
//        image.setRGB(x, y, color.getRGB());
        Color c = new Color(image.getRGB(x, y));
        g.setColor(new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue()));
        g.drawLine(x, y, x, y);

        for (int t = 0; t < Math.max(offsetX, offsetY); t++) {
            err -= Math.min(offsetX, offsetY);
            if (err < 0) {
                err += Math.max(offsetX, offsetY);
                x += dirX;
                y += dirY;
            } else {
                x += pdx;
                y += pdy;
            }
//            image.setRGB(x, y, color.getRGB());
            c = new Color(image.getRGB(x, y));
            g.setColor(new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue()));
            g.drawLine(x, y, x, y);
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
