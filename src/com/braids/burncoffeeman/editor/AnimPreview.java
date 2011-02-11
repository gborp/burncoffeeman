package com.braids.burncoffeeman.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

import com.braids.burncoffeeman.common.Constants;

public class AnimPreview extends JPanel {

	private static final int ANIM_SPEED            = 500;
	private static final int COLOR_CHANGE_INTERVAL = 8;
	private static final int MAX_PHASE_COUNT       = 4;

	private float            zoomLevel;
	private Color            ownColor1;
	private Color            ownColor2;
	private Color            grassColor1;
	private Color            grassColor2;
	private int              nextColorChangeCountDown;
	private int              phaseCount;

	public AnimPreview() {
		phaseCount = 1;
		zoomLevel = 4;
		ownColor1 = Color.RED;
		ownColor2 = Color.GRAY;

		grassColor1 = new Color(0, 48, 0);
		grassColor2 = new Color(0, 32, 0);

		new Timer(ANIM_SPEED, new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				iterateAnim();
			}
		}).start();
	}

	private void iterateAnim() {
		nextColorChangeCountDown--;
		if (nextColorChangeCountDown < 0) {
			nextColorChangeCountDown = COLOR_CHANGE_INTERVAL;
			ownColor1 = new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
			ownColor2 = new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
		}
		phaseCount++;
		if (phaseCount > MAX_PHASE_COUNT) {
			phaseCount = 1;
		}

		repaint();
	}

	public Dimension getPreferredSize() {
		return new Dimension((int) (16 * zoomLevel * 2), (int) (16 * zoomLevel * 2));
	}

	public Dimension getSize() {
		return getPreferredSize();
	}

	public void paint(Graphics g) {

		for (int y = 0; y < 16 * 2 * 2; y++) {
			for (int x = 0; x < 16 * 2 * 2; x++) {
				if ((x + y) % 2 == 0) {
					g.setColor(grassColor1);
				} else {
					g.setColor(grassColor2);
				}
				g.fillRect((int) (x * zoomLevel), (int) (y * zoomLevel), (int) (zoomLevel), (int) (zoomLevel));
			}
		}

		g.translate((int) (16 * zoomLevel / 2), (int) (16 * zoomLevel / 2));

		Color[][] arGfxWhole = new Color[16][16];

		AnimTilePhase currentHead = EditorManager.getInstance().getCurrentHead();
		currentHead = EditorManager.getInstance().getCreateAnimTilePhase(currentHead.getGroupName(), currentHead.getType(), currentHead.getActivityType(),
		        currentHead.getDirection(), phaseCount);
		AnimTilePhase currentBody = EditorManager.getInstance().getCurrentBody();
		currentBody = EditorManager.getInstance().getCreateAnimTilePhase(currentBody.getGroupName(), currentBody.getType(), currentBody.getActivityType(),
		        currentBody.getDirection(), phaseCount);
		AnimTilePhase currentLeg = EditorManager.getInstance().getCurrentLeg();
		currentLeg = EditorManager.getInstance().getCreateAnimTilePhase(currentLeg.getGroupName(), currentLeg.getType(), currentLeg.getActivityType(),
		        currentLeg.getDirection(), phaseCount);

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
		for (int y = 0; y < currentLeg.getHeight(); y++) {
			for (int x = 0; x < 16; x++) {
				arGfxWhole[yOffset][x] = currentLeg.getPixel(x, y);
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
				g.fillRect((int) (x * zoomLevel), (int) (y * zoomLevel + yOffset), (int) zoomLevel, (int) zoomLevel);

				if (color.getAlpha() > 0) {
					g.setColor(Color.BLACK);
					if (isUpperBorder(x, y, arGfxWhole)) {
						g.drawLine((int) (x * zoomLevel), (int) (y * zoomLevel + yOffset), (int) (x * zoomLevel + zoomLevel), (int) (y * zoomLevel + yOffset));
					}
					if (isBottomBorder(x, y, arGfxWhole)) {
						g.drawLine((int) (x * zoomLevel), (int) (y * zoomLevel + yOffset + zoomLevel), (int) (x * zoomLevel + zoomLevel), (int) (y * zoomLevel
						        + yOffset + zoomLevel));
					}
					if (isLeftBorder(x, y, arGfxWhole)) {
						g.drawLine((int) (x * zoomLevel), (int) (y * zoomLevel + yOffset), (int) (x * zoomLevel), (int) (y * zoomLevel + yOffset + zoomLevel));
					}
					if (isRightBorder(x, y, arGfxWhole)) {
						g.drawLine((int) (x * zoomLevel + zoomLevel), (int) (y * zoomLevel + yOffset), (int) (x * zoomLevel + zoomLevel), (int) (y * zoomLevel
						        + yOffset + zoomLevel));
					}
				}
			}
		}
	}

	private boolean isUpperBorder(int x, int y, Color[][] arGfxWhole) {
		return y == 0 || arGfxWhole[y - 1][x].getAlpha() == 0;
	}

	private boolean isBottomBorder(int x, int y, Color[][] arGfxWhole) {
		return y == 16 - 1 || arGfxWhole[y + 1][x].getAlpha() == 0;
	}

	private boolean isLeftBorder(int x, int y, Color[][] arGfxWhole) {
		return x == 0 || arGfxWhole[y][x - 1].getAlpha() == 0;
	}

	private boolean isRightBorder(int x, int y, Color[][] arGfxWhole) {
		return x == 16 - 1 || arGfxWhole[y][x + 1].getAlpha() == 0;
	}

}
