package ru.nsu.fit.g14203.evtushenko.dialogs;

import ru.nsu.fit.g14203.evtushenko.InitMainWindow;
import ru.nsu.fit.g14203.evtushenko.InitView;
import ru.nsu.fit.g14203.evtushenko.model.Model;

import javax.jws.WebParam;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class CreationDialog extends JDialog {

	public CreationDialog(Frame owner) {
		super(owner, "New document", true);

		setLocationRelativeTo(getParent());
		setResizable(false);
		setLayout(new GridBagLayout());

		KeyAdapter intsOnlyAdapter = new IntsOnlyAdapter();

		GridBagConstraints c = new GridBagConstraints();

		c.insets = new Insets(5, 5, 5, 5);

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

		c.fill = GridBagConstraints.HORIZONTAL;

		JTextField widthField = new JFormattedTextField("10");
		widthField.addKeyListener(intsOnlyAdapter);
		c.gridx = 1;
		c.gridy = 0;
		add(widthField, c);

		JTextField heightField = new JTextField("10");
		heightField.addKeyListener(intsOnlyAdapter);
		c.gridx = 1;
		c.gridy = 1;
		add(heightField, c);

		okButton.addActionListener(e -> {
			Model model = new Model();
			model.setWidth(Integer.parseInt(widthField.getText()));
			model.setHeight(Integer.parseInt(heightField.getText()));
			((InitMainWindow)owner).setModelToView(model);
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

	private static class IntsOnlyAdapter extends KeyAdapter {
		@Override
		public void keyTyped(KeyEvent e) {
			char c = e.getKeyChar();
			if (c < '0' || c > '9') {
				e.consume();
			}
		}
	}
}
