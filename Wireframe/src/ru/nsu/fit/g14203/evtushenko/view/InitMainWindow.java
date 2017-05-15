package ru.nsu.fit.g14203.evtushenko.view;

import ru.nsu.fit.g14203.evtushenko.model.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class InitMainWindow extends MainFrame {

    private final Model model;

    public InitMainWindow() {
        super(800, 800, "Wireframe");
        try {
            initMenu();
            initToolbar();
            connectToggles();
            model = new Model();
            Thread modelThread = new Thread(model);

            View view = new View(model);
            model.addObserver(view);

            JScrollPane scrollPane = new JScrollPane(view);
            scrollPane.getVerticalScrollBar().setUnitIncrement(50);
            modelThread.start();
            add(scrollPane);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        InitMainWindow mainFrame = new InitMainWindow();
        mainFrame.setVisible(true);
    }

    private void initToolbar() throws NoSuchMethodException {
        toolBar = new JToolBar("Main toolbar");
        toolBar.setRollover(true);
        toolBar.setFloatable(false);
        add(toolBar, BorderLayout.NORTH);
        addToolBarButton("File/Open");
        addToolBarButton("File/Settings");
        addToolBarSeparator();
        addToolBarButton("Help/About");
        addToolBarButton("File/Exit");
    }

    private void initMenu() throws NoSuchMethodException {
        addSubMenu("File", KeyEvent.VK_F);
        addMenuItem("File/Open", "Open", KeyEvent.VK_O, "open.png", "onOpen");
        addMenuItem("File/Settings", "Settings", KeyEvent.VK_O, "settings.png", "onSettings");
        addMenuSeparator("File");
        addMenuItem("File/Exit", "Exit", KeyEvent.VK_X, "exit.png", "onExit");

        addSubMenu("Help", KeyEvent.VK_H);
        addMenuItem("Help/About", "Show program version and copyright information", KeyEvent.VK_A, "about.png", "onAbout");
    }

    private void connectToggles() {
    }

    public void onAbout() {
        JOptionPane.showMessageDialog(this,
                "Version 1.0\nEvtushenko Ilya, 14203 FIT NSU", "About",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void onExit() {
        System.exit(0);
    }

    public void onOpen() {

    }

    public void onSettings() {
        new SplineDialog(model);
    }

}
