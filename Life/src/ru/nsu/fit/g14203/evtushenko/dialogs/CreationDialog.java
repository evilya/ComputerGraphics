package ru.nsu.fit.g14203.evtushenko.dialogs;

import ru.nsu.fit.g14203.evtushenko.model.Model;
import ru.nsu.fit.g14203.evtushenko.view.InitMainWindow;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class CreationDialog extends JDialog {

	public CreationDialog(Frame owner) {
		super(owner, "New document", true);

		setLocationRelativeTo(getParent());
		setResizable(false);
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;

		JLabel widthLable = new JLabel("Width: ");
		c.gridx = 0;
		c.gridy = 0;
		add(widthLable, c);

		JLabel heightLable = new JLabel("Height: ");
		c.gridx = 0;
		c.gridy = 1;
		add(heightLable, c);

		JButton okButton = new JButton("Ok");
		c.gridx = 0;
		c.gridy = 2;
		add(okButton, c);

		JButton cancelButton = new JButton("Cancel");
		c.gridx = 1;
		c.gridy = 2;
		add(cancelButton, c);

		JFormattedTextField widthField = new JFormattedTextField(new DecimalFormat("#"));
		c.gridx = 1;
		c.gridy = 0;
		add(widthField, c);

		JFormattedTextField heightField = new JFormattedTextField(new DecimalFormat("#"));
		c.gridx = 1;
		c.gridy = 1;
		add(heightField, c);

		okButton.addActionListener(e -> {
			Model model = ((InitMainWindow) owner).getModel();
			model.setWidthHeight(Integer.parseInt(widthField.getText()),
					Integer.parseInt(heightField.getText()));
			setVisible(false);
			dispose();
		});

		cancelButton.addActionListener(e -> {
			setVisible(false);
			dispose();
		});

		cancelButton.addActionListener(e -> dispose());

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setVisible(true);
		cancelButton.requestFocus();
	}
}
