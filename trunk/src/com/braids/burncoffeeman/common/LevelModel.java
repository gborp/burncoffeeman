package com.braids.burncoffeeman.common;

import java.util.ArrayList;
import java.util.List;

public class LevelModel {

	private LevelTileModel[][] levelTiles;
	private int                width;
	private int                height;

	public LevelModel(int width, int height) {
		this.width = width;
		this.height = height;
		levelTiles = new LevelTileModel[width][height];
	}

	public void setTile(LevelTileModel levelTileModel) {
		levelTiles[levelTileModel.getX()][levelTileModel.getY()] = levelTileModel;
	}

	public LevelTileModel getTile(int x, int y) {
		return levelTiles[x][y];
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getWidth() {
		return width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getHeight() {
		return height;
	}

	public void resetStateChanged() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				levelTiles[x][y].resetStateChanged();
			}
		}
	}

	public void cycle() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				levelTiles[x][y].cycle();
			}
		}
	}

	public List<LevelTileModel> getChangedTiles() {
		ArrayList<LevelTileModel> lstResult = new ArrayList<LevelTileModel>();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				LevelTileModel tile = levelTiles[x][y];
				if (tile.isStateChanged()) {
					lstResult.add(tile);
				}
			}
		}
		return lstResult;
	}
}
