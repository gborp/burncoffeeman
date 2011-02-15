package com.braids.burncoffeeman.client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.braids.burncoffeeman.common.Activity;
import com.braids.burncoffeeman.common.AnimTileModel;
import com.braids.burncoffeeman.common.BombModel;
import com.braids.burncoffeeman.common.Constants;
import com.braids.burncoffeeman.common.GraphicsTemplateManager;
import com.braids.burncoffeeman.common.LevelModel;
import com.braids.burncoffeeman.common.LevelTileModel;
import com.braids.burncoffeeman.common.PlayerModel;

public class Displayer extends JPanel {

	private LevelModel              levelModel;
	private Players                 players;
	private Bombs                   bombs;
	private GraphicsTemplateManager gtm;

	public Displayer() {
		gtm = GraphicsTemplateManager.getInstance();
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

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());

		int componentWidth = getWidth() / levelModel.getWidth();
		int componentHeight = getHeight() / levelModel.getHeight();

		int componentSize = Math.min(componentWidth, componentHeight);

		g.translate((getWidth() - componentSize * levelModel.getWidth()) / 2, (getHeight() - componentSize * levelModel.getHeight()) / 2);

		int virtualSize = Constants.COMPONENT_SIZE_IN_VIRTUAL;
		float divider = virtualSize / componentSize;

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

		Collection<PlayerModel> lstPlayers = players.getPlayerModels();

		for (PlayerModel playerModel : lstPlayers) {
			int x = (int) ((playerModel.getX() - Constants.COMPONENT_SIZE_IN_VIRTUAL / 2) / divider);
			int y = (int) ((playerModel.getY() - Constants.COMPONENT_SIZE_IN_VIRTUAL / 2) / divider);

			int phaseCount = playerModel.getAnimationPhase() / 640;

			Activity activity = playerModel.getActivity();
			Activity activityForGfx = getActivityForGfx(activity);

			BufferedImage playerGfx = ScaledGfxHelper.getPlayer(componentSize, Color.RED, Color.YELLOW, "default", activityForGfx, playerModel.getDirection(),
			        phaseCount);
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

	private void drawTile(Graphics g, int x, int y, float componentSize, LevelTileModel tile) {
		switch (tile.getWall()) {
			case GROUND:
				if (tile.hasFire()) {
					g.setColor(Color.YELLOW);
				} else {
					g.setColor(Color.GREEN);
				}
				break;
			case WALL:
				g.setColor(Color.DARK_GRAY);
				break;
			case BREAKABLE_WALL:
				g.setColor(Color.BLUE);
				break;
		}
		g.fillRect((int) (x * componentSize), (int) (y * componentSize), (int) componentSize, (int) componentSize);
	}

	public void addAnimTileModel(AnimTileModel data) {
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(data.getGfx());
			BufferedImage image = ImageIO.read(is);
			is.close();
			gtm.loadAnim(image, data.getGroupName(), data.getPhaseType());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
