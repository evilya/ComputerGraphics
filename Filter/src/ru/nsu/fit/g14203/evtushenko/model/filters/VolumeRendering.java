package ru.nsu.fit.g14203.evtushenko.model.filters;

import ru.nsu.fit.g14203.evtushenko.model.Charge;
import ru.nsu.fit.g14203.evtushenko.model.Model;
import ru.nsu.fit.g14203.evtushenko.utils.MathUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class VolumeRendering implements Filter {

    private final static ExecutorService EXECUTOR = Executors.newFixedThreadPool(4);

    private Model model;

    private double fMax;
    private double fMin;
    private int maxX;
    private int maxY;
    private int maxZ;

    private double dx;
    private double dy;
    private double dz;

    public VolumeRendering(double[] parameters, Model model) {
        maxX = (int) parameters[0];
        maxY = (int) parameters[1];
        maxZ = (int) parameters[2];
        dx = 1. / maxX;
        dy = 1. / maxY;
        dz = 1. / maxZ;
        this.model = model;
    }

    @Override
    public BufferedImage apply(BufferedImage source) {
        int width = source.getWidth();
        int height = source.getHeight();
        BufferedImage result = new BufferedImage(width, height, source.getType());

        findMinMax();

        List<Future<Void>> futures = new ArrayList<>();
        for (int x = 0; x < source.getWidth(); x++) {
            for (int y = 0; y < source.getHeight(); y++) {
                int finalY = y;
                int finalX = x;
                futures.add(EXECUTOR.submit(() -> renderPixel(source, result, finalX, finalY)));
            }
        }
        try {
            for (Future<Void> f : futures) {
                f.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
        return result;
    }

    private Void renderPixel(BufferedImage source, BufferedImage result, int x, int y) {
        Color color = new Color(source.getRGB(x, y));
        double red = (double) color.getRed() / 255;
        double green = (double) color.getGreen() / 255;
        double blue = (double) color.getBlue() / 255;
        for (int z = 0; z < maxZ; z++) {
            double value = calculateFunction(x / (350 / maxX), y / (350 / maxY), z);
            int quant = (int) Math.round((value - fMin) / (fMax - fMin) * 100);
            double absorption = Math.exp(-(model.getAbsorption()[quant] * dz));
            red = red * absorption + (double) model.getEmission()[quant][0] / 255 * dz;
            green = green * absorption + (double) model.getEmission()[quant][1] / 255 * dz;
            blue = blue * absorption + (double) model.getEmission()[quant][2] / 255 * dz;
        }
        result.setRGB(x, y, new Color
                (MathUtils.constraint((int) (red * 255)),
                        MathUtils.constraint((int) (green * 255)),
                        MathUtils.constraint((int) (blue * 255))).getRGB());
        return null;
    }

    private double calculateFunction(int x, int y, int z) {
        double centerX = (x + 0.5) * dx;
        double centerY = (y + 0.5) * dy;
        double centerZ = (z + 0.5) * dz;

        double value = 0.0;

        for (Charge c : model.getCharges()) {
            double distance = Math.sqrt(
                    Math.pow(Math.abs(centerX - c.getX()), 2)
                            + Math.pow(Math.abs(centerY - c.getY()), 2)
                            + Math.pow(Math.abs(centerZ - c.getZ()), 2));
            distance = Math.max(0.1, distance);
            value += c.getQ() / distance;
        }
        return value;
    }

    private void findMinMax() {
        fMin = Double.MAX_VALUE;
        fMax = Double.MIN_VALUE;
        double value;
        for (int x = 0; x < maxX; x++) {
            for (int y = 0; y < maxY; y++) {
                for (int z = 0; z < maxZ; z++) {
                    value = calculateFunction(x, y, z);
                    fMin = (value < fMin) ? value : fMin;
                    fMax = (value > fMax) ? value : fMax;
                }
            }
        }
    }
}
