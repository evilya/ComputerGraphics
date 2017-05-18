package ru.nsu.fit.g14203.evtushenko.view;

import ru.nsu.fit.g14203.evtushenko.model.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class InitMainWindow extends MainFrame {

    private final Model model;
    private final JScrollPane scrollPane;

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

            scrollPane = new JScrollPane(view);
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
        addToolBarButton("File/Save");
        addToolBarButton("File/Settings");
        addToolBarButton("File/Init");
        addToolBarSeparator();
        addToolBarButton("Help/About");
        addToolBarButton("File/Exit");
    }

    private void initMenu() throws NoSuchMethodException {
        addSubMenu("File", KeyEvent.VK_F);
        addMenuItem("File/Open", "Open", KeyEvent.VK_O, "open.png", "onOpen");
        addMenuItem("File/Save", "Save", KeyEvent.VK_O, "save.png", "onSave");
        addMenuItem("File/Settings", "Settings", KeyEvent.VK_O, "settings.png", "onSettings");
        addMenuItem("File/Init", "Init", KeyEvent.VK_O, "clear.png", "onInit");
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
        JFileChooser fileChooser = new JFileChooser("FIT_14203_Evtushenko_Ilya_WireFrame_Data");
        int res = fileChooser.showDialog(this, "Open");
        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                model.loadFromFile(fileChooser.getSelectedFile());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Can not open file");
            }
        }
        scrollPane.revalidate();
    }

    public void onSettings() {
        new SplineDialog(model);
    }

    public void onSave() {
        JFileChooser fileChooser = new JFileChooser("FIT_14203_Evtushenko_Ilya_Wireframe_Data");
        int res = fileChooser.showDialog(this, "Save");
        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                model.saveToFile(fileChooser.getSelectedFile());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Can not open file");
            }
        }
    }

    public void onInit() {
        model.init();
    }
}
