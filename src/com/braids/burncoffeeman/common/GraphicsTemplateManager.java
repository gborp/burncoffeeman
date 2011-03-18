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

	private static GraphicsTemplateManager     singleton;

	private List<String>                       lstGroupsHead;
	private List<String>                       lstGroupsBody;
	private List<String>                       lstGroupsLegs;

	private List<AnimTilePhase>                lstAnimTilePhaseHead;
	private List<AnimTilePhase>                lstAnimTilePhaseBody;
	private List<AnimTilePhase>                lstAnimTilePhaseLegs;

	private HashMap<AnimOriginalSlot, byte[]>  mapOriginalImages;

	private HashMap<Wall, BufferedImage>       mapWallTiles;
	private HashMap<Fire, List<BufferedImage>> mapFire;

	private List<BufferedImage>                lstBombStandingAnim;
	private List<BufferedImage>                lstBombMovingAnim;
	private List<BufferedImage>                lstBombTimerAnim;

	private BufferedImage                      dummyImage;

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
		mapFire = new HashMap<Fire, List<BufferedImage>>();
		dummyImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

		lstBombStandingAnim = new ArrayList<BufferedImage>();
		lstBombMovingAnim = new ArrayList<BufferedImage>();
		lstBombTimerAnim = new ArrayList<BufferedImage>();
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

		int num = 0;

		int width;
		int height;

		if (hasSeparator) {
			width = (img.getWidth() - columns + 1) / columns;
			height = (img.getHeight() - rows + 1) / rows;
		} else {
			width = img.getWidth() / columns;
			height = img.getHeight() / rows;
		}

		BufferedImage result[] = new BufferedImage[width * height];
		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < columns; x++) {
				result[num] = new BufferedImage(width, height, img.getType());
				Graphics2D g = result[num].createGraphics();
				int xSeparatorOffset = 0;
				int ySeparatorOffset = 0;
				if (hasSeparator) {
					xSeparatorOffset = x;
					ySeparatorOffset = y;
				}

				int dx1 = width * x + xSeparatorOffset;
				int dy1 = height * y + ySeparatorOffset;
				int dx2 = dx1 + width;
				int dy2 = dy1 + height;

				g.drawImage(img, 0, 0, width, height, dx1, dy1, dx2, dy2, null);
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

	public void loadBombs(BufferedImage image) throws IOException {
		BufferedImage[] images = splitImage(image, 8, 3, true);

		for (int i = 0; i < 8; i++) {
			lstBombStandingAnim.add(images[i]);
			lstBombMovingAnim.add(images[i + 8]);
		}
		lstBombTimerAnim.add(images[16]);
	}

	public void loadFires(BufferedImage image) throws IOException {
		BufferedImage[] images = splitImage(image, 5, 3, true);

		int offset = 0;
		offset += loadFire(Fire.HORIZONTAL, images, offset);
		offset += loadFire(Fire.VERTICAL, images, offset);
		offset += loadFire(Fire.CROSSING, images, offset);
	}

	private int loadFire(Fire fire, BufferedImage[] images, int offset) {
		ArrayList<BufferedImage> lstPhases = new ArrayList<BufferedImage>(5);
		for (int i = 0; i < 5; i++) {
			lstPhases.add(images[i + offset]);
		}
		mapFire.put(fire, lstPhases);
		return lstPhases.size();
	}

	public BufferedImage getWall(Wall wall) {
		BufferedImage result = mapWallTiles.get(wall);

		if (result == null) {
			result = dummyImage;
		}

		return result;
	}

	public List<BufferedImage> getFire(Fire fire) {
		List<BufferedImage> result = mapFire.get(fire);

		if (result == null) {
			// prevent initial npe errors
			result = new ArrayList<BufferedImage>(20);
			for (int i = 0; i < 20; i++) {
				result.add(dummyImage);
			}
		}

		return result;
	}

	public List<BufferedImage> getBomb(BombPhases bombPhase, BombType type) {
		if (bombPhase == BombPhases.ROLLING || bombPhase == BombPhases.FLYING) {
			return lstBombMovingAnim;
		}

		if (type == BombType.TIMER) {
			return lstBombTimerAnim;
		}

		return lstBombStandingAnim;
	}
}
