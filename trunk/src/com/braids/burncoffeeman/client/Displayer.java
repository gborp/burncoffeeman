package com.braids.burncoffeeman.client;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Collection;

import javax.swing.JPanel;

import com.braids.burncoffeeman.common.BombModel;
import com.braids.burncoffeeman.common.Constants;
import com.braids.burncoffeeman.common.LevelModel;
import com.braids.burncoffeeman.common.LevelTileModel;
import com.braids.burncoffeeman.common.PlayerModel;

public class Displayer extends JPanel {

	private LevelModel levelModel;
	private Players    players;
	private Bombs      bombs;

	public Displayer() {}

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

		int componentSize = 32;
		for (int y = 0; y < levelModel.getHeight(); y++) {
			for (int x = 0; x < levelModel.getWidth(); x++) {
				LevelTileModel tile = levelModel.getTile(x, y);
				if (tile != null) {
					drawTile(g, x, y, componentSize, tile);
				}
			}
		}

		int virtualSize = Constants.COMPONENT_SIZE_IN_VIRTUAL;
		float divider = virtualSize / componentSize;

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

			g.setColor(Color.RED);
			g.fillOval(x, y, componentSize, componentSize);
		}
	}

	private void drawTile(Graphics g, int x, int y, int componentSize, LevelTileModel tile) {
		switch (tile.getWall()) {
			case GROUND:
				g.setColor(Color.GREEN);
				break;
			case WALL:
				g.setColor(Color.DARK_GRAY);
				break;
			case BREAKABLE_WALL:
				g.setColor(Color.BLUE);
				break;
		}
		g.fillRect(x * componentSize, y * componentSize, componentSize, componentSize);
	}

}
