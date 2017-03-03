package ru.nsu.fit.g14203.evtushenko.dialogs;

import javax.swing.*;
import java.awt.*;

public class SaveOfferDialog extends JDialog {

	private boolean result;

	public SaveOfferDialog(Frame owner) {
		super(owner, "Save", true);

		setLocationRelativeTo(getParent());
		setResizable(false);
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;

		JLabel widthLable = new JLabel("Save this model?");
		c.gridx = 0;
		c.gridy = 0;
		add(widthLable, c);

		JButton yesButton = new JButton("Yes");
		c.gridx = 0;
		c.gridy = 1;
		add(yesButton, c);

		JButton noButton = new JButton("No");
		c.gridx = 1;
		c.gridy = 1;
		add(noButton, c);

		JButton cancelButton = new JButton("Cancel");
		c.gridx = 2;
		c.gridy = 1;
		add(cancelButton, c);

		yesButton.addActionListener(e -> {
			result = true;
			setVisible(false);
			dispose();
		});

		noButton.addActionListener(e -> {
			result = false;
			setVisible(false);
			dispose();
		});

		cancelButton.addActionListener(e -> {
			setVisible(false);
			dispose();
		});

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setVisible(true);
		cancelButton.requestFocus();
	}

	public boolean getResult() {
		return result;
	}
}
