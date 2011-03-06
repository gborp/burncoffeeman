package com.braids.burncoffeeman.client;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.braids.burncoffeeman.common.Activity;
import com.braids.burncoffeeman.common.AnimTilePhase;
import com.braids.burncoffeeman.common.AnimTilePhaseType;
import com.braids.burncoffeeman.common.Direction;
import com.braids.burncoffeeman.common.Fire;
import com.braids.burncoffeeman.common.GfxHelper;
import com.braids.burncoffeeman.common.GraphicsTemplateManager;
import com.braids.burncoffeeman.common.Wall;

public class ScaledGfxHelper {

	private static int                                actualSize = -1;
	private static HashMap<PlayerSlot, BufferedImage> mapPlayer  = new HashMap<PlayerSlot, BufferedImage>();
	private static HashMap<Wall, Image>               mapWall    = new HashMap<Wall, Image>();
	private static HashMap<Fire, List<Image>>         mapFire    = new HashMap<Fire, List<Image>>();

	private static void clearCache() {
		mapPlayer.clear();
		mapWall.clear();
		mapFire.clear();
	}

	public static Image getWall(int size, Wall wall) {
		if (actualSize != size) {
			actualSize = size;
			clearCache();
		}

		Image result = mapWall.get(wall);

		if (result == null) {
			GraphicsTemplateManager gtm = GraphicsTemplateManager.getInstance();
			result = gtm.getWall(wall).getScaledInstance(size + 1, size + 1, Image.SCALE_REPLICATE);

			mapWall.put(wall, result);
		}

		return result;
	}

	public static Image getFire(int size, Fire fire, int phase) {
		if (actualSize != size) {
			actualSize = size;
			clearCache();
		}

		List<Image> result = mapFire.get(fire);

		if (result == null) {
			GraphicsTemplateManager gtm = GraphicsTemplateManager.getInstance();
			result = new ArrayList<Image>();
			for (BufferedImage origFire : gtm.getFire(fire)) {
				result.add(origFire.getScaledInstance(size + 1, size + 1, Image.SCALE_REPLICATE));
			}
			mapFire.put(fire, result);
		}

		return result.get(phase);
	}

	public static BufferedImage getPlayer(int size, Color ownColor1, Color ownColor2, String groupName, Activity activityType, Direction direction,
	        int phaseNumber) {

		if (actualSize != size) {
			actualSize = size;
			clearCache();
		}

		PlayerSlot slot = new PlayerSlot();
		slot.ownColor1 = ownColor1;
		slot.ownColor2 = ownColor2;
		slot.groupName = groupName;
		slot.activityType = activityType;
		slot.direction = direction;
		slot.phaseNumber = phaseNumber;

		BufferedImage result = mapPlayer.get(slot);

		if (result == null) {
			GraphicsTemplateManager gtm = GraphicsTemplateManager.getInstance();

			AnimTilePhase head = gtm.getAnimPhase(groupName, AnimTilePhaseType.HEAD, activityType, direction, phaseNumber);
			AnimTilePhase body = gtm.getAnimPhase(groupName, AnimTilePhaseType.BODY, activityType, direction, phaseNumber);
			AnimTilePhase legs = gtm.getAnimPhase(groupName, AnimTilePhaseType.LEGS, activityType, direction, phaseNumber);

			result = new BufferedImage(size + 1, size + 1, BufferedImage.TYPE_INT_ARGB);
			GfxHelper.paintBombermanAnimPhase(result.getGraphics(), size / 16f, phaseNumber, Color.RED, Color.YELLOW, head, body, legs);
			mapPlayer.put(slot, result);
		}
		return result;
	}

	private static class PlayerSlot {

		Color     ownColor1;
		Color     ownColor2;
		String    groupName;
		Activity  activityType;
		Direction direction;
		int       phaseNumber;

		public boolean equals(Object obj) {
			PlayerSlot other = (PlayerSlot) obj;
			return ownColor1.equals(other.ownColor1) && ownColor2.equals(other.ownColor2) && groupName.equals(other.groupName)
			        && activityType.equals(other.activityType) && direction.equals(other.direction) && phaseNumber == other.phaseNumber;
		}

		public int hashCode() {
			return ownColor1.hashCode() ^ ownColor2.hashCode() ^ groupName.hashCode() ^ activityType.hashCode() ^ direction.hashCode() ^ (phaseNumber + 1);
		}

	}
}
