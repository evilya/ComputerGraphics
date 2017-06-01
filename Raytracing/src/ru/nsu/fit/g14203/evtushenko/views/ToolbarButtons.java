package ru.nsu.fit.g14203.evtushenko.views;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.util.function.Consumer;


class ToolbarButtons {
    private final JToolBar toolBar;
    private final JMenuBar menuBar;

    private Consumer<MouseEvent> mouseEnteredHandler;
    private Consumer<MouseEvent> mouseExitedHandler;

    public ToolbarButtons() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        menuBar = new JMenuBar();
    }

    public JToolBar getToolBar() {
        return toolBar;
    }

    public JMenuBar getMenuBar() {
        return menuBar;
    }

    public void setMouseEnteredHandler(Consumer<MouseEvent> mouseEnteredHandler) {
        this.mouseEnteredHandler = mouseEnteredHandler;
    }

    public void setMouseExitedHandler(Consumer<MouseEvent> mouseExitedHandler) {
        this.mouseExitedHandler = mouseExitedHandler;
    }

    public JMenu addMenu(JMenuItem parent, String name) {
        JMenu menu = new JMenu(name);
        addMenuItem(parent, menu);
        return menu;
    }

    public void addMenuSeparator(JMenu menu) {
        menu.addSeparator();
    }

    public void addToolBarSeparator() {
        toolBar.addSeparator();
    }

    public void addItem(JMenuItem menuParent, String name, String toolTip, String icoName, ActionListener actionListener) {
        JMenuItem menuItem = createMenuItem(name, actionListener);
        addMenuItem(menuParent, menuItem);

        if (icoName == null) {
            return;
        }

        JButton button = createToolbarButton(toolTip, icoName, actionListener);
        toolBar.add(button);
    }

    private void addMenuItem(JMenuItem parent, JMenuItem item) {
        if (parent != null) {
            parent.add(item);
        } else {
            menuBar.add(item);
        }
    }

    private JMenuItem createMenuItem(String name, ActionListener actionListener) {
        JMenuItem menuItem = new JMenuItem(name);
        initMenuItem(menuItem, actionListener);

        return menuItem;
    }

    private void initMenuItem(JMenuItem item, ActionListener actionListener) {
        if (actionListener != null) {
            item.addActionListener(actionListener);
        }
    }

    private JButton createToolbarButton(String toolTip, String icoName, ActionListener actionListener) {
        JButton button = new JButton();
        initToolbarButton(button, toolTip, icoName, actionListener);

        return button;
    }

    private void initToolbarButton(AbstractButton button, String toolTip, String icoName, ActionListener actionListener) {
        button.setToolTipText(toolTip);
        Icon aboutButtonIcon = getButtonIcon("icons/" + icoName);
        if (aboutButtonIcon != null) {
            button.setIcon(aboutButtonIcon);
        }
        button.addActionListener(actionListener);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                mouseEnteredHandler.accept(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                mouseExitedHandler.accept(mouseEvent);
            }
        });
    }

    private Icon getButtonIcon(String imgPath) {
        URL imgUrl = this.getClass().getClassLoader().getResource("resources" + File.separator + imgPath);
        if (imgUrl == null) {
            return null;
        }

        return new ImageIcon(imgUrl);
    }
}
