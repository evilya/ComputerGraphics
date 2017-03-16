package ru.nsu.fit.g14203.evtushenko;

import ru.nsu.fit.g14203.evtushenko.model.FilterParameters;
import ru.nsu.fit.g14203.evtushenko.model.Model;
import ru.nsu.fit.g14203.evtushenko.view.InitMainWindow;

import java.awt.image.BufferedImage;
import java.io.IOException;

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

    public void clear(){
        mainWindow.setChoseEnabled(false);
        mainWindow.setPartChosen(false);
        model.clear();
    }

}
