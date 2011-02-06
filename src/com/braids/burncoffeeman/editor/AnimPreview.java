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

	private int              zoomLevel;
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

		g.translate(16 * zoomLevel / 2, 16 * zoomLevel / 2);

		int yOffset = 0;
		AnimTilePhase currentHead = EditorManager.getInstance().getCurrentHead();
		currentHead = EditorManager.getInstance().getCreateAnimTilePhase(currentHead.getGroupName(), currentHead.getType(), currentHead.getActivityType(),
		        currentHead.getDirection(), phaseCount);

		paintTile(g, currentHead, yOffset);
		yOffset += currentHead.getHeight() * zoomLevel;

		AnimTilePhase currentBody = EditorManager.getInstance().getCurrentBody();
		currentBody = EditorManager.getInstance().getCreateAnimTilePhase(currentBody.getGroupName(), currentBody.getType(), currentBody.getActivityType(),
		        currentBody.getDirection(), phaseCount);

		paintTile(g, currentBody, yOffset);
		yOffset += currentBody.getHeight() * zoomLevel;

		AnimTilePhase currentLeg = EditorManager.getInstance().getCurrentLeg();
		currentLeg = EditorManager.getInstance().getCreateAnimTilePhase(currentLeg.getGroupName(), currentLeg.getType(), currentLeg.getActivityType(),
		        currentLeg.getDirection(), phaseCount);

		paintTile(g, currentLeg, yOffset);
		yOffset += currentLeg.getHeight() * zoomLevel;
	}

	private void paintTile(Graphics g, AnimTilePhase tile, int yOffset) {

		for (int y = 0; y < tile.getHeight(); y++) {
			for (int x = 0; x < tile.getWidth(); x++) {
				Color color = tile.getPixel(x, y);
				if (color.equals(Constants.COLOR_KEY_OWN_COLOR_1)) {
					color = ownColor1;
				} else if (color.equals(Constants.COLOR_KEY_OWN_COLOR_2)) {
					color = ownColor2;
				}
				g.setColor(color);
				g.fillRect((int) (x * zoomLevel), (int) (y * zoomLevel + yOffset), (int) zoomLevel, (int) zoomLevel);
			}
		}

	}
}
