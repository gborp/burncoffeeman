package com.braids.burncoffeeman.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

import com.braids.burncoffeeman.common.AnimTileModel;
import com.braids.burncoffeeman.common.BombModel;
import com.braids.burncoffeeman.common.ClientInputModel;
import com.braids.burncoffeeman.common.Constants;
import com.braids.burncoffeeman.common.GraphicsTemplateManager;
import com.braids.burncoffeeman.common.LevelModel;
import com.braids.burncoffeeman.common.PlayerInfoModel;
import com.braids.burncoffeeman.common.PlayerModel;

public class MainClient {

	private CommunicationClient comm;
	private ClientInputModel    clientInputModel;
	private LevelModel          levelModel;
	private Displayer           displayer;
	private Players             players;
	private Bombs               bombs;

	public MainClient() throws IOException {

		GraphicsTemplateManager.init();

		levelModel = new LevelModel(Constants.LEVEL_WIDTH, Constants.LEVEL_HEIGHT);
		players = new Players();
		bombs = new Bombs();

		clientInputModel = new ClientInputModel();

		new RepeatingReleasedEventsFixer().install();

		JFrame frame = new JFrame();

		displayer = new Displayer();
		displayer.setLevelModel(levelModel);
		displayer.setPlayers(players);
		displayer.setBombs(bombs);
		displayer.setFocusable(true);

		frame.setLayout(new BorderLayout());
		frame.add(displayer, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		frame.setSize(Constants.LEVEL_WIDTH * 32, Constants.LEVEL_HEIGHT * 32);

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
		playerInfo.setGfxHeadGroup("default");
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

		// timer.schedule(new TimerTask() {
		//
		// DecimalFormat twoPlaces = new DecimalFormat("0.00");
		//
		// private String formatIntToKb(int i) {
		//
		// return twoPlaces.format(((float) i) / 1024);
		// }
		//
		// public void run() {
		// System.out.println("IN/OUT kb/s: " +
		// formatIntToKb(comm.getStatisticsInputBytes()) + " " +
		// formatIntToKb(comm.getStatisticsOutputBytes()));
		// comm.resetInputOutputStatistics();
		// }
		// }, 1000, 1000);

		displayer.addKeyListener(new GameKeyListener(clientInputModel, comm));
	}

	public static void main(String[] args) throws IOException {
		new MainClient();

	}

	public LevelModel getLevelModel() {
		return levelModel;
	}

	public void refreshDisplay() {
		displayer.repaint();
	}

	public void setPlayerModel(PlayerModel data) {
		players.setPlayerModel(data);
	}

	public void setBombModel(BombModel data) {
		bombs.setBombModel(data);
	}

	public void addAnimTileModel(AnimTileModel data) {
		displayer.addAnimTileModel(data);
	}

}
