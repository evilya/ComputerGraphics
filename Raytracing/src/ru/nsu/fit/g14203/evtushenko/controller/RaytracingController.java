package ru.nsu.fit.g14203.evtushenko.controller;

import ru.nsu.fit.g14203.evtushenko.model.Loader;
import ru.nsu.fit.g14203.evtushenko.model.Matrix;
import ru.nsu.fit.g14203.evtushenko.model.RenderProgress;
import ru.nsu.fit.g14203.evtushenko.model.World;
import ru.nsu.fit.g14203.evtushenko.model.properties.Application;
import ru.nsu.fit.g14203.evtushenko.model.properties.PovConverter;
import ru.nsu.fit.g14203.evtushenko.views.MainFrame;
import ru.nsu.fit.g14203.evtushenko.views.Raytracer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.io.File;


public class RaytracingController {
    private Application application;
    private JFileChooser fileChooser;

    private Point2D prevPosition;

    private Raytracer raytracer;
    private MainFrame mainFrame;

    public void run() {
        fileChooser = new JFileChooser();
        File workingDirectory
                = new File(System.getProperty("user.dir")
                + File.separator
                + "FIT_14203_Evtushenko_Ilya_Raytracing_Data");
        fileChooser.setCurrentDirectory(workingDirectory);
        FileNameExtensionFilter settingsFileFilter
                = new FileNameExtensionFilter("Scenes (*.scene)", "scene");
        fileChooser.setFileFilter(settingsFileFilter);

        application = new Application();
        raytracer = new Raytracer(this);
        raytracer.addProgressConsumer(this::onRenderProgress);
        raytracer.start();
        mainFrame = new MainFrame(this);
        mainFrame.setVisible(true);
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    public Application getApplication() {
        return application;
    }

    private void onRenderProgress(RenderProgress progress) {
        switch (progress.getState()) {
            case RENDERING:
                mainFrame.getStatusBar().setMessage(String.format("Progress: %.2f%%", progress.getPercent() * 100.));
                break;

            case READY:
                SwingUtilities.invokeLater(() -> mainFrame.getView().setRenderedImage(progress.getImage()));

            case CANCELED:
                mainFrame.getStatusBar().setMessage("");
                break;
        }
    }

    public void onWorkspaceResized() {
        raytracer.cancelTask();
    }

    public void onOpenButtonClicked() {
        raytracer.cancelTask();
        int result = fileChooser.showOpenDialog(mainFrame);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            File sceneFile = fileChooser.getSelectedFile();
            Loader loader = new Loader(application, sceneFile);
            loader.load();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainFrame, "Can not load file: " + e.getMessage(),
                    "Open error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void onMouseWheelMoved(MouseWheelEvent event) {
        raytracer.cancelTask();
        PovConverter povConverter = application.getRender().getPovConverter();
        double zn = povConverter.getzN();
        zn += 0.5 * event.getWheelRotation();
        povConverter.setzN(zn);
    }

    public void onMousePressed(MouseEvent mouseEvent) {
        raytracer.cancelTask();
        prevPosition = mouseEvent.getPoint();
    }

    public void onMouseReleased() {
        raytracer.cancelTask();
        prevPosition = null;
    }

    public void onMouseDragged(MouseEvent mouseEvent) {
        raytracer.cancelTask();

        Point2D pos = mouseEvent.getPoint();
        if (prevPosition == null) {
            prevPosition = pos;
            return;
        }

        double x = (pos.getX() - prevPosition.getX()) * 0.5;
        double y = (pos.getY() - prevPosition.getY()) * 0.5;

        double sin = Math.sin(Math.PI / 180. * x);
        double cos = Math.cos(Math.PI / 180. * x);
        Matrix rotateY = new Matrix(4, 4, new double[]{
                cos, 0, sin, 0,
                0, 1, 0, 0,
                -sin, 0, cos, 0,
                0, 0, 0, 1
        });


        Matrix rotateX;
        sin = Math.sin(Math.PI / 180. * y);
        cos = Math.cos(Math.PI / 180. * y);
        rotateX = new Matrix(4, 4, new double[]{
                cos, -sin, 0, 0,
                sin, cos, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        });


        Matrix mouseRotation = rotateY.multiply(rotateX);
        World world = application.getScene().getWorld();
        Matrix csRotation = world.getRotate();
        csRotation = mouseRotation.multiply(csRotation);
        world.setRotate(csRotation);

        prevPosition = pos;
    }

    public void onRenderButtonClicked() {
        try {
            raytracer.addTask(application.getRender().clone());
        } catch (CloneNotSupportedException e) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Can't start render: " + e.getMessage(),
                    "Render error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void onEnterToolbarButton(MouseEvent event) {
        Component component = event.getComponent();
        if (!(component instanceof JComponent)) {
            return;
        }

        JComponent button = ((JComponent) component);
        mainFrame.getStatusBar().setMessage(button.getToolTipText());
    }

    public void onExitToolbarButton() {
        mainFrame.getStatusBar().setMessage("");
    }
}
