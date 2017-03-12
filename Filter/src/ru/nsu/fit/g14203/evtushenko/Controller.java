package ru.nsu.fit.g14203.evtushenko;

import ru.nsu.fit.g14203.evtushenko.model.FilterParameters;
import ru.nsu.fit.g14203.evtushenko.model.Model;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class Controller {


    private boolean partChosen;

    private Model model;

    public Controller(Model model) {
        this.model = model;
    }

    public void loadImage(String path) throws IOException {
        model.loadImage(path);
    }

    public void chooseImagePart(int centerX, int centerY) {
        partChosen = true;
        model.chooseImagePart(centerX, centerY);
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

    public boolean isPartChosen() {
        return partChosen;
    }
}
