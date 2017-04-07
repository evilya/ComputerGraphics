package ru.nsu.fit.g14203.evtushenko.view;

import ru.nsu.fit.g14203.evtushenko.model.Config;
import ru.nsu.fit.g14203.evtushenko.model.FileLoader;
import ru.nsu.fit.g14203.evtushenko.utils.ExtensionFileFilter;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.KeyEvent;

public class InitMainWindow extends MainFrame {
    private final View view;

    private JToggleButton interpolationToggle;
    private JToggleButton isolinesToggle;
    private JToggleButton gridToggle;
    private JToggleButton pointsToggle;
    private JToggleButton mapToggle;
    private JRadioButtonMenuItem interpolationItem;
    private JRadioButtonMenuItem isolinesItem;
    private JRadioButtonMenuItem gridItem;
    private JRadioButtonMenuItem pointsItem;
    private JRadioButtonMenuItem mapItem;

    public InitMainWindow() {
        super(1120, 620, "Isolines");
        try {
            setLayout(new BorderLayout());
            JPanel statusPanel = new JPanel();
            statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
            add(statusPanel, BorderLayout.SOUTH);
            statusPanel.setPreferredSize(new Dimension(getWidth(), 16));
            statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
            JLabel statusLabel = new JLabel("");
            statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
            statusPanel.add(statusLabel);
            initMenu();
            initToolbar();
            connectToggles();
            view = new View(statusLabel);

            add(view);

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
        addToolBarSeparator();
        addToolBarButton("View/Settings");
        interpolationToggle = addToolBarToggleButton("View/Interpolation");
        isolinesToggle = addToolBarToggleButton("View/Isolines");
        gridToggle = addToolBarToggleButton("View/Grid");
        pointsToggle = addToolBarToggleButton("View/Points");
        mapToggle = addToolBarToggleButton("View/Map");
        addToolBarSeparator();
        addToolBarButton("Help/About");
        addToolBarButton("File/Exit");
    }

    private void initMenu() throws NoSuchMethodException {
        addSubMenu("File", KeyEvent.VK_F);
        addMenuItem("File/Open", "Open", KeyEvent.VK_O, "open.png", "onOpen");
        addMenuSeparator("File");
        addMenuItem("File/Exit", "Exit", KeyEvent.VK_X, "exit.png", "onExit");

        addSubMenu("View", KeyEvent.VK_V);
        addRadioMenuItem("View/Settings", "Settings", 0, "settings.png", "onSettings");
        interpolationItem = (JRadioButtonMenuItem) addRadioMenuItem("View/Interpolation", "Enable interpolation", 0, "impacts.png", "onInterpolation");
        isolinesItem = (JRadioButtonMenuItem) addRadioMenuItem("View/Isolines", "Enable isolines", 0, "left.png", "onIsolines");
        gridItem = (JRadioButtonMenuItem) addRadioMenuItem("View/Grid", "Enable grid", 0, "xor.png", "onGrid");
        pointsItem = (JRadioButtonMenuItem) addRadioMenuItem("View/Points", "Enable points", 0, "replace.png", "onPoints");
        mapItem = (JRadioButtonMenuItem) addRadioMenuItem("View/Map", "Enable map", 0, "select.png", "onMap");

        addSubMenu("Help", KeyEvent.VK_H);
        addMenuItem("Help/About", "Show program version and copyright information", KeyEvent.VK_A, "about.png", "onAbout");
    }

    private void connectToggles() {
        interpolationItem.addActionListener(e -> interpolationToggle.setSelected(interpolationItem.isSelected()));
        interpolationToggle.addActionListener(e -> interpolationItem.setSelected(interpolationToggle.isSelected()));

        isolinesItem.addActionListener(e -> isolinesToggle.setSelected(isolinesItem.isSelected()));
        isolinesToggle.addActionListener(e -> isolinesItem.setSelected(isolinesToggle.isSelected()));

        gridItem.addActionListener(e -> gridToggle.setSelected(gridItem.isSelected()));
        gridToggle.addActionListener(e -> gridItem.setSelected(gridToggle.isSelected()));

        pointsItem.addActionListener(e -> pointsToggle.setSelected(pointsItem.isSelected()));
        pointsToggle.addActionListener(e -> pointsItem.setSelected(pointsToggle.isSelected()));

        mapItem.addActionListener(e -> mapToggle.setSelected(mapItem.isSelected()));
        mapToggle.addActionListener(e -> mapItem.setSelected(mapToggle.isSelected()));
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
        JFileChooser fileChooser = new JFileChooser("FIT_14203_Evtushenko_Ilya_Isolines_Data");
        fileChooser.addChoosableFileFilter(new ExtensionFileFilter("png", "PNG24"));
        fileChooser.addChoosableFileFilter(new ExtensionFileFilter("bmp", "BMP24"));
        int res = fileChooser.showDialog(this, "Open");
        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                Config config =
                        FileLoader.readConfig(fileChooser.getSelectedFile().getAbsolutePath());
                view.setConfig(config);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Can not open this file",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    public void onInterpolation() {
        view.setInterpolation(interpolationItem.isSelected());
    }

    public void onIsolines() {
        view.setIsolines(isolinesItem.isSelected());
    }

    public void onPoints() {
        view.setPoints(pointsItem.isSelected());
    }

    public void onGrid() {
        view.setGrid(gridItem.isSelected());
    }

    public void onSettings() {
        new SettingsDialog(this, view);
    }

    public void onMap(){
        view.setMap(mapItem.isSelected());
    }

}
