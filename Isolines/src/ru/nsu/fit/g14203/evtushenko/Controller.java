package ru.nsu.fit.g14203.evtushenko;

import ru.nsu.fit.g14203.evtushenko.model.Model;
import ru.nsu.fit.g14203.evtushenko.view.InitMainWindow;

public class Controller {

    private final InitMainWindow mainWindow;
    private Model model;

    public Controller(Model model, InitMainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.model = model;
    }
}
