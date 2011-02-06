package com.braids.burncoffeeman.common;

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

}
