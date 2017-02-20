package ru.nsu.fit.g14203.evtushenko.dialogs;

import javax.swing.*;
import java.awt.*;

public class CreationDialog extends JDialog {

	public CreationDialog(Frame owner) {
		super(owner);

		setResizable(false);
		setModal(true);
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.insets = new Insets(5,5,5,5);

		JLabel widthLable = new JLabel("Width: ");
		c.gridx = 0;
		c.gridy = 0;
		add(widthLable, c);

		JTextField widthField= new JTextField("10");
		c.gridx = 1;
		c.gridy = 0;
		add(widthField, c);


		JLabel heightLable = new JLabel("Height: ");
		c.gridx = 0;
		c.gridy = 1;
		add(heightLable, c);

		JTextField heightField= new JTextField("10");
		c.gridx = 1;
		c.gridy = 1;
		add(heightField, c);

		JButton okButton = new JButton("Ok");
		c.gridx = 0;
		c.gridy = 2;
		add(okButton, c);

		JButton cancelButton = new JButton("Cancel");
		c.gridx = 1;
		c.gridy = 2;
		add(cancelButton, c);


		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setVisible(true);
		cancelButton.requestFocus();
	}
}
