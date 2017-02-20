package ru.nsu.fit.g14203.evtushenko;

import ru.nsu.fit.g14203.evtushenko.dialogs.CreationDialog;
import ru.nsu.fit.g14203.evtushenko.model.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class InitMainWindow extends MainFrame {
	public InitMainWindow() {
		super(600, 400, "LIFE");
		setMinimumSize(new Dimension(300, 300));
		try {
			addSubMenu("File", KeyEvent.VK_F);
			addMenuItem("File/Exit", "Exit application", KeyEvent.VK_X, "Exit.gif", "onExit");
			addMenuItem("File/New", "Create new field", KeyEvent.VK_N, "Exit.gif", "onNew");
			addSubMenu("Help", KeyEvent.VK_H);
			addMenuItem("Help/About", "Shows program version and copyright information", KeyEvent.VK_A, "About.gif", "onAbout");


			addToolBarButton("File/Exit");
			addToolBarSeparator();
			addToolBarButton("Help/About");


			JPanel imageView = new InitView();
			JScrollPane scrollPane = new JScrollPane(imageView);
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

	public void onNew(){
		JDialog dialog = new CreationDialog(this);

	}

	public static void main(String[] args) {
		InitMainWindow mainFrame = new InitMainWindow();
		mainFrame.setVisible(true);
	}
}
