package com.braids.burncoffeeman.client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import com.braids.burncoffeeman.common.BombModel;
import com.braids.burncoffeeman.common.ClientInputModel;
import com.braids.burncoffeeman.common.Constants;
import com.braids.burncoffeeman.common.GfxByteModel;
import com.braids.burncoffeeman.common.GraphicsTemplateManager;
import com.braids.burncoffeeman.common.LevelModel;
import com.braids.burncoffeeman.common.PlayerInfoModel;
import com.braids.burncoffeeman.common.PlayerModel;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class MainClient {

	private CommunicationClient comm;
	private ClientInputModel    clientInputModel;
	private LevelModel          levelModel;
	private Displayer           displayer;
	private Players             players;
	private Bombs               bombs;
	private ClientSettings      clientSettings;

	public MainClient() throws IOException {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		GraphicsTemplateManager.init();

		levelModel = new LevelModel(Constants.LEVEL_WIDTH, Constants.LEVEL_HEIGHT);
		players = new Players();
		bombs = new Bombs();

		clientInputModel = new ClientInputModel();

		new RepeatingReleasedEventsFixer().install();

		CellConstraints cc = new CellConstraints();

		JFrame frame = new JFrame("Burn Coffeeman");
		frame.setLayout(new FormLayout("f:100dlu:g", "f:100dlu:g"));

		displayer = new Displayer();
		displayer.setLevelModel(levelModel);
		displayer.setPlayers(players);
		displayer.setBombs(bombs);

		clientSettings = new ClientSettings();

		JPanel pnlServerConfig = new JPanel(new FormLayout("p,2dlu,p", "p,2dlu,p"));
		pnlServerConfig.setName("Server");

		JButton btnStartServer = new JButton(new StartServerAction());

		pnlServerConfig.add(btnStartServer, cc.xy(3, 1));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		tabbedPane.add(displayer);
		tabbedPane.add(clientSettings.getDisplayComponent());
		tabbedPane.add(pnlServerConfig);

		frame.add(tabbedPane, cc.xy(1, 1));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		frame.setSize(Constants.LEVEL_WIDTH * 32, Constants.LEVEL_HEIGHT * 32);

		setDefaults();
	}

	private void setDefaults() {
		clientSettings.setDefaults();
	}

	public static void main(String[] args) throws IOException {
		new MainClient();
	}

	public void startClient() throws IOException {
		comm = new CommunicationClient(this);
		new Thread(new Runnable() {

			public void run() {
				try {
					comm.execute();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}).start();
		comm.waitUntilServerReady();

		PlayerInfoModel playerInfo = new PlayerInfoModel();
		playerInfo.setColor1(Color.GREEN);
		playerInfo.setColor2(Color.BLUE);
		playerInfo.setName("Captian Demo");
		playerInfo.setGfxHeadGroup("colored");
		playerInfo.setGfxBodyGroup("default");
		playerInfo.setGfxLegsGroup("default");

		comm.outComm(playerInfo.code());

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			public void run() {
				try {
					comm.flush();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}, 40, 40);

		timer.schedule(new TimerTask() {

			DecimalFormat twoPlaces = new DecimalFormat("0.00");

			private String formatIntToKb(int i) {

				return twoPlaces.format(((float) i) / 1024);
			}

			public void run() {
				System.out.println("IN/OUT kb/s: " + formatIntToKb(comm.getStatisticsInputBytes() / 10) + " "
				        + formatIntToKb(comm.getStatisticsOutputBytes() / 10));
				comm.resetInputOutputStatistics();
			}
		}, 10000, 10000);

		displayer.addKeyListener(new GameKeyListener(clientInputModel, comm));
	}

	public LevelModel getLevelModel() {
		return levelModel;
	}

	public void setPlayerModel(PlayerModel data) {
		players.setPlayerModel(data);
	}

	public void setBombModel(BombModel data) {
		bombs.setBombModel(data);
	}

	public void addAnimTileModel(GfxByteModel data) throws IOException {
		displayer.addAnimTileModel(data);
	}

	public void setWallImage(GfxByteModel data) throws IOException {
		displayer.setWallImage(data);
	}

	public void setFireImage(GfxByteModel data) throws IOException {
		displayer.setFireImage(data);
	}

	public void setBombImage(GfxByteModel data) throws IOException {
		displayer.setBombImage(data);
	}

	public void setPlayerInfoModel(PlayerInfoModel data) {
		players.setPlayerInfoModel(data);
	}

	private class StartServerAction extends AbstractAction {

		public StartServerAction() {
			super("Start server");
		}

		public void actionPerformed(ActionEvent e) {
			try {
				startClient();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}
}
