package ru.nsu.fit.g14203.evtushenko.view;

import ru.nsu.fit.g14203.evtushenko.utils.FileUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;

public class MainFrame extends JFrame {
    private JMenuBar menuBar;
    protected JToolBar toolBar;

    public MainFrame() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ignored) {
        }
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        toolBar = new JToolBar("Main toolbar");
        toolBar.setRollover(true);
        toolBar.setFloatable(false);
        add(toolBar, BorderLayout.PAGE_START);
    }

    public MainFrame(int x, int y, String title) {
        this();
        setSize(x, y);
        setLocationByPlatform(true);
        setTitle(title);
    }

    public JMenuItem createMenuItem(String title,
                                    String tooltip,
                                    int mnemonic,
                                    String icon,
                                    String actionMethod) throws SecurityException, NoSuchMethodException {
        JMenuItem item = new JMenuItem(title);
        item.setMnemonic(mnemonic);
        item.setToolTipText(tooltip);
        if (icon != null) {
            try {
                loadImage(icon, title, item);
            } catch (IOException ignore) {
            }
        }
        final Method method = getClass().getMethod(actionMethod);
        item.addActionListener(evt -> {
            try {
                method.invoke(MainFrame.this);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return item;
    }

    private void loadImage(String icon, String title, JMenuItem item) throws IOException {
        Image image = ImageIO.read(getClass().getResource("/resources/" + icon));
        image = image.getScaledInstance(16, 16, 0);
        ImageIcon imageIcon = new ImageIcon(image, title);
        item.setIcon(imageIcon);
    }

    public JRadioButtonMenuItem createRadioMenuItem(String title,
                                                    String tooltip,
                                                    int mnemonic,
                                                    String icon,
                                                    String actionMethod) throws SecurityException, NoSuchMethodException {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(title);
        item.setMnemonic(mnemonic);
        item.setToolTipText(tooltip);
        if (icon != null) {
            try {
                loadImage(icon, title, item);
            } catch (IOException ignore) {
            }
        }
        final Method method = getClass().getMethod(actionMethod);
        item.addActionListener(evt -> {
            try {
                method.invoke(MainFrame.this);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return item;
    }

    public JMenuItem createMenuItem(String title,
                                    String tooltip,
                                    int mnemonic,
                                    String actionMethod) throws SecurityException, NoSuchMethodException {
        return createMenuItem(title, tooltip, mnemonic, null, actionMethod);
    }

    public JMenu createSubMenu(String title, int mnemonic) {
        JMenu menu = new JMenu(title);
        menu.setMnemonic(mnemonic);
        return menu;
    }

    public void addSubMenu(String title, int mnemonic) {
        MenuElement element = getParentMenuElement(title);
        if (element == null) {
            throw new InvalidParameterException("Menu path not found: " + title);
        }
        JMenu subMenu = createSubMenu(getMenuPathName(title), mnemonic);
        if (element instanceof JMenuBar) {
            ((JMenuBar) element).add(subMenu);
        } else {
            addItem(title, element, subMenu);
        }
    }


    public JMenuItem addMenuItem(String title,
                                 String tooltip,
                                 int mnemonic,
                                 String icon,
                                 String actionMethod) throws SecurityException, NoSuchMethodException {
        MenuElement element = getParentMenuElement(title);
        if (element == null) {
            throw new InvalidParameterException("Menu path not found: " + title);
        }
        JMenuItem item = createMenuItem(getMenuPathName(title), tooltip, mnemonic, icon, actionMethod);
        return addItem(title, element, item);
    }

    public JMenuItem addRadioMenuItem(String title,
                                      String tooltip,
                                      int mnemonic,
                                      String icon,
                                      String actionMethod) throws SecurityException, NoSuchMethodException {
        MenuElement element = getParentMenuElement(title);
        if (element == null) {
            throw new InvalidParameterException("Menu path not found: " + title);
        }
        JRadioButtonMenuItem item = createRadioMenuItem(getMenuPathName(title), tooltip, mnemonic, icon, actionMethod);
        return addItem(title, element, item);
    }

    private JMenuItem addItem(String title, MenuElement element, JMenuItem item) {
        if (element instanceof JMenu) {
            return ((JMenu) element).add(item);
        } else if (element instanceof JPopupMenu) {
            return ((JPopupMenu) element).add(item);
        } else {
            throw new InvalidParameterException("Invalid menu path: " + title);
        }
    }

    public void addMenuItem(String title,
                            String tooltip,
                            int mnemonic,
                            String actionMethod) throws SecurityException, NoSuchMethodException {
        addMenuItem(title, tooltip, mnemonic, null, actionMethod);
    }


    public void addMenuSeparator(String title) {
        MenuElement element = getMenuElement(title);
        if (element == null) {
            throw new InvalidParameterException("Menu path not found: " + title);
        }
        if (element instanceof JMenu) {
            ((JMenu) element).addSeparator();
        } else if (element instanceof JPopupMenu) {
            ((JPopupMenu) element).addSeparator();
        } else {
            throw new InvalidParameterException("Invalid menu path: " + title);
        }
    }

    private String getMenuPathName(String menuPath) {
        int pos = menuPath.lastIndexOf('/');
        if (pos > 0) {
            return menuPath.substring(pos + 1);
        } else {
            return menuPath;
        }
    }

    private MenuElement getParentMenuElement(String menuPath) {
        int pos = menuPath.lastIndexOf('/');
        if (pos > 0) {
            return getMenuElement(menuPath.substring(0, pos));
        } else {
            return menuBar;
        }
    }

    public MenuElement getMenuElement(String menuPath) {
        MenuElement element = menuBar;
        for (String pathElement : menuPath.split("/")) {
            MenuElement newElement = null;
            for (MenuElement subElement : element.getSubElements()) {
                if ((subElement instanceof JMenu
                        && ((JMenu) subElement).getText().equals(pathElement))
                        || (subElement instanceof JMenuItem
                        && ((JMenuItem) subElement).getText().equals(pathElement))) {
                    if (subElement.getSubElements().length == 1
                            && subElement.getSubElements()[0] instanceof JPopupMenu) {
                        newElement = subElement.getSubElements()[0];
                    } else {
                        newElement = subElement;
                    }
                    break;
                }
            }
            if (newElement == null) {
                return null;
            }
            element = newElement;
        }
        return element;
    }

    public JButton createToolBarButton(JMenuItem item) {
        JButton button = new JButton(item.getIcon());
        for (ActionListener listener : item.getActionListeners()) {
            button.addActionListener(listener);
        }
        button.setToolTipText(item.getToolTipText());
        return button;
    }

    public JToggleButton createToolBarToggleButton(JMenuItem item) {
        JToggleButton button = new JToggleButton(item.getIcon());
        for (ActionListener listener : item.getActionListeners()) {
            button.addActionListener(listener);
        }
        button.setToolTipText(item.getToolTipText());
        return button;
    }

    public JButton createToolBarButton(String menuPath) {
        JMenuItem item = (JMenuItem) getMenuElement(menuPath);
        if (item == null) {
            throw new InvalidParameterException("Menu path not found: " + menuPath);
        }
        return createToolBarButton(item);
    }

    public JToggleButton createToolBarToggleButton(String menuPath) {
        JMenuItem item = (JMenuItem) getMenuElement(menuPath);
        if (item == null) {
            throw new InvalidParameterException("Menu path not found: " + menuPath);
        }
        return createToolBarToggleButton(item);
    }

    public JButton addToolBarButton(String menuPath) {
        JButton button = createToolBarButton(menuPath);
        toolBar.add(button);
        return button;
    }

    public JToggleButton addToolBarToggleButton(String menuPath) {
        JToggleButton button = createToolBarToggleButton(menuPath);
        toolBar.add(button);
        return button;
    }

    public void addToolBarSeparator() {
        toolBar.addSeparator();
    }

    public File getSaveFileName(String extension, String description) {
        return FileUtils.getSaveFileName(this, extension, description);
    }

    public File getOpenFileName(String extension, String description) {
        return FileUtils.getOpenFileName(this, extension, description);
    }
}
