package com.braids.burncoffeeman.common;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class GfxHelper {

	public static void paintBombermanAnimPhase(Graphics g, float zoomLevel, int phaseCount, Color ownColor1, Color ownColor2, AnimTilePhase currentHead,
	        AnimTilePhase currentBody, AnimTilePhase currentLegs) {

		Graphics2D g2d = (Graphics2D) g;

		g2d.scale(zoomLevel / 4, zoomLevel / 4);

		zoomLevel = 4;

		Color[][] arGfxWhole = new Color[16][16];

		GraphicsTemplateManager gtm = GraphicsTemplateManager.getInstance();
		currentHead = gtm
		        .getAnimPhase(currentHead.getGroupName(), currentHead.getType(), currentHead.getActivityType(), currentHead.getDirection(), phaseCount);
		currentBody = gtm
		        .getAnimPhase(currentBody.getGroupName(), currentBody.getType(), currentBody.getActivityType(), currentBody.getDirection(), phaseCount);
		currentLegs = gtm
		        .getAnimPhase(currentLegs.getGroupName(), currentLegs.getType(), currentLegs.getActivityType(), currentLegs.getDirection(), phaseCount);

		int yOffset = 0;
		for (int y = 0; y < currentHead.getHeight(); y++) {
			for (int x = 0; x < 16; x++) {
				arGfxWhole[yOffset][x] = currentHead.getPixel(x, y);
			}
			yOffset++;
		}
		for (int y = 0; y < currentBody.getHeight(); y++) {
			for (int x = 0; x < 16; x++) {
				arGfxWhole[yOffset][x] = currentBody.getPixel(x, y);
			}
			yOffset++;
		}
		for (int y = 0; y < currentLegs.getHeight(); y++) {
			for (int x = 0; x < 16; x++) {
				arGfxWhole[yOffset][x] = currentLegs.getPixel(x, y);
			}
			yOffset++;
		}

		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				Color color = arGfxWhole[y][x];
				if (color.equals(Constants.COLOR_KEY_OWN_COLOR_1)) {
					color = ownColor1;
				} else if (color.equals(Constants.COLOR_KEY_OWN_COLOR_2)) {
					color = ownColor2;
				}
				g.setColor(color);
				g.fillRect((int) (x * zoomLevel), (int) (y * zoomLevel), (int) zoomLevel, (int) zoomLevel);

				if (color.getAlpha() > 0) {
					g.setColor(Color.BLACK);
					if (isUpperBorder(x, y, arGfxWhole)) {
						g.drawLine((int) (x * zoomLevel), (int) (y * zoomLevel), (int) (x * zoomLevel + zoomLevel), (int) (y * zoomLevel));
					}
					if (isBottomBorder(x, y, arGfxWhole)) {
						g.drawLine((int) (x * zoomLevel), (int) (y * zoomLevel + zoomLevel), (int) (x * zoomLevel + zoomLevel),
						        (int) (y * zoomLevel + zoomLevel));
					}
					if (isLeftBorder(x, y, arGfxWhole)) {
						g.drawLine((int) (x * zoomLevel), (int) (y * zoomLevel), (int) (x * zoomLevel), (int) (y * zoomLevel + zoomLevel));
					}
					if (isRightBorder(x, y, arGfxWhole)) {
						g.drawLine((int) (x * zoomLevel + zoomLevel), (int) (y * zoomLevel), (int) (x * zoomLevel + zoomLevel),
						        (int) (y * zoomLevel + zoomLevel));
					}
				}
			}
		}
	}

	private static boolean isUpperBorder(int x, int y, Color[][] arGfxWhole) {
		return y == 0 || arGfxWhole[y - 1][x].getAlpha() == 0;
	}

	private static boolean isBottomBorder(int x, int y, Color[][] arGfxWhole) {
		return y == 16 - 1 || arGfxWhole[y + 1][x].getAlpha() == 0;
	}

	private static boolean isLeftBorder(int x, int y, Color[][] arGfxWhole) {
		return x == 0 || arGfxWhole[y][x - 1].getAlpha() == 0;
	}

	private static boolean isRightBorder(int x, int y, Color[][] arGfxWhole) {
		return x == 16 - 1 || arGfxWhole[y][x + 1].getAlpha() == 0;
	}
}
