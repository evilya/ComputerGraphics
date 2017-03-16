package ru.nsu.fit.g14203.evtushenko.model;

import ru.nsu.fit.g14203.evtushenko.EventType;
import ru.nsu.fit.g14203.evtushenko.Observable;
import ru.nsu.fit.g14203.evtushenko.model.filters.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Model extends Observable implements Runnable {

    private BufferedImage saved;

    private BufferedImage imageA;
    private BufferedImage imageB;
    private BufferedImage imageC;

    private BlockingQueue<FilterParameters> taskQueue = new ArrayBlockingQueue<>(5);

    public void loadImage(String path) throws IOException {
        imageA = ImageIO.read(new File(path));
        System.out.println("Original size: " + imageA.getWidth() + " " + imageA.getHeight());
        notifyObservers(EventType.A);
    }

    public void chooseImagePart(int centerX, int centerY) {
        int width = Math.min(350, imageA.getWidth());
        int height = Math.min(350, imageA.getHeight());
        imageB = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imageB.createGraphics();
        g.drawImage(imageA, 0, 0,
                width, height,
                Math.max(centerX - width / 2, 0), Math.max(centerY - height / 2, 0),
                centerX + width / 2, centerY + height / 2,
                null);
        g.dispose();
        notifyObservers(EventType.B);
    }

    public void applyFilter(FilterParameters parameters) {
        taskQueue.clear();
        taskQueue.add(parameters);
    }

    public BufferedImage getImageA() {
        return imageA;
    }

    public BufferedImage getImageB() {
        return imageB;
    }

    public BufferedImage getImageC() {
        return imageC;
    }

    @Override
    public void run() {
        try {
            while (true) {
                FilterParameters parameters = taskQueue.take();
                Filter filter = null;
                switch (parameters.getType()) {
                    case BLACK_AND_WHITE:
                        filter = new BlackAndWhiteFilter();
                        break;
                    case NEGATIVE:
                        filter = new NegativeFilter();
                        break;
                    case SHARPNESS:
                        filter = new SharpnessFilter();
                        break;
                    case STAMPING:
                        filter = new StampingFilter();
                        break;
                    case BLUR:
                        filter = new BlurFilter();
                        break;
                    case AQUARELLE:
                        filter = new AquarelleFilter();
                        break;
                    case FLOYD_STEINBERG:
                        filter = new FloydSteinbergFilter(parameters.getParameters());
                        break;
                    case ORDERED_DITHERING:
                        filter = new OrderedDitheringFilter();
                        break;
                    case ROBERTS:
                        filter = new RobertsFilter(parameters.getParameters());
                        break;
                    case ROTATION:
                        filter = new RotationFilter(parameters.getParameters());
                        break;
                    case ZOOM:
                        filter = new ZoomFilter();
                        break;
                    case GAMMA:
                        filter = new GammaFilter(parameters.getParameters());
                        break;
                    case SOBEL:
                        filter = new SobelFilter(parameters.getParameters());
                        break;
                }
                imageC = filter.apply(imageB);
                notifyObservers(EventType.C);
            }
        } catch (InterruptedException e) {
            return;
        }
    }

    public void moveLeft() {
        if (imageC != null) {
            int width = Math.min(350, imageC.getWidth());
            int height = Math.min(350, imageC.getHeight());
            imageB = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = imageB.createGraphics();
            g.drawImage(imageC, 0, 0, null);
            g.dispose();
            notifyObservers(EventType.B);
        }
    }

    public void clear() {
        imageA = null;
        imageB = null;
        imageC = null;
        notifyObservers(EventType.A);
        notifyObservers(EventType.B);
        notifyObservers(EventType.C);
    }

    public void saveState() {
        saved = imageC;
    }

    public void rollback() {
        imageC = saved;
        notifyObservers(EventType.C);
    }
}
