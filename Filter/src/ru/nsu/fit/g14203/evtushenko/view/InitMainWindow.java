package ru.nsu.fit.g14203.evtushenko.view;

import ru.nsu.fit.g14203.evtushenko.Controller;
import ru.nsu.fit.g14203.evtushenko.model.FilterParameters;
import ru.nsu.fit.g14203.evtushenko.model.Model;
import ru.nsu.fit.g14203.evtushenko.utils.ExtensionFileFilter;
import ru.nsu.fit.g14203.evtushenko.view.dialogs.SingleFloatParameterDialog;
import ru.nsu.fit.g14203.evtushenko.view.dialogs.SingleIntParameterDialog;
import ru.nsu.fit.g14203.evtushenko.view.dialogs.ThreeIntsDialog;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import static ru.nsu.fit.g14203.evtushenko.model.FilterParameters.FilterType;

public class InitMainWindow extends MainFrame {

    private Set<Component> toolbarButtons = new HashSet<>();
    private Set<Component> menuItems = new HashSet<>();
    private Set<Component> saveButtons = new HashSet<>();
    private Set<Component> saveItems = new HashSet<>();

    private JToggleButton chooseToggle;
    private JRadioButtonMenuItem chooseItem;
    private JMenuItem renderingRunItem;
    private JButton renderingRunButton;

    private Controller controller;
    private boolean saved;
    private View view;
    private String file;
    private JMenuItem emissionItem;
    private JMenuItem absorptionItem;
    private JToggleButton emissionToggle;
    private JToggleButton absorptionToggle;

    public InitMainWindow() {
        super(1120, 620, "Filter");
        try {
            initMenu();
            initToolbar();

            toolbarButtons.forEach(e -> e.setEnabled(false));
            menuItems.forEach(e -> e.setEnabled(false));
            saveItems.forEach(e -> e.setEnabled(false));
            saveButtons.forEach(e -> e.setEnabled(false));

            Model model = new Model();
            Thread modelThread = new Thread(model);
            modelThread.start();
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

    private void initToolbar() throws NoSuchMethodException {
        addToolBarButton("File/Open");
        addToolBarButton("File/Clear");
        saveButtons.add(addToolBarButton("File/Save"));
        saveButtons.add(addToolBarButton("File/Save as"));
        addToolBarSeparator();

        chooseToggle = addToolBarToggleButton("Filters/Choose");
        chooseToggle.setEnabled(false);
        chooseToggle.addChangeListener(e -> chooseItem.setSelected(chooseToggle.isSelected()));
        chooseItem.addChangeListener(e -> chooseToggle.setSelected(chooseItem.isSelected()));
        toolbarButtons.add(addToolBarButton("Filters/BW"));
        toolbarButtons.add(addToolBarButton("Filters/Blur"));
        toolbarButtons.add(addToolBarButton("Filters/Negative"));
        toolbarButtons.add(addToolBarButton("Filters/Sharpness"));
        toolbarButtons.add(addToolBarButton("Filters/Stamp"));
        toolbarButtons.add(addToolBarButton("Filters/Aquarelle"));
        toolbarButtons.add(addToolBarButton("Filters/Gamma"));
        addToolBarSeparator();
        toolbarButtons.add(addToolBarButton("Filters/Floyd-Steinberg"));
        toolbarButtons.add(addToolBarButton("Filters/Ordered dithering"));
        addToolBarSeparator();
        toolbarButtons.add(addToolBarButton("Filters/Roberts"));
        toolbarButtons.add(addToolBarButton("Filters/Sobel"));
        addToolBarSeparator();
        toolbarButtons.add(addToolBarButton("Filters/Rotation"));
        toolbarButtons.add(addToolBarButton("Filters/Zoom"));
        toolbarButtons.add(addToolBarButton("Filters/Copy C to B"));
        addToolBarSeparator();
        toolbarButtons.add(addToolBarButton("Volume rendering/Load"));
        emissionToggle = addToolBarToggleButton("Volume rendering/Emission");
        toolbarButtons.add(emissionToggle);
        absorptionToggle = addToolBarToggleButton("Volume rendering/Absorption");
        toolbarButtons.add(absorptionToggle);
        renderingRunButton = addToolBarButton("Volume rendering/Run");
        renderingRunButton.setEnabled(false);
        addToolBarSeparator();
        toolbarButtons.add(addToolBarButton("Help/About"));
        toolbarButtons.add(addToolBarButton("File/Exit"));

        emissionItem.addChangeListener(e -> emissionToggle.setSelected(emissionItem.isSelected()));
        emissionToggle.addChangeListener(e -> emissionItem.setSelected(emissionToggle.isSelected()));

        absorptionItem.addChangeListener(e -> absorptionToggle.setSelected(absorptionItem.isSelected()));
        absorptionToggle.addChangeListener(e -> absorptionItem.setSelected(absorptionToggle.isSelected()));
    }

    private void initMenu() throws NoSuchMethodException {
        addSubMenu("File", KeyEvent.VK_F);
        addMenuItem("File/Open", "Open", KeyEvent.VK_O, "open.png", "onOpen");
        saveItems.add(addMenuItem("File/Save", "Save", KeyEvent.VK_S, "save.png", "onSave"));
        saveItems.add(addMenuItem("File/Save as", "Save as", KeyEvent.VK_A, "saveas.png", "onSaveAs"));
        menuItems.add(addMenuItem("File/Clear", "Clear", KeyEvent.VK_A, "clear.png", "onClear"));
        addMenuSeparator("File");
        addMenuItem("File/Exit", "Exit", KeyEvent.VK_X, "exit.png", "onExit");

        addSubMenu("Filters", KeyEvent.VK_I);
        chooseItem = (JRadioButtonMenuItem) addRadioMenuItem("Filters/Choose", "Choose", KeyEvent.VK_O, "select.png", "onChose");
        chooseItem.setEnabled(false);
        menuItems.add(addMenuItem("Filters/BW", "Black and white", KeyEvent.VK_A, "button_bw.png", "onBw"));
        menuItems.add(addMenuItem("Filters/Blur", "Blur", KeyEvent.VK_O, "button_bl.png", "onBlur"));
        menuItems.add(addMenuItem("Filters/Negative", "Negative", KeyEvent.VK_S, "button_ne.png", "onNegative"));
        menuItems.add(addMenuItem("Filters/Sharpness", "Sharpness", KeyEvent.VK_A, "button_sh.png", "onSharpness"));
        menuItems.add(addMenuItem("Filters/Stamp", "Stamp", KeyEvent.VK_A, "button_st.png", "onStamp"));
        menuItems.add(addMenuItem("Filters/Floyd-Steinberg", "Floyd-Steinberg dithering", KeyEvent.VK_A, "button_fl.png", "onFloydSteinberg"));
        menuItems.add(addMenuItem("Filters/Ordered dithering", "Ordered dithering", KeyEvent.VK_A, "button_or.png", "onOrderedDithering"));
        menuItems.add(addMenuItem("Filters/Aquarelle", "Aquarelle", KeyEvent.VK_A, "button_aq.png", "onAquarelle"));
        menuItems.add(addMenuItem("Filters/Roberts", "Roberts", KeyEvent.VK_A, "button_ro.png", "onRoberts"));
        menuItems.add(addMenuItem("Filters/Rotation", "Rotation", KeyEvent.VK_A, "rotate.png", "onRotation"));
        menuItems.add(addMenuItem("Filters/Zoom", "Zoom", KeyEvent.VK_A, "zoom.png", "onZoom"));
        menuItems.add(addMenuItem("Filters/Gamma", "Gamma", KeyEvent.VK_A, "button_ga.png", "onGamma"));
        menuItems.add(addMenuItem("Filters/Sobel", "Sobel", KeyEvent.VK_A, "button_so.png", "onSobel"));
        menuItems.add(addMenuItem("Filters/Copy C to B", "Copy image from zone C to zone B", KeyEvent.VK_A, "right.png", "onCopy"));
        addSubMenu("Volume rendering", KeyEvent.VK_I);
        menuItems.add(addMenuItem("Volume rendering/Load", "Load rendering parameters", KeyEvent.VK_A, "open.png", "onLoadParameters"));
        emissionItem = addRadioMenuItem("Volume rendering/Emission", "Enable emission", KeyEvent.VK_A, "xor.png", "onEmission");
        menuItems.add(emissionItem);
        absorptionItem = addRadioMenuItem("Volume rendering/Absorption", "Enable absorption", KeyEvent.VK_A, "replace.png", "onAbsorption");
        menuItems.add(absorptionItem);

        renderingRunItem = addMenuItem("Volume rendering/Run", "Load rendering parameters", KeyEvent.VK_A, "run.png", "onRenderingRun");
        renderingRunItem.setEnabled(false);

        addSubMenu("Help", KeyEvent.VK_H);
        addMenuItem("Help/About", "Show program version and copyright information", KeyEvent.VK_A, "about.png", "onAbout");
    }

    public static void main(String[] args) {
        InitMainWindow mainFrame = new InitMainWindow();
        mainFrame.setVisible(true);
    }


    public void onAbout() {
        JOptionPane.showMessageDialog(this,
                "Version 1.0\nEvtushenko Ilya, 14203 FIT NSU", "About",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void onExit() {
        if (!saved) {
            int res = JOptionPane.showConfirmDialog(this,
                    "Would you like to save?",
                    "Save",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            switch (res) {
                case JOptionPane.YES_OPTION:
                    onSaveAs();
                case JOptionPane.NO_OPTION:
                    System.exit(0);
                    break;
                default:
                    break;
            }
        }
    }

    public void onOpen() {
        JFileChooser fileChooser = new JFileChooser("FIT_14203_Evtushenko_Ilya_Filter_Data");
        fileChooser.addChoosableFileFilter(new ExtensionFileFilter("png", "PNG24"));
        fileChooser.addChoosableFileFilter(new ExtensionFileFilter("bmp", "BMP24"));
        int res = fileChooser.showDialog(this, "Open");
        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                onClear();
                file = fileChooser.getSelectedFile().getPath();
                controller.loadImage(file);
                chooseItem.setEnabled(true);
                chooseToggle.setEnabled(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Can not open file");
            }
        }
    }

    public void onSave() {
        if (file != null) {
            try (OutputStream out = new FileOutputStream(file)) {
                ImageIO.write(controller.getImageC(), "png", out);
                saved = true;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Can not save file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            onSaveAs();
        }
    }

    public void onSaveAs() {
        JFileChooser fileChooser = new JFileChooser("FIT_14203_Evtushenko_Ilya_Filter_Data");
        ExtensionFileFilter pngFilter = new ExtensionFileFilter("png", "PNG24");
        fileChooser.setFileFilter(pngFilter);
        int res = fileChooser.showDialog(this, "Save");
        if (res == JFileChooser.APPROVE_OPTION) {
            String selectedFile = fileChooser.getSelectedFile().getPath()+".png";
            try (OutputStream out = new FileOutputStream(selectedFile)) {
                ImageIO.write(controller.getImageC(), "png", out);
                saved = true;
                file = selectedFile;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Can not save file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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
        saved = false;
        new ThreeIntsDialog(this,
                "Floyd-Steinberg dithering",
                controller,
                new FilterParameters(FilterType.FLOYD_STEINBERG, new double[0]),
                new String[]{"Red", "Green", "Blue"},
                new int[]{2, 2, 2},
                new int[]{256, 256, 256},
                true);
    }

    public void onOrderedDithering() {
        applyFilter(new FilterParameters(FilterType.ORDERED_DITHERING));
    }

    public void onAquarelle() {
        applyFilter(new FilterParameters(FilterType.AQUARELLE));
    }

    public void onRoberts() {
        saved = false;
        new SingleIntParameterDialog(this,
                "Roberts",
                controller,
                FilterType.ROBERTS,
                0,
                255,
                60);
    }

    public void onRotation() {
        saved = false;
        new SingleIntParameterDialog(this,
                "Rotation",
                controller,
                FilterType.ROTATION,
                -180,
                180,
                0);
    }

    public void onZoom() {
        applyFilter(new FilterParameters(FilterType.ZOOM));
    }

    public void onGamma() {
        saved = false;
        new SingleFloatParameterDialog(this,
                "Gamma",
                controller,
                FilterType.GAMMA,
                0,
                2,
                1);
    }

    public void onSobel() {
        saved = false;
        new SingleIntParameterDialog(this,
                "Sobel",
                controller,
                FilterType.SOBEL,
                0,
                255,
                110);
    }

    public void onRenderingRun() {
        saved = false;
        new ThreeIntsDialog(this,
                "Volume rendering parameters",
                controller,
                new FilterParameters(
                        FilterType.VOLUME_RENDERING,
                        new double[]{
                                emissionItem.isSelected() ? 1. : 0.,
                                absorptionItem.isSelected() ? 1. : 0.,
                        }),
                new String[]{"Nx", "Ny", "Nz"},
                new int[]{4, 4, 4},
                new int[]{350, 350, 350},
                false);
    }

    public void onNegative() {
        applyFilter(new FilterParameters(FilterType.NEGATIVE));
    }

    public void onCopy() {
        controller.moveLeft();
    }

    public void onEmission() {
    }

    public void onAbsorption() {
    }

    public void onClear() {
        view.setChoosePart(false);
        controller.clear();
        renderingRunItem.setEnabled(false);
        renderingRunButton.setEnabled(false);
    }

    public void onLoadParameters() {
        JFileChooser fileChooser = new JFileChooser("FIT_14203_Evtushenko_Ilya_Filter_Data");
        fileChooser.addChoosableFileFilter(new ExtensionFileFilter("txt", ""));
        int res = fileChooser.showDialog(this, "Open");
        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                file = fileChooser.getSelectedFile().getPath();
                controller.loadRenderingParameters(file);
                renderingRunButton.setEnabled(true);
                renderingRunItem.setEnabled(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Can not open file");
            }
        }
    }

    private void applyFilter(FilterParameters parameters) {
        saved = false;
        controller.applyFilter(parameters);
    }

    public void setPartChosen(boolean isChosen) {
        toolbarButtons.forEach(e -> e.setEnabled(isChosen));
        menuItems.forEach(e -> e.setEnabled(isChosen));
    }

    public void setChoseEnabled(boolean enabled) {
        view.setChoosePart(enabled);
        chooseItem.setSelected(false);
        chooseItem.setEnabled(enabled);
        chooseToggle.setSelected(false);
        chooseToggle.setEnabled(enabled);
    }

    public void setSaveEnabled(boolean enabled) {
        saveButtons.forEach(e -> e.setEnabled(enabled));
        saveItems.forEach(e -> e.setEnabled(enabled));
    }
}
