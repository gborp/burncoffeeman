package com.braids.burncoffeeman.client;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
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

	private static int                                actualSize    = -1;
	private static HashMap<PlayerSlot, BufferedImage> mapPlayer     = new HashMap<PlayerSlot, BufferedImage>();
	private static HashMap<Wall, Image>               mapWall       = new HashMap<Wall, Image>();
	private static HashMap<FireSlot, List<Image>>     mapFire       = new HashMap<FireSlot, List<Image>>();

	private static ColorFilter                        colorFilter   = new ColorFilter();

	private static final Color                        fireKeyColor1 = new Color(255, 0, 255);
	private static final Color                        fireKeyColor2 = new Color(0, 0, 0);

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

	public static Image getFire(int size, Fire fire, Color ownColor1, Color ownColor2, int phase) {
		if (actualSize != size) {
			actualSize = size;
			clearCache();
		}

		FireSlot slot = new FireSlot();
		slot.fire = fire;
		slot.ownColor1 = ownColor1;
		slot.ownColor2 = ownColor2;

		List<Image> result = mapFire.get(slot);

		if (result == null) {
			GraphicsTemplateManager gtm = GraphicsTemplateManager.getInstance();

			colorFilter.fromColor1 = fireKeyColor1.getRGB();
			colorFilter.toColor1 = ownColor1.getRGB();

			colorFilter.fromColor2 = fireKeyColor2.getRGB();
			colorFilter.toColor2 = ownColor2.getRGB();

			result = new ArrayList<Image>();
			for (BufferedImage origFire : gtm.getFire(fire)) {
				ImageProducer ip = new FilteredImageSource(origFire.getSource(), colorFilter);
				Image coloredImage = Toolkit.getDefaultToolkit().createImage(ip);
				result.add(coloredImage.getScaledInstance(size + 1, size + 1, Image.SCALE_REPLICATE));
			}
			mapFire.put(slot, result);
		}

		return result.get(phase);
	}

	private static class ColorFilter extends RGBImageFilter {

		public int fromColor1;
		public int toColor1;
		public int fromColor2;
		public int toColor2;

		public int filterRGB(int x, int y, int rgb) {
			if (rgb == fromColor1) {
				return toColor1;
			} else if (rgb == fromColor2) {
				return toColor2;
			}
			return rgb;
		}

	}

	public static BufferedImage getPlayer(int size, Color ownColor1, Color ownColor2, String headGroupName, String bodyGroupName, String legsGroupName,
	        Activity activityType, Direction direction, int phaseNumber) {

		if (actualSize != size) {
			actualSize = size;
			clearCache();
		}

		PlayerSlot slot = new PlayerSlot();
		slot.ownColor1 = ownColor1;
		slot.ownColor2 = ownColor2;
		slot.headGroupName = headGroupName;
		slot.bodyGroupName = bodyGroupName;
		slot.legsGroupName = legsGroupName;
		slot.activityType = activityType;
		slot.direction = direction;
		slot.phaseNumber = phaseNumber;

		BufferedImage result = mapPlayer.get(slot);

		if (result == null) {
			GraphicsTemplateManager gtm = GraphicsTemplateManager.getInstance();

			AnimTilePhase head = gtm.getAnimPhase(headGroupName, AnimTilePhaseType.HEAD, activityType, direction, phaseNumber);
			AnimTilePhase body = gtm.getAnimPhase(bodyGroupName, AnimTilePhaseType.BODY, activityType, direction, phaseNumber);
			AnimTilePhase legs = gtm.getAnimPhase(legsGroupName, AnimTilePhaseType.LEGS, activityType, direction, phaseNumber);

			result = new BufferedImage(size + 1, size + 1, BufferedImage.TYPE_INT_ARGB);
			GfxHelper.paintBombermanAnimPhase(result.getGraphics(), size / 16f, phaseNumber, slot.ownColor1, slot.ownColor2, head, body, legs);
			mapPlayer.put(slot, result);
		}
		return result;
	}

	private static class PlayerSlot {

		Color     ownColor1;
		Color     ownColor2;
		String    headGroupName;
		String    bodyGroupName;
		String    legsGroupName;
		Activity  activityType;
		Direction direction;
		int       phaseNumber;

		public boolean equals(Object obj) {
			PlayerSlot other = (PlayerSlot) obj;
			return ownColor1.equals(other.ownColor1) && ownColor2.equals(other.ownColor2) && headGroupName.equals(other.headGroupName)
			        && bodyGroupName.equals(other.bodyGroupName) && legsGroupName.equals(other.legsGroupName) && activityType.equals(other.activityType)
			        && direction.equals(other.direction) && phaseNumber == other.phaseNumber;
		}

		public int hashCode() {
			return ownColor1.hashCode() ^ ownColor2.hashCode() ^ headGroupName.hashCode() ^ bodyGroupName.hashCode() ^ legsGroupName.hashCode()
			        ^ activityType.hashCode() ^ direction.hashCode() ^ (phaseNumber + 1);
		}

	}

	private static class FireSlot {

		Color ownColor1;
		Color ownColor2;
		Fire  fire;

		public boolean equals(Object obj) {
			FireSlot other = (FireSlot) obj;
			return ownColor1.equals(other.ownColor1) && ownColor2.equals(other.ownColor2) && fire.equals(other.fire);
		}

		public int hashCode() {
			return ownColor1.hashCode() ^ ownColor2.hashCode() ^ fire.hashCode();
		}
	}
}
