package ru.nsu.fit.g14203.evtushenko.views;

import ru.nsu.fit.g14203.evtushenko.controller.RaytracingController;
import ru.nsu.fit.g14203.evtushenko.model.Intersection;
import ru.nsu.fit.g14203.evtushenko.model.Matrix;
import ru.nsu.fit.g14203.evtushenko.model.RenderProgress;
import ru.nsu.fit.g14203.evtushenko.model.Scene;
import ru.nsu.fit.g14203.evtushenko.model.properties.CameraConverter;
import ru.nsu.fit.g14203.evtushenko.model.properties.OpticalParameters;
import ru.nsu.fit.g14203.evtushenko.model.properties.PovConverter;
import ru.nsu.fit.g14203.evtushenko.model.properties.Render;
import ru.nsu.fit.g14203.evtushenko.model.shapes.LightSource;
import ru.nsu.fit.g14203.evtushenko.model.shapes.Primitive;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;


public class Raytracer extends Thread {
    private final Object taskMonitor = new Object();
    private final RaytracingController raytracingController;
    private final List<Consumer<RenderProgress>> progressListener = new CopyOnWriteArrayList<>();
    private boolean hasNext = false;
    private Render curTask;
    private Render nextTask;

    public Raytracer(RaytracingController raytracingController) {
        this.raytracingController = raytracingController;
    }

    public void addProgressConsumer(Consumer<RenderProgress> consumer) {
        progressListener.add(consumer);
    }

    public void cancelTask() {
        synchronized (taskMonitor) {
            nextTask = null;
            hasNext = true;
            taskMonitor.notifyAll();
        }
    }

    public void addTask(Render render) {
        synchronized (taskMonitor) {
            nextTask = render;
            hasNext = true;
            taskMonitor.notifyAll();
        }
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            synchronized (taskMonitor) {
                while (!hasNext) {
                    try {
                        taskMonitor.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                curTask = nextTask;
                nextTask = null;
                hasNext = false;
            }
            if (curTask != null) {
                render();
            }
        }
    }

    private void notifyProgressConsumers(RenderProgress progress) {
        progressListener.forEach(e -> e.accept(progress));
    }

    private void render() {
        View view = raytracingController.getMainFrame().getView();
        PovConverter povConverter = curTask.getPovConverter();
        CameraConverter cameraConverter = curTask.getCameraConverter();

        double viewPortSizeRatio = Math.min((view.getWidth() - 1) / povConverter.getsW(),
                (view.getHeight() - 1) / povConverter.getsH());

        int viewPortWidth = (int) (povConverter.getsW() * viewPortSizeRatio);
        int viewPortHeight = (int) (povConverter.getsH() * viewPortSizeRatio);

        Rectangle viewPort = new Rectangle((view.getWidth() - viewPortWidth) / 2,
                (view.getHeight() - viewPortHeight) / 2,
                viewPortWidth,
                viewPortHeight);
        int x0 = viewPort.x;
        int y0 = viewPort.y;
        int x1 = x0 + viewPort.width;
        int y1 = y0 + viewPort.height;


        Matrix sceneMatrix = raytracingController.getApplication().getScene().getWorld().getTransform();
        Matrix worldToCamMatrix = cameraConverter.getWorldToCamMatrix();
        Matrix transformMatrix = worldToCamMatrix.multiply(sceneMatrix);
        raytracingController.getApplication().getScene().rotate(transformMatrix);

        BufferedImage image = new BufferedImage(view.getWidth(), view.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Matrix startPos = new Matrix(1, 3, new double[]{0, 0, 0});

        int step = 1;
        int rays = 2;
        double offset;

        offset = (double) step / 2. / rays;
        double dStep = (double) rays / step;

        ResultColor[][] colors = new ResultColor[image.getWidth()][];
        for (int i = 0; i < image.getWidth(); ++i) {
            colors[i] = new ResultColor[image.getHeight()];
        }

        double min = 255.;
        double max = 0.;
        double[] r = new double[rays * rays];
        double[] g = new double[rays * rays];
        double[] b = new double[rays * rays];
        for (int x = x0; x <= x1; x += step) {
            for (int y = y0; y <= y1; y += step) {
                synchronized (taskMonitor) {
                    if (hasNext) {
                        notifyProgressConsumers(new RenderProgress(RenderProgress.State.CANCELED));
                        return;
                    }
                }

                double currentX = x + offset;
                double currentY = y + offset;

                Arrays.fill(r, 0);
                Arrays.fill(g, 0);
                Arrays.fill(b, 0);

                for (int iX = 0; iX < rays; ++iX) {
                    for (int iY = 0; iY < rays; ++iY) {
                        double realX = (currentX + iX * step / dStep - (x0 + x1) / 2.) / viewPortSizeRatio;
                        double realY = (currentY + iY * step / dStep - (y0 + y1) / 2.) / -viewPortSizeRatio;

                        Matrix direction = new Matrix(
                                1,
                                3,
                                new double[]{realX, realY, -povConverter.getzN()}
                        ).normalize();

                        ResultColor color = traceOne(startPos, direction, curTask.getDepth());
                        r[iX * rays + iY] = color.r;
                        g[iX * rays + iY] = color.g;
                        b[iX * rays + iY] = color.b;
                    }
                }

                colors[x][y] = new ResultColor(
                        Arrays.stream(r).average().orElse(0),
                        Arrays.stream(g).average().orElse(0),
                        Arrays.stream(b).average().orElse(0)
                );

                min = Math.min(min, Math.min(colors[x][y].r, Math.min(colors[x][y].g, colors[x][y].b)));
                max = Math.max(max, Math.max(colors[x][y].r, Math.max(colors[x][y].g, colors[x][y].b)));

                for (int iX = 0; iX < step; ++iX) {
                    if (iX + x > x1) {
                        break;
                    }
                    for (int iY = 0; iY < step; ++iY) {
                        if (iY + y > y1) {
                            break;
                        }
                        colors[x + iX][y + iY] = colors[x][y];
                    }
                }
            }

            notifyProgressConsumers(new RenderProgress((double) (x - x0) / viewPort.getWidth(),
                    RenderProgress.State.RENDERING));
        }

        double ratio = 255. / max;
        for (int x = x0; x <= x1; ++x) {
            for (int y = y0; y <= y1; ++y) {
                Color color = new Color(
                        (int) Math.round(Math.max(0, colors[x][y].r) * ratio),
                        (int) Math.round(Math.max(0, colors[x][y].g) * ratio),
                        (int) Math.round(Math.max(0, colors[x][y].b) * ratio)
                );
                image.setRGB(x, y, color.getRGB());
            }
        }

//        Graphics2D graphics = image.createGraphics();
//        graphics.setPaint(Color.RED);
//        graphics.draw(viewPort);


        //GAMMA CORRECTION
        notifyProgressConsumers(new RenderProgress(1., RenderProgress.State.READY, image));
    }

    private ResultColor traceOne(Matrix startPos, Matrix direction, int depth) {
        if (depth == 0) {
            return new ResultColor(Color.BLACK);
        }

        Intersection intersection = findIntersection(startPos, direction);
        if (intersection == null) {
            return new ResultColor(curTask.getAmbient());
        }

        OpticalParameters opticalParameters = intersection.getPrimitive().getOpticalParameters();
        Color backgroundColor = curTask.getAmbient();

        ResultColor resultColor = new ResultColor();
        resultColor.r = backgroundColor.getRed() * opticalParameters.getD().getR();
        resultColor.g = backgroundColor.getGreen() * opticalParameters.getD().getG();
        resultColor.b = backgroundColor.getBlue() * opticalParameters.getD().getB();

        Matrix normal = intersection.getNormal();
        Matrix view = startPos.subtract(intersection.getPosition()).normalize();

        Scene scene = raytracingController.getApplication().getScene();
        for (LightSource lightSource : scene.getRotatedLightSources()) {
            Matrix lightPosition = lightSource.getPosition().toMatrix3();
            Matrix toLight = lightPosition.subtract(intersection.getPosition());
            Matrix toLightNormalized = toLight.normalize();

            double d = Math.sqrt(toLight.get(0, 0) * toLight.get(0, 0) +
                    toLight.get(0, 1) * toLight.get(0, 1) +
                    toLight.get(0, 2) * toLight.get(0, 2));


            Intersection lightSourceIntersection
                    = findIntersection(intersection.getPosition().add(toLightNormalized.multiply(0.00001)),
                    toLightNormalized);

            if (lightSourceIntersection != null && Double.compare(lightSourceIntersection.getDistance(), d) < 0) {
                continue;
            }

            double f = 1. / (1. + d);

            Matrix h = toLightNormalized.add(view).normalize();

            double nl = normal.scalarProduct(toLightNormalized);

            double nh = normal.scalarProduct(h);

            Color lightColor = lightSource.getColor();
            resultColor.r += f * lightColor.getRed() * (nl * opticalParameters.getD().getR() + nh * opticalParameters.getS().getR());
            resultColor.g += f * lightColor.getGreen() * (nl * opticalParameters.getD().getG() + nh * opticalParameters.getS().getG());
            resultColor.b += f * lightColor.getBlue() * (nl * opticalParameters.getD().getB() + nh * opticalParameters.getS().getB());
        }

        Matrix R = normal.multiply(2 * normal.scalarProduct(view)).subtract(view);

        ResultColor mirrored = traceOne(intersection.getPosition().add(R.multiply(0.0001)), R, depth - 1);

        resultColor.r += mirrored.r * opticalParameters.getS().getR();
        resultColor.g += mirrored.g * opticalParameters.getS().getG();
        resultColor.b += mirrored.b * opticalParameters.getS().getB();

        return resultColor;
    }

    private Intersection findIntersection(Matrix startPos, Matrix direction) {
        Intersection result = null;
        for (Primitive primitive : raytracingController.getApplication().getScene().getRotatedPrimitives()) {
            Intersection in = primitive.getIntersection(startPos, direction);

            if (in == null) {
                continue;
            }

            if (result == null || in.getDistance() < result.getDistance()) {
                result = in;
            }
        }
        return result;
    }

    private class ResultColor {
        double r;
        double g;
        double b;

        ResultColor() {
        }

        ResultColor(Color color) {
            r = color.getRed();
            g = color.getGreen();
            b = color.getBlue();
        }

        ResultColor(double r, double g, double b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }
    }
}
