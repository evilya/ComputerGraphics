package ru.nsu.fit.g14203.evtushenko;

import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

import ru.nsu.fit.g14203.evtushenko.MainFrame;


public class InitMainWindow extends MainFrame {
	/**
	 * Default constructor to create main window
	 */
	public InitMainWindow()
	{
		super(600, 400, "Init application");

		try
		{
			addSubMenu("File", KeyEvent.VK_F);
			addMenuItem("File/Exit", "Exit application", KeyEvent.VK_X, "Exit.gif", "onExit");
			addSubMenu("Help", KeyEvent.VK_H);
			addMenuItem("Help/About...", "Shows program version and copyright information", KeyEvent.VK_A, "About.gif", "onAbout");
			
			addToolBarButton("File/Exit");
			addToolBarSeparator();
			addToolBarButton("Help/About...");
			
			add(new InitView());
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Help/About... - shows program version and copyright information
	 */
	public void onAbout()
	{
		JOptionPane.showMessageDialog(this, "Init, version 1.0\nCopyright © 2010 Vasya Pupkin, FF, group 1234", "About Init", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * File/Exit - exits application
	 */
	public void onExit()
	{
		System.exit(0);
	}
	
	/**
	 * Application main entry point
	 * @param args command line arguments (unused)
	 */
	public static void main(String[] args)
	{
		InitMainWindow mainFrame = new InitMainWindow();
		mainFrame.setVisible(true);
	}
}
