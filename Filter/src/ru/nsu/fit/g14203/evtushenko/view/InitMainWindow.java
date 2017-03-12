package ru.nsu.fit.g14203.evtushenko.view;

import ru.nsu.fit.g14203.evtushenko.Controller;
import ru.nsu.fit.g14203.evtushenko.model.FilterParameters;
import ru.nsu.fit.g14203.evtushenko.model.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

import static ru.nsu.fit.g14203.evtushenko.model.FilterParameters.FilterType;

public class InitMainWindow extends MainFrame {

    private Controller controller;
    private View view;
    private JToggleButton chooseToggle;
    private JRadioButtonMenuItem chooseItem;

    public InitMainWindow() {
        super(1120, 460, "Filter");
        try {
            initMenu();
            initToolbar();

            Model model = new Model();
            Thread modelThread = new Thread(model);
            modelThread.start();
            controller = new Controller(model);
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

    private void initToolbar() throws NoSuchMethodException {
        addToolBarButton("File/Open");
        addToolBarButton("File/Save");
        addToolBarButton("File/Save as");
        addToolBarSeparator();

        chooseToggle = addToolBarToggleButton("Filters/Chose");
        chooseToggle.setEnabled(false);

        addToolBarButton("Filters/Blur");
        addToolBarButton("Filters/Negative");
        addToolBarButton("Filters/BW");
        addToolBarButton("Filters/Sharpness");
        addToolBarButton("Filters/Stamp");
        addToolBarButton("Filters/Floyd-Steinberg");
        addToolBarButton("Filters/Ordered dithering");
        addToolBarButton("Filters/Aquarelle");
        addToolBarButton("Filters/Roberts");
        addToolBarButton("Filters/Rotation");
        addToolBarButton("Filters/Zoom");
        addToolBarButton("Filters/Gamma");


        addToolBarSeparator();
        addToolBarButton("Help/About");
        addToolBarButton("File/Exit");
    }

    private void initMenu() throws NoSuchMethodException {
        addSubMenu("File", KeyEvent.VK_F);
        addMenuItem("File/Open", "Open", KeyEvent.VK_O, "open.png", "onOpen");
        addMenuItem("File/Save", "Save", KeyEvent.VK_S, "save.png", "onSave");
        addMenuItem("File/Save as", "Save as", KeyEvent.VK_A, "saveas.png", "onSaveAs");
        addMenuSeparator("File");
        addMenuItem("File/Exit", "Exit", KeyEvent.VK_X, "exit.png", "onExit");

        addSubMenu("Filters", KeyEvent.VK_I);
        chooseItem = (JRadioButtonMenuItem) addRadioMenuItem("Filters/Chose", "Chose", KeyEvent.VK_O, "run.png", "onChose");
        chooseItem.setEnabled(false);
        addMenuItem("Filters/Blur", "Blur", KeyEvent.VK_O, "run.png", "onBlur");
        addMenuItem("Filters/Negative", "Negative", KeyEvent.VK_S, "run.png", "onNegative");
        addMenuItem("Filters/BW", "Black and white", KeyEvent.VK_A, "run.png", "onBw");
        addMenuItem("Filters/Sharpness", "Sharpness", KeyEvent.VK_A, "run.png", "onSharpness");
        addMenuItem("Filters/Stamp", "Stamp", KeyEvent.VK_A, "run.png", "onStamp");
        addMenuItem("Filters/Floyd-Steinberg", "Floyd-Steinberg dithering", KeyEvent.VK_A, "run.png", "onFloydSteinberg");
        addMenuItem("Filters/Ordered dithering", "Ordered dithering", KeyEvent.VK_A, "run.png", "onOrderedDithering");
        addMenuItem("Filters/Aquarelle", "Aquarelle", KeyEvent.VK_A, "run.png", "onAquarelle");
        addMenuItem("Filters/Roberts", "Roberts", KeyEvent.VK_A, "run.png", "onRoberts");
        addMenuItem("Filters/Rotation", "Rotation", KeyEvent.VK_A, "run.png", "onRotation");
        addMenuItem("Filters/Zoom", "Zoom", KeyEvent.VK_A, "run.png", "onZoom");
        addMenuItem("Filters/Gamma", "Gamma", KeyEvent.VK_A, "run.png", "onGamma");

        addSubMenu("Help", KeyEvent.VK_H);
        addMenuItem("Help/About", "Show program version and copyright information", KeyEvent.VK_A, "about.png", "onAbout");
    }

    public static void main(String[] args) {
        InitMainWindow mainFrame = new InitMainWindow();
        mainFrame.setVisible(true);
    }


    public void onAbout() {
        JOptionPane.showMessageDialog(this,
                "Sample, version 0.9\nEvtushenko Ilya, 14203 FIT NSU", "About Init",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void onExit() {
        System.exit(0);
    }

    public void onOpen() {
        JFileChooser fileChooser = new JFileChooser("FIT_14203_Evtushenko_Ilya_Filter_Data");
        int res = fileChooser.showDialog(this, "Open");
        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                String path = fileChooser.getSelectedFile().getPath();
                controller.loadImage(path);
                chooseItem.setEnabled(true);
                chooseToggle.setEnabled(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Can not open file");
            }
        }
    }

    public void onSave() {

    }

    public void onSaveAs() {

    }

    public void onChose() {
        view.setChoosePart(chooseToggle.isSelected());
    }

    public void onBlur() {
        applyFilter(new FilterParameters(FilterParameters.FilterType.BLUR));
    }

    public void onBw() {
        applyFilter(new FilterParameters(FilterType.BLACK_AND_WHITE));
    }

    public void onSharpness() {
        applyFilter(new FilterParameters(FilterType.SHARPNESS));
    }

    public void onStamp() {
        applyFilter(new FilterParameters(FilterType.STAMPING));
    }

    public void onFloydSteinberg() {
        applyFilter(new FilterParameters(FilterType.FLOYD_STEINBERG, new double[]{2., 2., 2.}));
    }

    public void onOrderedDithering() {
        applyFilter(new FilterParameters(FilterType.ORDERED_DITHERING, new double[]{2, 2, 2}));
    }

    public void onAquarelle() {

        applyFilter(new FilterParameters(FilterType.AQUARELLE));
    }

    public void onRoberts() {

        applyFilter(new FilterParameters(FilterType.ROBERTS, new double[]{50, 50, 50}));
    }

    public void onRotation(){
        applyFilter(new FilterParameters(FilterType.ROTATION, new double[]{180}));
    }

    public void onZoom(){
        applyFilter(new FilterParameters(FilterType.ZOOM));
    }

    public void onGamma(){
        applyFilter(new FilterParameters(FilterType.GAMMA, new double[]{100.0}));
    }

    public void onNegative() {
        applyFilter(new FilterParameters(FilterType.NEGATIVE));
    }

    private void applyFilter(FilterParameters parameters) {
        if (controller.isPartChosen()) {
            controller.applyFilter(parameters);
        }
    }
}
