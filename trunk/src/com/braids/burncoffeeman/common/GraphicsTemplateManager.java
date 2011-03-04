package com.braids.burncoffeeman.common;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

public class GraphicsTemplateManager {

	private static GraphicsTemplateManager    singleton;

	private List<String>                      lstGroupsHead;
	private List<String>                      lstGroupsBody;
	private List<String>                      lstGroupsLegs;

	private List<AnimTilePhase>               lstAnimTilePhaseHead;
	private List<AnimTilePhase>               lstAnimTilePhaseBody;
	private List<AnimTilePhase>               lstAnimTilePhaseLegs;

	private HashMap<AnimOriginalSlot, byte[]> mapOriginalImages;

	private HashMap<Wall, BufferedImage>      mapWallTiles;

	private static class AnimOriginalSlot {

		String            groupName;
		AnimTilePhaseType phaseType;

		public boolean equals(Object obj) {
			AnimOriginalSlot other = (AnimOriginalSlot) obj;
			return groupName.equals(other.groupName) && phaseType == other.phaseType;
		}

		public int hashCode() {
			return groupName.hashCode() ^ phaseType.hashCode();
		}
	}

	public static GraphicsTemplateManager init() {
		singleton = new GraphicsTemplateManager();
		singleton.doInit();
		return singleton;
	}

	public static GraphicsTemplateManager getInstance() {
		return singleton;
	}

	private void doInit() {
		lstAnimTilePhaseHead = new ArrayList<AnimTilePhase>();
		lstAnimTilePhaseBody = new ArrayList<AnimTilePhase>();
		lstAnimTilePhaseLegs = new ArrayList<AnimTilePhase>();
		lstGroupsHead = new ArrayList<String>();
		lstGroupsBody = new ArrayList<String>();
		lstGroupsLegs = new ArrayList<String>();
		mapOriginalImages = new HashMap<AnimOriginalSlot, byte[]>();

		mapWallTiles = new HashMap<Wall, BufferedImage>();
	}

	public void loadAnimOriginals(File dir) {
		for (File file : dir.listFiles()) {
			if (file.isFile() && !file.isHidden() && file.getName().endsWith(".png") && file.getName().contains("_")) {
				try {
					String name = file.getName();
					name = name.substring(0, name.length() - ".png".length());
					String[] nameSegments = name.split("\\_");
					String groupName = nameSegments[0];
					AnimTilePhaseType phaseType = AnimTilePhaseType.valueOf(nameSegments[1]);

					AnimOriginalSlot slot = new AnimOriginalSlot();
					slot.groupName = groupName;
					slot.phaseType = phaseType;

					mapOriginalImages.put(slot, Helper.getFileAsByteArray(file));

				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public byte[] getOriginalImage(String groupName, AnimTilePhaseType phaseType) {
		AnimOriginalSlot slot = new AnimOriginalSlot();
		slot.groupName = groupName;
		slot.phaseType = phaseType;

		byte[] result = mapOriginalImages.get(slot);
		if (result == null) {
			System.out.println("OMG! " + groupName + " " + phaseType);
		}

		return result;
	}

	public void loadAnims(File dir) {

		for (File file : dir.listFiles()) {
			if (file.isFile() && !file.isHidden() && file.getName().endsWith(".png") && file.getName().contains("_")) {
				try {
					BufferedImage image = ImageIO.read(file);

					String name = file.getName();
					name = name.substring(0, name.length() - ".png".length());
					String[] nameSegments = name.split("\\_");
					String groupName = nameSegments[0];
					AnimTilePhaseType phaseType = AnimTilePhaseType.valueOf(nameSegments[1]);

					loadAnim(image, groupName, phaseType);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}

		Collections.sort(lstAnimTilePhaseHead, new AnimTilePhaseComparator());
		Collections.sort(lstAnimTilePhaseBody, new AnimTilePhaseComparator());
		Collections.sort(lstAnimTilePhaseLegs, new AnimTilePhaseComparator());

		Collections.sort(lstGroupsHead);
		Collections.sort(lstGroupsBody);
		Collections.sort(lstGroupsLegs);
	}

	public void loadAnim(BufferedImage image, String groupName, AnimTilePhaseType phaseType) {

		switch (phaseType) {
			case HEAD:
				if (!lstGroupsHead.contains(groupName)) {
					lstGroupsHead.add(groupName);
				}
				break;
			case BODY:
				if (!lstGroupsBody.contains(groupName)) {
					lstGroupsBody.add(groupName);
				}
				break;
			case LEGS:
				if (!lstGroupsLegs.contains(groupName)) {
					lstGroupsLegs.add(groupName);
				}
				break;
		}

		int phaseHeight = phaseType.getHeight();
		int y = 0;
		for (Activity activityType : Activity.values()) {
			if (activityType.hasOwnGfx) {
				for (Direction direction : Direction.values()) {
					for (int phase = 1; phase <= activityType.getIterations(); phase++) {
						AnimTilePhase tile = getAnimPhase(groupName, phaseType, activityType, direction, phase);
						tile.loadFromBitmap(image, (phase - 1) * 16, y);
					}
					y += phaseHeight;
				}
			}
		}

	}

	public void saveAnims() {
		saveAnimGroup(new File("gfx"));
	}

	private void saveAnimGroup(File dir) {
		Collections.sort(lstAnimTilePhaseHead, new AnimTilePhaseComparator());
		Collections.sort(lstAnimTilePhaseBody, new AnimTilePhaseComparator());
		Collections.sort(lstAnimTilePhaseLegs, new AnimTilePhaseComparator());

		saveType(lstGroupsHead, AnimTilePhaseType.HEAD, dir);
		saveType(lstGroupsBody, AnimTilePhaseType.BODY, dir);
		saveType(lstGroupsLegs, AnimTilePhaseType.LEGS, dir);
	}

	private void saveType(List<String> lstGroup, AnimTilePhaseType phaseType, File dir) {
		int phaseHeight = phaseType.getHeight();
		for (String groupName : lstGroup) {

			BufferedImage bi = new BufferedImage(16 * Constants.MAX_ANIM_PHASE_COUNT, phaseHeight * Direction.values().length * Activity.getNumberOfOwnGfx(),
			        BufferedImage.TYPE_INT_ARGB);
			int y = 0;
			for (Activity activityType : Activity.values()) {
				if (activityType.hasOwnGfx()) {
					for (Direction direction : Direction.values()) {
						for (int phase = 1; phase <= activityType.getIterations(); phase++) {
							AnimTilePhase tile = getAnimPhase(groupName, phaseType, activityType, direction, phase);
							tile.saveToBitmap(bi, (phase - 1) * 16, y);
						}
						y += phaseHeight;
					}
				}
			}

			File file = new File(dir, groupName + "_" + phaseType.toString() + ".png");
			try {
				ImageIO.write(bi, "png", file);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void addNewAnimTilePhase(AnimTilePhase newPhase) {
		switch (newPhase.getType()) {
			case HEAD:
				lstAnimTilePhaseHead.add(newPhase);
				break;
			case BODY:
				lstAnimTilePhaseBody.add(newPhase);
				break;
			case LEGS:
				lstAnimTilePhaseLegs.add(newPhase);
				break;
		}

	}

	public void saveAll() {
		saveAnims();
	}

	public AnimTilePhase getAnimTilePhase(String groupName, AnimTilePhaseType type, Activity activityType, Direction direction, int phaseNumber) {
		List<AnimTilePhase> lstAnimTilePhase = null;
		switch (type) {
			case HEAD:
				lstAnimTilePhase = lstAnimTilePhaseHead;
				break;
			case BODY:
				lstAnimTilePhase = lstAnimTilePhaseBody;
				break;
			case LEGS:
				lstAnimTilePhase = lstAnimTilePhaseLegs;
				break;
		}

		for (AnimTilePhase li : lstAnimTilePhase) {
			if (li.getGroupName().equals(groupName) && li.getType().equals(type) && li.getActivityType().equals(activityType)
			        && li.getDirection().equals(direction) && li.getPhaseNumber() == phaseNumber) {
				return li;
			}
		}
		return null;
	}

	public AnimTilePhase getAnimPhase(String groupName, AnimTilePhaseType type, Activity activityType, Direction direction, int phaseNumber) {
		if (phaseNumber < 1) {
			phaseNumber = 1;
		}
		phaseNumber--;
		phaseNumber = phaseNumber % activityType.getIterations();
		phaseNumber++;
		AnimTilePhase tile = getAnimTilePhase(groupName, type, activityType, direction, phaseNumber);
		if (tile == null) {
			tile = new AnimTilePhase(groupName, type, activityType, direction, phaseNumber);
			switch (type) {
				case HEAD:
					lstAnimTilePhaseHead.add(tile);
					break;
				case BODY:
					lstAnimTilePhaseBody.add(tile);
					break;
				case LEGS:
					lstAnimTilePhaseLegs.add(tile);
					break;
			}
		}
		return tile;
	}

	public List<String> getGroupListForHead() {
		return lstGroupsHead;
	}

	public List<String> getGroupListForBody() {
		return lstGroupsBody;
	}

	public List<String> getGroupListForLegs() {
		return lstGroupsLegs;
	}

	private static BufferedImage[] splitImage(BufferedImage img, int columns, int rows, boolean hasSeparator) {
		int width = img.getWidth() / columns;
		int height = img.getHeight() / rows;
		int num = 0;
		int correction = hasSeparator ? 1 : 0;

		BufferedImage result[] = new BufferedImage[width * height];
		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < columns; x++) {
				result[num] = new BufferedImage(width, height, img.getType());
				Graphics2D g = result[num].createGraphics();
				g.drawImage(img, 0, 0, width, height, width * x - correction, height * y - correction, width * x + width, height * y + height, null);
				g.dispose();
				num++;
			}
		}
		return result;
	}

	public void loadWalls(BufferedImage image) throws IOException {
		BufferedImage[] images = splitImage(image, 1, 7, true);

		int i = 0;
		for (Wall w : Wall.values()) {
			mapWallTiles.put(w, images[i]);
			i++;
		}
	}

	public BufferedImage getWall(Wall wall) {
		return mapWallTiles.get(wall);
	}

	private void loadFire() throws IOException {
		BufferedImage[] images = splitImage(ImageIO.read(new File("gfx/tile-fire.png")), 5, 3, false);

	}
}
