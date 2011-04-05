package com.braids.burncoffeeman.client;

import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.braids.burncoffeeman.common.Constants;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class ClientSettings {

	private ArrayList<PlayerSettings> lstPlayerSettings;

	public ClientSettings() {

	}

	public JComponent getDisplayComponent() {
		CellConstraints cc = new CellConstraints();

		JPanel pnlClientConfig = new JPanel(new FormLayout("p,2dlu,p:g", "p,2dlu,f:p:g"));
		pnlClientConfig.setName("Client");

		JTabbedPane panePlayers = new JTabbedPane(JTabbedPane.TOP);
		lstPlayerSettings = new ArrayList<PlayerSettings>();
		for (int i = 0; i < Constants.MAX_LOCAL_PLAYER_NUMBER; i++) {
			PlayerSettings playerSettings = new PlayerSettings(i);
			panePlayers.add(playerSettings.getDisplayComponent());
		}

		pnlClientConfig.add(panePlayers, cc.xywh(1, 3, 3, 1));

		return pnlClientConfig;
	}

	public void setDefaults() {
	// TODO
	// lstPlayerSettings

	}

}
