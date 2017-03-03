package ru.nsu.fit.g14203.evtushenko.view;

import ru.nsu.fit.g14203.evtushenko.dialogs.CreationDialog;
import ru.nsu.fit.g14203.evtushenko.dialogs.SaveOfferDialog;
import ru.nsu.fit.g14203.evtushenko.dialogs.SettingsDialog;
import ru.nsu.fit.g14203.evtushenko.model.Cell;
import ru.nsu.fit.g14203.evtushenko.model.Model;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class InitMainWindow extends MainFrame {

	private JScrollPane scrollPane;
	private Model model;
	private HexView view;
	private Timer timer;
	private String curFile;

	public InitMainWindow() {
		super(600, 400, "LIFE");
		try {
			addSubMenu("File", KeyEvent.VK_F);
			addMenuItem("File/New", "Create new", KeyEvent.VK_N, "new.png", "onNew");
			addMenuItem("File/Open", "Open", KeyEvent.VK_O, "open.png", "onOpen");
			addMenuItem("File/Save", "Save", KeyEvent.VK_S, "save.png", "onSave");
			addMenuItem("File/Save as", "Save as", KeyEvent.VK_A, "saveas.png", "onSaveAs");
			addMenuSeparator("File");
			addMenuItem("File/Exit", "Exit", KeyEvent.VK_X, "exit.png", "onExit");

			addSubMenu("Game", KeyEvent.VK_G);
			addMenuItem("Game/Next", "Next state", KeyEvent.VK_E, "next.png", "onNext");
			JRadioButtonMenuItem menuRun =
					(JRadioButtonMenuItem) addRadioMenuItem("Game/Run",
							"Run game",
							KeyEvent.VK_R,
							"run.png",
							"onRun");
			addMenuItem("Game/Clear", "Clear field", KeyEvent.VK_C, "clear.png", "onClear");
			addMenuSeparator("Game");
			JRadioButtonMenuItem menuImpacts =
					(JRadioButtonMenuItem) addRadioMenuItem("Game/Show impacts",
							"Shows each cell's impact",
							KeyEvent.VK_I,
							"impacts.png",
							"onImpactShow");
			JRadioButtonMenuItem menuReplace =
					(JRadioButtonMenuItem) addRadioMenuItem("Game/Replace",
							"Replace fill",
							KeyEvent.VK_R,
							"replace.png",
							"onReplace");
			JRadioButtonMenuItem menuXor =
					(JRadioButtonMenuItem) addRadioMenuItem("Game/XOR",
							"Xor fill",
							KeyEvent.VK_X,
							"xor.png",
							"onXor");
			addMenuItem("Game/Settings", "Open game settings", KeyEvent.VK_S, "settings.png", "onSettings");
			addSubMenu("Help", KeyEvent.VK_H);
			addMenuItem("Help/About", "Show program version and copyright information", KeyEvent.VK_A, "about.png", "onAbout");

			addToolBarButton("File/New");
			addToolBarButton("File/Open");
			addToolBarButton("File/Save");
			addToolBarButton("File/Save as");
			addToolBarSeparator();
			addToolBarButton("Game/Next");
			JToggleButton toolbarRun = addToolBarToggleButton("Game/Run");
			JToggleButton toolbarImpacts = addToolBarToggleButton("Game/Show impacts");
			addToolBarButton("Game/Clear");
			JToggleButton toolbarReplace = addToolBarToggleButton("Game/Replace");
			JToggleButton toolbarXor = addToolBarToggleButton("Game/XOR");
			addToolBarSeparator();
			addToolBarButton("Game/Settings");
			addToolBarSeparator();
			addToolBarButton("File/Exit");
			addToolBarButton("Help/About");

			toolbarRun.addActionListener(e -> menuRun.setSelected(toolbarRun.isSelected()));
			menuRun.addActionListener(e -> toolbarRun.setSelected(menuRun.isSelected()));

			menuImpacts.addActionListener(e -> toolbarImpacts.setSelected(menuImpacts.isSelected()));
			toolbarImpacts.addActionListener(e -> menuImpacts.setSelected(toolbarImpacts.isSelected()));

			ButtonGroup menuGroup = new ButtonGroup();
			menuGroup.add(menuXor);
			menuGroup.add(menuReplace);
			menuReplace.setSelected(true);

			ButtonGroup toolbarGroup = new ButtonGroup();
			toolbarGroup.add(toolbarXor);
			toolbarGroup.add(toolbarReplace);
			toolbarReplace.setSelected(true);

			menuReplace.addActionListener(e -> toolbarReplace.setSelected(menuReplace.isSelected()));
			toolbarReplace.addActionListener(e -> menuReplace.setSelected(toolbarReplace.isSelected()));

			menuXor.addActionListener(e -> toolbarXor.setSelected(menuXor.isSelected()));
			toolbarXor.addActionListener(e -> menuXor.setSelected(toolbarXor.isSelected()));

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

	public static void main(String[] args) {
		InitMainWindow mainFrame = new InitMainWindow();
		mainFrame.setVisible(true);
	}

	public void onImpactShow() {
		if (view.isShowImpact()) {
			view.setShowImpact(false);
		} else {
			view.setShowImpact(true);
		}
	}

	public void onSettings() {
		new SettingsDialog(this, model);
		scrollPane.revalidate();
		scrollPane.repaint();
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
		SaveOfferDialog saveOfferDialog = new SaveOfferDialog(this);
		if (saveOfferDialog.getResult()) {
			onSave();
		}
		System.exit(0);
	}

	public void onNew() {
		SaveOfferDialog saveOfferDialog = new SaveOfferDialog(this);
		if (saveOfferDialog.getResult()) {
			onSave();
		}
		curFile = null;
		new CreationDialog(this);
		scrollPane.revalidate();
	}

	public void onOpen() {
		SaveOfferDialog saveOfferDialog = new SaveOfferDialog(this);
		if (saveOfferDialog.getResult()) {
			onSave();
		}
		JFileChooser fileChooser = new JFileChooser("FIT_14203_Evtushenko_Ilya_Life_Data");
		int res = fileChooser.showDialog(this, "Open");
		if (res == JFileChooser.APPROVE_OPTION) {
			try {
				curFile = fileChooser.getSelectedFile().getPath();
				model.loadFromFile(curFile);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Can not open file");
			}
		}
		scrollPane.revalidate();
	}

	public void onSave() {
		try {
			if (curFile == null) {
				onSaveAs();
			} else {
				writeModelToFile(new File(curFile));
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Can not save to file");
		}
	}

	public void onSaveAs() {
		try {
			JFileChooser fileChooser = new JFileChooser("FIT_14203_Evtushenko_Ilya_Life_Data");
			int res = fileChooser.showDialog(this, "Save");
			if (res == JFileChooser.APPROVE_OPTION) {
				writeModelToFile(fileChooser.getSelectedFile());
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Can not save to file");
		}
	}

	public void onReplace() {
		model.setXorFill(false);
	}

	public void onXor() {
		model.setXorFill(true);
	}

	private void writeModelToFile(File file) throws IOException {
		try (Writer writer = new FileWriter(file)) {
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
					if (model.getCellState(x, y)) {
						writer.write(x + " " + y + "\n");
					}
				}
			}
		}
	}

	public Model getModel() {
		return model;
	}
}
