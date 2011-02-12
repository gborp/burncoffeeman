package com.braids.burncoffeeman.common;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class AnimTilePhase {

	private String            groupName;
	private AnimTilePhaseType type;
	private Direction         direction;
	private Activity          activityType;
	private int               phaseNumber;

	private Color[][]         arGfx;

	public AnimTilePhase(String groupName, AnimTilePhaseType type, Activity activityType, Direction direction, int phase) {
		this.groupName = groupName;
		this.type = type;
		this.activityType = activityType;
		this.direction = direction;
		this.phaseNumber = phase;
		arGfx = new Color[getHeight()][getWidth()];
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				arGfx[y][x] = new Color(0, 0, 0, 0);
			}
		}
	}

	public int getWidth() {
		return 16;
	}

	public int getHeight() {
		return getType().getHeight();
	}

	public Color getPixel(int x, int y) {
		return arGfx[y][x];
	}

	public void setPixel(int x, int y, Color color) {
		arGfx[y][x] = color;
	}

	public void saveToFile(File file) {
		BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				image.setRGB(x, y, arGfx[y][x].getRGB());
			}
		}
		try {
			ImageIO.write(image, "png", file);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void saveToBitmap(BufferedImage image, int xoffset, int yoffset) {
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				image.setRGB(xoffset + x, yoffset + y, arGfx[y][x].getRGB());
			}
		}
	}

	public void loadFromBitmap(BufferedImage image, int xoffset, int yoffset) {
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				Color color = new Color(image.getRGB(x + xoffset, y + yoffset), true);
				if (color.getAlpha() > 0 && color.getAlpha() < 255) {
					if (color.getRed() == Constants.COLOR_KEY_OWN_COLOR_1.getRed() && color.getGreen() == Constants.COLOR_KEY_OWN_COLOR_1.getGreen()
					        && color.getBlue() == Constants.COLOR_KEY_OWN_COLOR_1.getBlue()) {
						color = Constants.COLOR_KEY_OWN_COLOR_1;
					} else if (color.getRed() == Constants.COLOR_KEY_OWN_COLOR_2.getRed() && color.getGreen() == Constants.COLOR_KEY_OWN_COLOR_2.getGreen()
					        && color.getBlue() == Constants.COLOR_KEY_OWN_COLOR_2.getBlue()) {
						color = Constants.COLOR_KEY_OWN_COLOR_2;
					}
				}
				arGfx[y][x] = color;
			}
		}
	}

	public void loadFile(File file) {
		try {
			BufferedImage image = ImageIO.read(file);

			for (int y = 0; y < getHeight(); y++) {
				for (int x = 0; x < getWidth(); x++) {
					arGfx[y][x] = new Color(image.getRGB(x, y), true);
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public AnimTilePhaseType getType() {
		return type;
	}

	public String getGroupName() {
		return this.groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Direction getDirection() {
		return this.direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public Activity getActivityType() {
		return this.activityType;
	}

	public int getPhaseNumber() {
		return this.phaseNumber;
	}

	public void copyContentsFrom(AnimTilePhase copyHead) {
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				arGfx[y][x] = copyHead.arGfx[y][x];
			}
		}
	}

	public void mirror() {
		Color[][] arNewGfx = new Color[getHeight()][getWidth()];
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				arNewGfx[y][x] = arGfx[y][getWidth() - x - 1];
			}
		}
		arGfx = arNewGfx;
	}

	public boolean isUpperBorder(int x, int y) {
		return y == 0 || arGfx[y - 1][x].getAlpha() == 0;
	}

	public boolean isBottomBorder(int x, int y) {
		return y == getHeight() - 1 || arGfx[y + 1][x].getAlpha() == 0;
	}

	public boolean isLeftBorder(int x, int y) {
		return x == 0 || arGfx[y][x - 1].getAlpha() == 0;
	}

	public boolean isRightBorder(int x, int y) {
		return x == getWidth() - 1 || arGfx[y][x + 1].getAlpha() == 0;
	}
}
