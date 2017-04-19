package ru.nsu.fit.g14203.evtushenko.view;

import ru.nsu.fit.g14203.evtushenko.model.Model;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.KeyEvent;

public class InitMainWindow extends MainFrame {
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
            Model model = new Model();
            Thread modelThread = new Thread(model);
            View view = new View(model);
            model.addObserver(view);

            add(view);
            modelThread.start();

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
        addToolBarButton("Help/About");
        addToolBarButton("File/Exit");
    }

    private void initMenu() throws NoSuchMethodException {
        addSubMenu("File", KeyEvent.VK_F);
        addMenuItem("File/Open", "Open", KeyEvent.VK_O, "open.png", "onOpen");
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

}
