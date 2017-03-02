package ru.nsu.fit.g14203.evtushenko.dialogs;

import ru.nsu.fit.g14203.evtushenko.EventType;
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

		cellSizeSlider = new SliderEditPanel(3, 50, model.getCellSize());
		cellSizeSlider.setPreferredSize(new Dimension(250, 70));
		cellSizeSlider.setBorder(BorderFactory.createTitledBorder("Cell size"));
		c.gridx = 0;
		add(cellSizeSlider, c);

		lineThicknessSlider = new SliderEditPanel(1, 10, model.getLineThickness());
		lineThicknessSlider.setPreferredSize(new Dimension(250, 70));
		lineThicknessSlider.setBorder(BorderFactory.createTitledBorder("Line Thickness"));
		c.gridx = 1;
		add(lineThicknessSlider, c);


		lifeParametersPanel = new LifeParametersPanel(
				model.getLiveBegin(),
				model.getLiveEnd(),
				model.getBirthBegin(),
				model.getBirthEnd(),
				model.getFirstImpact(),
				model.getSecondImpact());
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

		okButton.addActionListener(new OnOkListener());

		JButton cancelButton = new JButton("Cancel");
		c.gridx = 1;
		add(cancelButton, c);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setVisible(true);
	}

	private class OnOkListener implements ActionListener {

		private boolean checkParameters(){
			int width = widthHeightPanel.getWidth();
			int height = widthHeightPanel.getHeight();
			double lifeBegin = lifeParametersPanel.getLifeBeginField();
			double lifeEnd = lifeParametersPanel.getLifeEndField();
			double birthBegin = lifeParametersPanel.getBirthBeginField();
			double birthEnd = lifeParametersPanel.getBirthEndField();
			int lineThickness = lineThicknessSlider.getValue();
			int cellSize = cellSizeSlider.getValue();

			return width >= 1
					&& height >= 1
					&& lifeBegin <= birthBegin
					&& birthBegin <= birthEnd
					&& birthEnd <= lifeEnd
					&& cellSize > lineThickness;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (checkParameters()) {
				model.setWidthHeight(widthHeightPanel.getWidthValue(), widthHeightPanel.getHeightValue());
				model.setLineThickness(lineThicknessSlider.getValue());
				model.setCellSize(cellSizeSlider.getValue());
				model.setXorFill(xorReplacePanel.isXor());
				model.setLiveBegin(lifeParametersPanel.getLifeBeginField());
				model.setLiveEnd(lifeParametersPanel.getLifeEndField());
				model.setBirthBegin(lifeParametersPanel.getBirthBeginField());
				model.setBirthEnd(lifeParametersPanel.getBirthEndField());
				model.setFirstImpact(lifeParametersPanel.getFirstImpactField());
				model.setSecondImpact(lifeParametersPanel.getSecondImpactField());
				model.notifyObservers(EventType.RESIZE);
				setVisible(false);
				dispose();
			} else {
				JOptionPane.showMessageDialog(SettingsDialog.this, "Illegal parameters");
			}
		}
	}
}
