package ru.nsu.fit.g14203.evtushenko.views;

import ru.nsu.fit.g14203.evtushenko.controller.RaytracingController;

import javax.swing.*;
import java.awt.*;


public class MainFrame extends JFrame {
    private final RaytracingController raytracingController;

    private View view;
    private StatusBar statusBar;

    public MainFrame(RaytracingController controller) {
        this.raytracingController = controller;

        setMinimumSize(new Dimension(400, 400));
        setSize(800, 600);
        setTitle("Raytracing");
        setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        initWorkSpace();
        initStatusBar();
        initButtons();
    }

    private void initButtons() {
        ToolbarButtons toolbarButtons = new ToolbarButtons();

        toolbarButtons.setMouseEnteredHandler(raytracingController::onEnterToolbarButton);
        toolbarButtons.setMouseExitedHandler((event) -> raytracingController.onExitToolbarButton());

        JMenu fileMenu = toolbarButtons.addMenu(null, "File");
        toolbarButtons.addItem(fileMenu, "Open", "Open", "open.png", (actionEvent1) -> raytracingController.onOpenButtonClicked());
        toolbarButtons.addMenuSeparator(fileMenu);
        toolbarButtons.addItem(fileMenu, "Exit", null, null, null);
        toolbarButtons.addToolBarSeparator();

        JMenu viewMenu = toolbarButtons.addMenu(null, "View");
        toolbarButtons.addItem(viewMenu, "Render", "Render", "run.png", (actionEvent) -> raytracingController.onRenderButtonClicked());
        toolbarButtons.addToolBarSeparator();

        setJMenuBar(toolbarButtons.getMenuBar());
        add(toolbarButtons.getToolBar(), BorderLayout.PAGE_START);
    }

    private void initWorkSpace() {
        view = new View(raytracingController);

        JScrollPane workspaceScrollPane = new JScrollPane(view);
        add(workspaceScrollPane, BorderLayout.CENTER);
    }

    private void initStatusBar() {
        statusBar = new StatusBar();
        add(statusBar, BorderLayout.PAGE_END);
    }

    public View getView() {
        return view;
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }
}
