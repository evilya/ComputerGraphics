package ru.nsu.fit.g14203.evtushenko.dialogs.panels;

import javax.swing.*;

public class XorReplacePanel extends JPanel {

	private final JRadioButton replaceButton;
	private final JRadioButton xorButton;

	public XorReplacePanel(boolean xorSelected) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		xorButton = new JRadioButton("XOR");
		replaceButton = new JRadioButton("Replace");
		ButtonGroup group = new ButtonGroup();
		group.add(xorButton);
		group.add(replaceButton);
		add(xorButton);
		add(replaceButton);
		if (xorSelected){
			xorButton.setSelected(true);
		} else {
			replaceButton.setSelected(true);
		}
	}

	public boolean isXor(){
		return xorButton.isSelected();
	}
}
