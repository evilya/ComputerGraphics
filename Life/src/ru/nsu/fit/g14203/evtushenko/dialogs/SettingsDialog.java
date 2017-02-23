package ru.nsu.fit.g14203.evtushenko.dialogs;

import ru.nsu.fit.g14203.evtushenko.dialogs.panels.LifeParametersPanel;
import ru.nsu.fit.g14203.evtushenko.dialogs.panels.SliderEditPanel;
import ru.nsu.fit.g14203.evtushenko.dialogs.panels.WidthHeightPanel;
import ru.nsu.fit.g14203.evtushenko.dialogs.panels.XorReplacePanel;
import ru.nsu.fit.g14203.evtushenko.model.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsDialog extends JDialog {

	private final WidthHeightPanel widthHeightPanel;
	private final XorReplacePanel xorReplacePanel;
	private final SliderEditPanel cellSizeSlider;
	private final SliderEditPanel lineThicknessSlider;
	private final LifeParametersPanel lifeParametersPanel;
	private final Model model;

	public SettingsDialog(Frame owner, Model model) {
		super(owner, "Settings", true);
		this.model = model;
		setPreferredSize(new Dimension(540, 320));
		setLocationRelativeTo(getParent());
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridy = 0;

		widthHeightPanel = new WidthHeightPanel(model.getWidth(), model.getHeight());
		widthHeightPanel.setPreferredSize(new Dimension(250, 70));
		widthHeightPanel.setBorder(BorderFactory.createTitledBorder("Field Size"));
		c.gridx = 0;
		add(widthHeightPanel, c);

		xorReplacePanel = new XorReplacePanel(model.isXorFill());
		xorReplacePanel.setPreferredSize(new Dimension(250, 70));
		xorReplacePanel.setBorder(BorderFactory.createTitledBorder("Fill mode"));
		c.gridx = 1;
		add(xorReplacePanel, c);

		c.gridy = 1;

		cellSizeSlider = new SliderEditPanel(3, 100, model.getCellSize());
		cellSizeSlider.setPreferredSize(new Dimension(250, 70));
		cellSizeSlider.setBorder(BorderFactory.createTitledBorder("Cell size"));
		c.gridx = 0;
		add(cellSizeSlider, c);

		lineThicknessSlider = new SliderEditPanel(1, 10, model.getLineThickness());
		lineThicknessSlider.setPreferredSize(new Dimension(250, 70));
		lineThicknessSlider.setBorder(BorderFactory.createTitledBorder("Line Thickness"));
		c.gridx = 1;
		add(lineThicknessSlider, c);


		lifeParametersPanel = new LifeParametersPanel(1.2f, 0.f, 0.f, 0.f, 0.f, 0.f);
		lifeParametersPanel.setBorder(BorderFactory.createTitledBorder("Life parameters"));
		c.gridwidth = 2;
		c.gridy = 2;
		c.gridx = 0;
		add(lifeParametersPanel, c);

		c.gridwidth = 1;
		c.gridy = 3;

		JButton okButton = new JButton("Ok");
		c.gridx = 0;
		add(okButton, c);

		okButton.addActionListener(new onOkListener());

		JButton cancelButton = new JButton("Cancel");
		c.gridx = 1;
		add(cancelButton, c);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setVisible(true);
	}

	private class onOkListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (widthHeightPanel.getWidthValue() >= 1 || widthHeightPanel.getHeightValue() >= 1) {
				model.setWidthHeight(widthHeightPanel.getWidthValue(), widthHeightPanel.getHeightValue());
				model.setLineThickness(lineThicknessSlider.getValue());
				model.setCellSize(cellSizeSlider.getValue());
				model.setXorFill(xorReplacePanel.isXor());
				//TODO
				setVisible(false);
				dispose();
			} else {
				JOptionPane.showMessageDialog(SettingsDialog.this, "Illegal parameters");
			}
		}
	}
}
