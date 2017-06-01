package ru.nsu.fit.g14203.evtushenko;

import ru.nsu.fit.g14203.evtushenko.controller.RaytracingController;

import javax.swing.*;


class Raytracing {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ignored) {
        }
        new RaytracingController().run();
    }
}

