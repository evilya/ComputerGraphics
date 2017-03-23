package ru.nsu.fit.g14203.evtushenko;

import ru.nsu.fit.g14203.evtushenko.model.Absorption;
import ru.nsu.fit.g14203.evtushenko.model.Emission;
import ru.nsu.fit.g14203.evtushenko.model.FilterParameters;
import ru.nsu.fit.g14203.evtushenko.model.Model;
import ru.nsu.fit.g14203.evtushenko.view.InitMainWindow;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class Controller {

    private final InitMainWindow mainWindow;
    private Model model;

    public Controller(Model model, InitMainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.model = model;
    }

    public void loadImage(String path) throws IOException {
        model.loadImage(path);
    }

    public void chooseImagePart(int centerX, int centerY) {
        mainWindow.setPartChosen(true);
        model.chooseImagePart(centerX, centerY);
    }


    public void moveLeft() {
        model.moveLeft();
    }

    public void applyFilter(FilterParameters parameters) {
        mainWindow.setSaveEnabled(true);
        model.applyFilter(parameters);
    }

    public BufferedImage getImageA() {
        return model.getImageA();
    }

    public BufferedImage getImageB() {
        return model.getImageB();
    }

    public BufferedImage getImageC() {
        return model.getImageC();
    }

    public void clear() {
        mainWindow.setSaveEnabled(false);
        mainWindow.setChoseEnabled(false);
        mainWindow.setPartChosen(false);
        model.clear();
    }

    public void saveState() {
        model.saveState();
    }

    public void rollback() {
        model.rollback();
    }


    public List<Absorption> getAbsorptionPoints() {
        return model.getAbsorptionPoints();
    }


    public List<Emission> getEmissionPoints() {
        return model.getEmissionPoints();
    }

    public void loadRenderingParameters(String pathname) {
        model.loadRenderingParameters(pathname);
    }
}
