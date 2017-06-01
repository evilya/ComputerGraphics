package ru.nsu.fit.g14203.evtushenko.views;

import ru.nsu.fit.g14203.evtushenko.controller.RaytracingController;
import ru.nsu.fit.g14203.evtushenko.model.Matrix;
import ru.nsu.fit.g14203.evtushenko.model.Point3D;
import ru.nsu.fit.g14203.evtushenko.model.Scene;
import ru.nsu.fit.g14203.evtushenko.model.World;
import ru.nsu.fit.g14203.evtushenko.model.properties.Application;
import ru.nsu.fit.g14203.evtushenko.model.properties.CameraConverter;
import ru.nsu.fit.g14203.evtushenko.model.properties.PovConverter;
import ru.nsu.fit.g14203.evtushenko.model.shapes.LightSource;
import ru.nsu.fit.g14203.evtushenko.model.shapes.Line;
import ru.nsu.fit.g14203.evtushenko.model.shapes.Primitive;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;


public class View extends JComponent {
    private final RaytracingController raytracingController;
    private BufferedImage renderedImage = null;

    View(RaytracingController raytracingController) {
        this.raytracingController = raytracingController;

        Application application = raytracingController.getApplication();
        CameraConverter cameraConverter = application.getRender().getCameraConverter();
        PovConverter povConverter = application.getRender().getPovConverter();

        cameraConverter.addObserver(CameraConverter.Event.UP, this::repaint);
        cameraConverter.addObserver(CameraConverter.Event.VP, this::repaint);
        cameraConverter.addObserver(CameraConverter.Event.POSITION, this::repaint);

        povConverter.addObserver(PovConverter.Event.SWSH, this::repaint);
        povConverter.addObserver(PovConverter.Event.ZN, this::repaint);
        povConverter.addObserver(PovConverter.Event.ZF, this::repaint);

        application.getScene().addObserver(Scene.Event.SCENE, this::repaint);
        application.getScene().getWorld().addObserver(World.Event.ROTATION, this::repaint);

        application.addObserver(Application.Event.BG_COLOR, this::repaint);
        application.addObserver(Application.Event.CLIPPING, this::repaint);
        application.addObserver(Application.Event.BOX, this::repaint);

        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
                resetRenderedImage();
                raytracingController.onMouseWheelMoved(mouseWheelEvent);
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                raytracingController.onMousePressed(mouseEvent);
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                raytracingController.onMouseReleased();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                resetRenderedImage();
                raytracingController.onMouseDragged(mouseEvent);
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                resetRenderedImage();
                raytracingController.onWorkspaceResized();
            }
        });
    }

    private void resetRenderedImage() {
        setRenderedImage(null);
    }

    public void setRenderedImage(BufferedImage image) {
        renderedImage = image;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Application application = raytracingController.getApplication();
        PovConverter povConverter = application.getRender().getPovConverter();
        CameraConverter cameraConverter = application.getRender().getCameraConverter();

        if (renderedImage != null) {
            graphics.drawImage(renderedImage, 0, 0, null);
            return;
        }

        Dimension componentSize = getSize();
        BufferedImage canvas = new BufferedImage(componentSize.width, componentSize.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D canvasGraphics = canvas.createGraphics();

        canvasGraphics.setColor(application.getBackgroundColor());
        canvasGraphics.fillRect(0, 0, componentSize.width, componentSize.height);

        double viewPortSizeRatio = Math.min((componentSize.getWidth()) / povConverter.getsW(),
                (componentSize.getHeight()) / povConverter.getsH());
        int viewPortWidth = (int) (povConverter.getsW() * viewPortSizeRatio);
        int viewPortHeight = (int) (povConverter.getsH() * viewPortSizeRatio);
        Rectangle viewPort = new Rectangle((componentSize.width - viewPortWidth) / 2, (componentSize.height - viewPortHeight) / 2, viewPortWidth, viewPortHeight);

        Scene scene = application.getScene();

        Matrix worldToCamMatrix = cameraConverter.getWorldToCamMatrix();
        Matrix projectionMatrix = povConverter.getMatrix();
        Matrix sceneTransformMatrix = projectionMatrix.multiply(worldToCamMatrix);

        int x0 = viewPort.x;
        int y0 = viewPort.y;
        int x1 = x0 + viewPort.width;
        int y1 = y0 + viewPort.height;
        Matrix proj = new Matrix(4, 4, new double[]{
                (x1 - x0) / 2., 0, 0, (x0 + x1) / 2.,
                0, -(y1 - y0) / 2., 0, (y0 + y1) / 2.,
                0, 0, 1, 1,
                0, 0, 0, 1
        });

        drawScene(canvasGraphics, scene, sceneTransformMatrix, proj);

        graphics.drawImage(canvas, 0, 0, null);
    }

    private void drawScene(Graphics graphics, Scene scene, Matrix csTransform, Matrix displayTransform) {
        Matrix transformMatrix = csTransform.multiply(scene.getWorld().getTransform());

        for (Primitive primitive : scene.getPrimitives()) {
            drawPrimitive(graphics, primitive, transformMatrix, displayTransform);
        }

//        drawAxes(graphics, transformMatrix, displayTransform);
//        drawLights(graphics, scene, transformMatrix, displayTransform);
    }

    private void drawPrimitive(Graphics graphics, Primitive primitive, Matrix csTransform, Matrix displayTransform) {
        Matrix transformMatrix = csTransform.multiply(primitive.getWorld().getTransform());

        drawLines(graphics, primitive, transformMatrix, displayTransform);
//        drawAxes(graphics, transformMatrix, displayTransform);
    }

    private void drawLights(Graphics graphics, Scene scene, Matrix csTransform, Matrix displayTransform) {
        double size = 0.2;
        for (LightSource lightSource : scene.getLightSources()) {

            graphics.setColor(lightSource.getColor());

            Point3D a = lightSource.getPosition();

            Point3D first = new Point3D(a.getX() - size, a.getY(), a.getZ());
            Point3D second = new Point3D(a.getX() + size, a.getY(), a.getZ());
            drawLine(graphics, new Line(first, second), csTransform, displayTransform);

            first = new Point3D(a.getX(), a.getY() - size, a.getZ());
            second = new Point3D(a.getX(), a.getY() + size, a.getZ());
            drawLine(graphics, new Line(first, second), csTransform, displayTransform);

            first = new Point3D(a.getX(), a.getY(), a.getZ() - size);
            second = new Point3D(a.getX(), a.getY(), a.getZ() + size);
            drawLine(graphics, new Line(first, second), csTransform, displayTransform);
        }
    }

    private void drawLines(Graphics graphics, Primitive primitive, Matrix csTransform, Matrix displayTransform) {
        if (primitive == null) {
            return;
        }

        graphics.setColor(Color.BLACK);
        for (Line line : primitive.getLines()) {
            drawLine(graphics, line, csTransform, displayTransform);
        }
    }

    private void drawLine(Graphics graphics, Line line, Matrix csTransform, Matrix displayTransform) {
        Matrix pos1 = line.getFirst().toMatrix4();
        pos1 = csTransform.multiply(pos1);
        pos1 = pos1.divide(pos1.get(0, 3));

        Matrix pos2 = line.getSecond().toMatrix4();
        pos2 = csTransform.multiply(pos2);
        pos2 = pos2.divide(pos2.get(0, 3));

        Line visibleLine;
        if (raytracingController.getApplication().isClippingEnabled()) {
            visibleLine = clipping(new Point3D(pos1.subMatrix(0, 0, 1, 3)),
                    new Point3D(pos2.subMatrix(0, 0, 1, 3)));
            if (visibleLine == null) {
                return;
            }
        } else {
            visibleLine = new Line(new Point3D(pos1.subMatrix(0, 0, 1, 3)), new Point3D(pos2.subMatrix(0, 0, 1, 3)));
        }

        pos1 = displayTransform.multiply(visibleLine.getFirst().toMatrix4());
        pos2 = displayTransform.multiply(visibleLine.getSecond().toMatrix4());

        int x0 = (int) Math.round(pos1.get(0, 0));
        int y0 = (int) Math.round(pos1.get(0, 1));
        int x1 = (int) Math.round(pos2.get(0, 0));
        int y1 = (int) Math.round(pos2.get(0, 1));
        if (checkDisplayPos(x0, y0) || checkDisplayPos(x1, y1)) {
            graphics.drawLine(x0, y0, x1, y1);
        }
    }

    private boolean checkDisplayPos(int x, int y) {
        return x >= 0 && y >= 0 && x < getWidth() && y < getHeight();
    }

    private Line clipping(Point3D pos1, Point3D pos2) {
        boolean inViewPort1 = isInFrame(pos1);
        boolean inViewPort2 = isInFrame(pos2);

        if (inViewPort1 && inViewPort2) {
            return new Line(pos1, pos2);
        }
        if (!inViewPort1 && !inViewPort2) {
            return null;
        }

        Line2D line = new Line2D.Double(pos1.getX(), pos1.getY(), pos2.getX(), pos2.getY());
        Point2D[] corners = new Point2D[]{
                new Point2D.Double(1, 1),
                new Point2D.Double(1, -1),
                new Point2D.Double(-1, -1),
                new Point2D.Double(-1, 1),
        };

        for (int i = 0; i < corners.length; ++i) {
            int next = (i + 1) % corners.length;
            Line2D border = new Line2D.Double(corners[i], corners[next]);
            if (!border.intersectsLine(line)) {
                continue;
            }

            Point2D intersectPoint = getIntersectPoint(line, border);
            if (inViewPort1) {
                return new Line(pos1, new Point3D(intersectPoint));
            } else {
                return new Line(pos2, new Point3D(intersectPoint));
            }
        }

//        double k = (pos2.getY() - pos1.getY()) / (pos2.getX() - pos1.getX());

        if (inViewPort1) {
            return getPartSegmentZ(pos1, pos2);
        }

        return getPartSegmentZ(pos2, pos1);
    }

    private Line getPartSegmentZ(Point3D in, Point3D out) {
        double border = Double.compare(out.getZ(), -1.) < 0 ? -1. : 0;

        double t = (border - in.getZ()) / (out.getZ() - in.getZ());
        double x = in.getX() + (out.getX() - in.getX()) * t;
        double y = in.getY() + (out.getY() - in.getY()) * t;

        return new Line(in, new Point3D(x, y, border));
    }

    private Point2D getIntersectPoint(Line2D line1, Line2D line2) {
//        double k = (line1.getP2().getY() - line1.getP1().getY()) / (line1.getP2().getX() - line1.getP1().getX());

        double x1 = line1.getX1();
        double y1 = line1.getY1();
        double x2 = line1.getX2();
        double y2 = line1.getY2();
        double x3 = line2.getX1();
        double y3 = line2.getY1();
        double x4 = line2.getX2();
        double y4 = line2.getY2();

        double x = ((x2 - x1) * (x3 * y4 - x4 * y3) - (x4 - x3) * (x1 * y2 - x2 * y1)) /
                ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
        double y = ((y3 - y4) * (x1 * y2 - x2 * y1) - (y1 - y2) * (x3 * y4 - x4 * y3)) /
                ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
        return new Point2D.Double(x, y);
    }

    private boolean isInFrame(Point3D pos) {
        return Double.compare(pos.getX(), 1.) <= 0
                && Double.compare(pos.getX(), -1.) >= 0
                && Double.compare(pos.getY(), 1.) <= 0
                && Double.compare(pos.getY(), -1.) >= 0
                && Double.compare(pos.getZ(), -1.) >= 0
                && Double.compare(pos.getZ(), 0) <= 0;
    }

    private void drawAxes(Graphics graphics, Matrix csTransform, Matrix displayTransform) {
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setStroke(new BasicStroke(3));


        graphics.setColor(Color.GREEN);
        drawLine(graphics, new Line(new Point3D(0, 0, 0), new Point3D(0, 1, 0)), csTransform, displayTransform);

        graphics.setColor(Color.BLUE);
        drawLine(graphics, new Line(new Point3D(0, 0, 0), new Point3D(0, 0, 1)), csTransform, displayTransform);

        graphics.setColor(Color.RED);
        drawLine(graphics, new Line(new Point3D(0, 0, 0), new Point3D(1, 0, 0)), csTransform, displayTransform);

        graphics2D.setStroke(new BasicStroke(1));
    }
}
