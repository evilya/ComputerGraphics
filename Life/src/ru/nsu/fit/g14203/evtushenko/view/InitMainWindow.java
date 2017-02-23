package ru.nsu.fit.g14203.evtushenko.view;

import ru.nsu.fit.g14203.evtushenko.dialogs.CreationDialog;
import ru.nsu.fit.g14203.evtushenko.dialogs.SettingsDialog;
import ru.nsu.fit.g14203.evtushenko.model.Cell;
import ru.nsu.fit.g14203.evtushenko.model.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

public class InitMainWindow extends MainFrame {

	private JScrollPane scrollPane;
	private Model model;
	private HexView view;
	private Timer timer;

	public InitMainWindow() {
		super(600, 400, "LIFE");
		setMinimumSize(getSize());
		try {
			addSubMenu("File", KeyEvent.VK_F);
			addMenuItem("File/New", "Create new game", KeyEvent.VK_N, "new.png", "onNew");
			addMenuItem("File/Open", "Open game", KeyEvent.VK_O, "open.png", "onOpen");
			addMenuItem("File/Save", "Save game", KeyEvent.VK_S, "save.png", "onSave");
			addMenuSeparator("File");
			addMenuItem("File/Exit", "Exit application", KeyEvent.VK_X, "exit.png", "onExit");

			addSubMenu("Game", KeyEvent.VK_G);
			addMenuItem("Game/Next", "Next state", KeyEvent.VK_E, "next.png", "onNext");
			addMenuItem("Game/Run", "Run game", KeyEvent.VK_R, "run.png", "onRun");
			addMenuItem("Game/Clear", "Clear field", KeyEvent.VK_C, "clear.png", "onClear");
			addMenuSeparator("Game");
			addMenuItem("Game/Show impacts", "Shows each cell's impact", KeyEvent.VK_I, "impacts.png", "onImpactShow");
			addMenuItem("Game/Settings", "Open game settings", KeyEvent.VK_S, "options.png", "onSettings");
			addSubMenu("Help", KeyEvent.VK_H);
			addMenuItem("Help/About", "Show program version and copyright information", KeyEvent.VK_A, "about.png", "onAbout");

			JRadioButtonMenuItem lal = new JRadioButtonMenuItem("lal");
			JRadioButtonMenuItem lul = new JRadioButtonMenuItem("lul");

			ButtonGroup group = new ButtonGroup();
			group.add(lal);
			group.add(lul);

			((JPopupMenu) getMenuElement("File").getComponent()).add(lal);
			((JPopupMenu) getMenuElement("File").getComponent()).add(lul);

			addToolBarButton("File/New");
			addToolBarButton("File/Open");
			addToolBarButton("File/Save");
			addToolBarSeparator();
			addToolBarButton("Game/Next");
			addToolBarButton("Game/Run");
			addToolBarButton("Game/Show impacts");
			addToolBarButton("Game/Clear");
			addToolBarSeparator();
			addToolBarButton("Game/Settings");
			addToolBarSeparator();
			addToolBarButton("File/Exit");
			addToolBarButton("Help/About");

//			getMenuElement("Game/Next").getComponent().setEnabled(false);
//			Component[] components = toolBar.getComponents();
//			components[4].setEnabled(false);

			timer = new Timer(1000, e -> onNext());

			model = new Model();
			view = new HexView(model);
			scrollPane = new JScrollPane(view);
			model.addObserver(view);
			scrollPane.getVerticalScrollBar().setUnitIncrement(50);

			add(scrollPane);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public void onImpactShow() {

	}

	public void onSettings() {
		new SettingsDialog(this, model);
		scrollPane.revalidate();
	}

	public void onClear() {
		model.clear();
	}

	public void onAbout() {
		JOptionPane.showMessageDialog(this,
				"Sample, version 0.9\nEvtushenko Ilya, 14203 FIT NSU", "About Init",
				JOptionPane.INFORMATION_MESSAGE);
	}

	public void onNext() {
		model.next();
	}

	public void onRun() {
		if (timer.isRunning()) {
			timer.stop();
		} else {
			timer.start();
		}
	}

	public void onExit() {
		System.exit(0);
	}

	public void onNew() {
		new CreationDialog(this);
		scrollPane.revalidate();
	}

	public void onOpen() {
		JFileChooser fileChooser = new JFileChooser("data");
		int res = fileChooser.showDialog(this, "Open");
		if (res == JFileChooser.APPROVE_OPTION) {
			try {
				model.loadFromFile(fileChooser.getSelectedFile().getPath());
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Can not open file");
			}
		}
		scrollPane.revalidate();
	}

	public void onSave() {
		JFileChooser fileChooser = new JFileChooser("data");
		int res = fileChooser.showDialog(this, "Save");
		if (res == JFileChooser.APPROVE_OPTION) {
			try {
				File file = fileChooser.getSelectedFile();
				Writer writer = new FileWriter(file);
				int width = model.getWidth();
				int height = model.getHeight();
				writer.write(width + " " + height + "\n");
				writer.write(model.getLineThickness() + "\n");
				writer.write(model.getCellSize() + "\n");
				Cell[][] cells = model.getCells();
				int aliveCount = 0;
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width - y % 2; x++) {
						if (cells[y][x].isAlive()) {
							aliveCount++;
						}
					}
				}
				writer.write(aliveCount + "\n");
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width - y % 2; x++) {
						if (model.getCellState(x,y)) {
							writer.write(x + " " + y + "\n");
						}
					}
				}
				writer.close();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Can not save to file");
			}
		}
	}

	public Model getModel() {
		return model;
	}

	public static void main(String[] args) {
		InitMainWindow mainFrame = new InitMainWindow();
		mainFrame.setVisible(true);
	}
}
