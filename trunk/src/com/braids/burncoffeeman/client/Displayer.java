package com.braids.burncoffeeman.client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

import com.braids.burncoffeeman.common.Activity;
import com.braids.burncoffeeman.common.BombModel;
import com.braids.burncoffeeman.common.Constants;
import com.braids.burncoffeeman.common.GfxByteModel;
import com.braids.burncoffeeman.common.GraphicsTemplateManager;
import com.braids.burncoffeeman.common.Helper;
import com.braids.burncoffeeman.common.LevelModel;
import com.braids.burncoffeeman.common.LevelTileModel;
import com.braids.burncoffeeman.common.PlayerInfoModel;
import com.braids.burncoffeeman.common.PlayerModel;

public class Displayer extends JPanel {

	private LevelModel              levelModel;
	private Players                 players;
	private Bombs                   bombs;
	private GraphicsTemplateManager gtm;
	private int                     animationCounter;

	public Displayer() {
		gtm = GraphicsTemplateManager.getInstance();

		new Timer(Constants.MAIN_CYCLE_PERIOD, new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		}).start();
	}

	public void setLevelModel(LevelModel levelModel) {
		this.levelModel = levelModel;
	}

	public void setPlayers(Players players) {
		this.players = players;
	}

	public void setBombs(Bombs bombs) {
		this.bombs = bombs;
	}

	protected void paintComponent(Graphics g) {
		// super.paintComponent(g);

		if (levelModel == null) {
			return;
		}

		animationCounter = (animationCounter + 1) & 0xffff;

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());

		int componentWidth = getWidth() / levelModel.getWidth();
		int componentHeight = getHeight() / levelModel.getHeight();

		int componentSize = Math.min(componentWidth, componentHeight);

		g.translate((getWidth() - componentSize * levelModel.getWidth()) / 2, (getHeight() - componentSize * levelModel.getHeight()) / 2);

		int virtualSize = Constants.COMPONENT_SIZE_IN_VIRTUAL;
		float divider = virtualSize / (float) componentSize;

		for (int y = 0; y < levelModel.getHeight(); y++) {
			for (int x = 0; x < levelModel.getWidth(); x++) {
				LevelTileModel tile = levelModel.getTile(x, y);
				if (tile != null) {
					drawTile(g, x, y, componentSize, tile);
				}
			}
		}

		Collection<BombModel> lstBombs = bombs.getBombModels();
		for (BombModel b : lstBombs) {
			int x = (int) ((b.getX() - Constants.COMPONENT_SIZE_IN_VIRTUAL / 2) / divider);
			int y = (int) ((b.getY() - Constants.COMPONENT_SIZE_IN_VIRTUAL / 2) / divider);

			g.setColor(Color.BLACK);
			g.fillOval(x, y, componentSize, componentSize);
		}

		List<PlayerModel> lstPlayers = players.getPlayerModels();
		for (PlayerModel playerModel : lstPlayers) {
			int x = (int) ((playerModel.getX() - Constants.COMPONENT_SIZE_IN_VIRTUAL * 1.5 / 2) / divider);
			int y = (int) ((playerModel.getY() - Constants.COMPONENT_SIZE_IN_VIRTUAL) / divider);

			int phaseCount = playerModel.getAnimationPhase() / 640;

			Activity activity = playerModel.getActivity();
			Activity activityForGfx = getActivityForGfx(activity);

			BufferedImage playerGfx = ScaledGfxHelper.getPlayer((int) (componentSize * 1.5), Color.RED, Color.YELLOW, "default", activityForGfx, playerModel
			        .getDirection(), phaseCount);
			g.drawImage(playerGfx, x, y, null);
		}
	}

	private Activity getActivityForGfx(Activity activity) {
		if (activity == Activity.KICKING_WITH_BOMB) {
			activity = Activity.KICKING;
		} else if (activity == Activity.STANDING_WITH_BOMB) {
			activity = Activity.STANDING;
		} else if (activity == Activity.WALKING_WITH_BOMB) {
			activity = Activity.WALKING;
		}
		return activity;
	}

	private void drawTile(Graphics g, int x, int y, int componentSize, LevelTileModel tile) {

		g.drawImage(ScaledGfxHelper.getWall(componentSize, tile.getWall()), (x * componentSize), (y * componentSize), null);
		if (tile.hasFire()) {
			Color color1;
			Color color2;
			if (tile.getFireOwnerId() == Constants.NOONES_BOMB) {
				color1 = Color.black;
				color2 = Color.black;
			} else {
				PlayerInfoModel playerInfo = players.getPlayerInfoModel(tile.getFireOwnerId());
				color1 = playerInfo.getColor1();
				color2 = playerInfo.getColor2();
			}
			g.drawImage(ScaledGfxHelper.getFire(componentSize, tile.getFire(), color1, color2,
			        ((animationCounter * 8 / Constants.MAIN_CYCLE_PER_SEC & 255) + x + y) % 5), (x * componentSize), (y * componentSize), null);
		}
	}

	public void addAnimTileModel(GfxByteModel data) throws IOException {
		gtm.loadAnim(Helper.loadImageFromByteArray(data.getGfx()), data.getGroupName(), data.getPhaseType());
	}

	public void setWallImage(GfxByteModel data) throws IOException {
		gtm.loadWalls(Helper.loadImageFromByteArray(data.getGfx()));
	}

	public void setFireImage(GfxByteModel data) throws IOException {
		gtm.loadFires(Helper.loadImageFromByteArray(data.getGfx()));
	}
}
