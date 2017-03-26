package ru.nsu.fit.g14203.evtushenko.view;

import ru.nsu.fit.g14203.evtushenko.Controller;
import ru.nsu.fit.g14203.evtushenko.model.Model;
import ru.nsu.fit.g14203.evtushenko.utils.ExtensionFileFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class InitMainWindow extends MainFrame {
    private Controller controller;
    private View view;

    public InitMainWindow() {
        super(1120, 620, "Filter");
        try {
            initMenu();
            initToolbar();

            Model model = new Model();
            controller = new Controller(model, this);
            view = new View(controller);
            model.addObserver(view);

            JPanel flowPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JScrollPane scrollPane = new JScrollPane(flowPane);

            flowPane.setPreferredSize(new Dimension(1100, 370));
            flowPane.add(view);
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
        addToolBarButton("File/Open");
        addToolBarButton("File/Save");
        addToolBarButton("File/Save as");
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

        addSubMenu("Help", KeyEvent.VK_H);
        addMenuItem("Help/About", "Show program version and copyright information", KeyEvent.VK_A, "about.png", "onAbout");
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


}
