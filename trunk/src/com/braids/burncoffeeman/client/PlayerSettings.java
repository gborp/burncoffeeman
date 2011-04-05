package com.braids.burncoffeeman.client;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class PlayerSettings {

	private final int  localPlayerId;
	private JCheckBox  cbActive;
	private JTextField sfName;

	public PlayerSettings(int localPlayerId) {
		this.localPlayerId = localPlayerId;
	}

	public JComponent getDisplayComponent() {
		CellConstraints cc = new CellConstraints();

		JPanel panel = new JPanel(new FormLayout("p,2dlu,p:g", "p,2dlu,p,2dlu,p"));
		panel.setName("Player " + (localPlayerId + 1));

		cbActive = new JCheckBox();
		sfName = new JTextField(20);

		panel.add(new JLabel("Active"), cc.xy(1, 1));
		panel.add(cbActive, cc.xy(3, 1));

		panel.add(new JLabel("Name"), cc.xy(1, 3));
		panel.add(sfName, cc.xy(3, 3));

		return panel;
	}
}
