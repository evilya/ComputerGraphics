package ru.nsu.fit.g14203.evtushenko;

import ru.nsu.fit.g14203.evtushenko.dialogs.CreationDialog;
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
	private InitView view;

	public InitMainWindow() {
		super(600, 400, "LIFE");
		setMinimumSize(new Dimension(300, 300));
		try {
			addSubMenu("File", KeyEvent.VK_F);
			addMenuItem("File/New", "Create new field", KeyEvent.VK_N, "Exit.gif", "onNew");
			addMenuItem("File/Open", "Open field", KeyEvent.VK_O, "Exit.gif", "onOpen");
			addMenuItem("File/Save", "Save field", KeyEvent.VK_O, "Exit.gif", "onSave");
			addMenuItem("File/Exit", "Exit application", KeyEvent.VK_X, "Exit.gif", "onExit");
			addSubMenu("Help", KeyEvent.VK_H);
			addMenuItem("Help/About", "Shows program version and copyright information", KeyEvent.VK_A, "About.gif", "onAbout");


			addToolBarButton("File/Exit");
			addToolBarSeparator();
			addToolBarButton("Help/About");

			model = new Model();

			view = new InitView(model);
			scrollPane = new JScrollPane(view);
			scrollPane.getVerticalScrollBar().setUnitIncrement(5);

			add(scrollPane);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public void onAbout() {
		JOptionPane.showMessageDialog(this,
				"Sample, version 0.6\nEvtushenko Ilya, 14203 FIT NSU", "About Init", JOptionPane.INFORMATION_MESSAGE);
	}

	public void onExit() {
		System.exit(0);
	}

	public void onNew() {
		JDialog dialog = new CreationDialog(this);
	}

	public void onOpen() {
		JFileChooser fileChooser = new JFileChooser();
		int res = fileChooser.showDialog(this, "Open");
		if (res == JFileChooser.APPROVE_OPTION) {
			try {
				model = new Model(fileChooser.getSelectedFile().getPath());
				setModelToView(model);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Can not open file");
			}
		}
	}

	public void onSave() {
		JFileChooser fileChooser = new JFileChooser();
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
				Cell[][] cells = model.getPrevCells();
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
						if (cells[y][x].isAlive()) {
							writer.write(x + " " + y+"\n");
						}
					}
				}
				writer.close();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Can not save to file");
			}
		}
	}

	public void setModelToView(Model model) {
		view.setModel(model);
		view.repaint();
		scrollPane.revalidate();
		scrollPane.repaint();
		view.update();
	}

	public static void main(String[] args) {
		InitMainWindow mainFrame = new InitMainWindow();
		mainFrame.setVisible(true);
	}
}
