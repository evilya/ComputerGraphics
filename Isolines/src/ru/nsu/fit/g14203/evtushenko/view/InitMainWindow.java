package ru.nsu.fit.g14203.evtushenko.view;

import ru.nsu.fit.g14203.evtushenko.utils.ExtensionFileFilter;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class InitMainWindow extends MainFrame {
    private final View view;

    private JToggleButton interpolationToggle;
    private JRadioButtonMenuItem interpolationItem;

    public InitMainWindow() {
        super(1120, 620, "Isolines");
        try {
            initMenu();
            initToolbar();
            connectToggles();
            view = new View();

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
        addToolBarButton("File/Open");
        addToolBarButton("File/Save");
        addToolBarButton("File/Save as");
        addToolBarSeparator();
        interpolationToggle = addToolBarToggleButton("View/Interpolation");
        addToolBarSeparator();
        addToolBarButton("Help/About");
        addToolBarButton("File/Exit");
    }

    private void initMenu() throws NoSuchMethodException {
        addSubMenu("File", KeyEvent.VK_F);
        addMenuItem("File/Open", "Open", KeyEvent.VK_O, "open.png", "onOpen");
        addMenuItem("File/Save", "Save", KeyEvent.VK_S, "save.png", "onSave");
        addMenuItem("File/Save as", "Save as", 0, "saveas.png", "onSaveAs");
        addMenuSeparator("File");
        addMenuItem("File/Exit", "Exit", KeyEvent.VK_X, "exit.png", "onExit");

        addSubMenu("View", KeyEvent.VK_V);
        interpolationItem = (JRadioButtonMenuItem) addRadioMenuItem("View/Interpolation", "Enable interpolation", 0, "xor.png", "onInterpolation");

        addSubMenu("Help", KeyEvent.VK_H);
        addMenuItem("Help/About", "Show program version and copyright information", KeyEvent.VK_A, "about.png", "onAbout");
    }

    private void connectToggles() {
        interpolationItem.addActionListener(e -> interpolationToggle.setSelected(interpolationItem.isSelected()));
        interpolationToggle.addActionListener(e -> interpolationItem.setSelected(interpolationToggle.isSelected()));
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
        JFileChooser fileChooser = new JFileChooser("FIT_14203_Evtushenko_Ilya_Filter_Data");
        fileChooser.addChoosableFileFilter(new ExtensionFileFilter("png", "PNG24"));
        fileChooser.addChoosableFileFilter(new ExtensionFileFilter("bmp", "BMP24"));
        int res = fileChooser.showDialog(this, "Open");
        if (res == JFileChooser.APPROVE_OPTION) {

        }
    }

    public void onSave() {

    }

    public void onSaveAs() {

    }

    public void onInterpolation() {
        view.setInterpolation(interpolationItem.isSelected());
    }

}
