package com.braids.burncoffeeman.editor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import com.braids.burncoffeeman.common.Activity;
import com.braids.burncoffeeman.common.Constants;
import com.braids.burncoffeeman.common.Direction;

public class EditorManager {

	private static EditorManager singleton;
	private List<String>         lstGroupsHead;
	private List<String>         lstGroupsBody;
	private List<String>         lstGroupsLegs;

	private List<AnimTilePhase>  lstAnimTilePhaseHead;
	private List<AnimTilePhase>  lstAnimTilePhaseBody;
	private List<AnimTilePhase>  lstAnimTilePhaseLegs;

	private AnimTilePhase        currentHead;
	private AnimTilePhase        currentBody;
	private AnimTilePhase        currentLeg;
	private AnimTilePhase        copyHead;
	private AnimTilePhase        copyBody;
	private AnimTilePhase        copyLeg;

	public static void init() {
		singleton = new EditorManager();
		singleton.doInit();
	}

	public static EditorManager getInstance() {
		return singleton;
	}

	public EditorManager() {}

	private void doInit() {
		lstAnimTilePhaseHead = new ArrayList<AnimTilePhase>();
		lstAnimTilePhaseBody = new ArrayList<AnimTilePhase>();
		lstAnimTilePhaseLegs = new ArrayList<AnimTilePhase>();
		lstGroupsHead = new ArrayList<String>();
		lstGroupsBody = new ArrayList<String>();
		lstGroupsLegs = new ArrayList<String>();

		lstGroupsHead.add("default");
		lstGroupsBody.add("default");
		lstGroupsLegs.add("default");

		loadAnims(new File("gfx"));
	}

	private void loadAnim(File file) {
		String name = file.getName();
		name = name.substring(0, name.length() - ".png".length());
		String[] nameSegments = name.split("\\_");
		String groupName = nameSegments[0];
		AnimTilePhaseType phaseType = AnimTilePhaseType.valueOf(nameSegments[1]);
		try {
			BufferedImage bi = ImageIO.read(file);

			int phaseHeight = phaseType.getHeight();
			int y = 0;
			for (Activity activityType : Activity.values()) {
				if (activityType.hasOwnGfx) {
					for (Direction direction : Direction.values()) {
						for (int phase = 1; phase <= activityType.getIterations(); phase++) {
							AnimTilePhase tile = getCreateAnimTilePhase(groupName, phaseType, activityType, direction, phase);
							tile.loadFromBitmap(bi, (phase - 1) * 16, y);
						}
						y += phaseHeight;
					}
				}
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void loadAnims(File dir) {

		for (File file : dir.listFiles()) {
			if (file.isFile() && !file.isHidden() && file.getName().endsWith(".png")) {
				loadAnim(file);
			}
		}

		Collections.sort(lstAnimTilePhaseHead, new AnimTilePhaseComparator());
		Collections.sort(lstAnimTilePhaseBody, new AnimTilePhaseComparator());
		Collections.sort(lstAnimTilePhaseLegs, new AnimTilePhaseComparator());

		currentHead = getCreateAnimTilePhase("default", AnimTilePhaseType.HEAD, Activity.STANDING, Direction.LEFT, 1);
		currentBody = getCreateAnimTilePhase("default", AnimTilePhaseType.BODY, Activity.STANDING, Direction.LEFT, 1);
		currentLeg = getCreateAnimTilePhase("default", AnimTilePhaseType.LEGS, Activity.STANDING, Direction.LEFT, 1);
	}

	public void saveAnims() {
		saveAnimGroup(new File("gfx"));
	}

	private void saveAnimGroup(File dir) {
		Collections.sort(lstAnimTilePhaseHead, new AnimTilePhaseComparator());
		Collections.sort(lstAnimTilePhaseBody, new AnimTilePhaseComparator());
		Collections.sort(lstAnimTilePhaseLegs, new AnimTilePhaseComparator());

		saveType(AnimTilePhaseType.HEAD, dir);
		saveType(AnimTilePhaseType.BODY, dir);
		saveType(AnimTilePhaseType.LEGS, dir);
	}

	private void saveType(AnimTilePhaseType phaseType, File dir) {
		int phaseHeight = phaseType.getHeight();
		for (String groupName : lstGroupsHead) {

			BufferedImage bi = new BufferedImage(16 * Constants.MAX_ANIM_PHASE_COUNT, phaseHeight * Direction.values().length * Activity.getNumberOfOwnGfx(),
			        BufferedImage.TYPE_INT_ARGB);
			int y = 0;
			for (Activity activityType : Activity.values()) {
				if (activityType.hasOwnGfx) {
					for (Direction direction : Direction.values()) {
						for (int phase = 1; phase <= activityType.getIterations(); phase++) {
							AnimTilePhase tile = getCreateAnimTilePhase(groupName, phaseType, activityType, direction, phase);
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

	public AnimTilePhase getCreateAnimTilePhase(String groupName, AnimTilePhaseType type, Activity activityType, Direction direction, int phaseNumber) {
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

	public AnimTilePhase getCurrentHead() {
		return this.currentHead;
	}

	public void setCurrentHead(AnimTilePhase currentHead) {
		this.currentHead = currentHead;
	}

	public AnimTilePhase getCurrentBody() {
		return this.currentBody;
	}

	public void setCurrentBody(AnimTilePhase currentBody) {
		this.currentBody = currentBody;
	}

	public AnimTilePhase getCurrentLeg() {
		return this.currentLeg;
	}

	public void setCurrentLeg(AnimTilePhase currentLeg) {
		this.currentLeg = currentLeg;
	}

	public void copyHead() {
		copyHead = currentHead;
	}

	public void pasteHead() {
		currentHead.copyContentsFrom(copyHead);
	}

	public void copyBody() {
		copyBody = currentBody;
	}

	public void pasteBody() {
		currentBody.copyContentsFrom(copyBody);
	}

	public void copyLeg() {
		copyLeg = currentLeg;
	}

	public void pasteLeg() {
		currentLeg.copyContentsFrom(copyLeg);
	}

	public void mirrorHead() {
		currentHead.mirror();
	}

	public void mirrorBody() {
		currentBody.mirror();
	}

	public void mirrorLeg() {
		currentLeg.mirror();
	}

}
