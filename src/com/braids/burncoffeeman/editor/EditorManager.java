package com.braids.burncoffeeman.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EditorManager {

	private static EditorManager singleton;
	private List<AnimTilePhase>  lstAnimTilePhase;
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
		lstAnimTilePhase = new ArrayList<AnimTilePhase>();
		loadAnims(new File("gfx/default"));
	}

	private void loadAnims(File dir) {

		String groupName = dir.getName();
		for (File file : dir.listFiles()) {
			if (file.isFile() && !file.isHidden()) {
				String name = file.getName();
				if (name.endsWith(".png")) {
					name = name.substring(0, name.length() - ".png".length());
					String[] nameSegments = name.split("\\_");
					if (nameSegments.length == 4) {

						AnimTilePhaseType type = AnimTilePhaseType.valueOf(nameSegments[0]);
						AnimActivityType phaseType = AnimActivityType.valueOf(nameSegments[1]);
						AnimDirection direction = AnimDirection.valueOf(nameSegments[2]);
						Integer phase = Integer.valueOf(nameSegments[3]);

						AnimTilePhase li = new AnimTilePhase(groupName, type, phaseType, direction, phase);
						li.loadFile(file);
						lstAnimTilePhase.add(li);
					}
				}
			}
		}

		// try {
		// Properties propPhases = new Properties();
		// FileReader configReader = new FileReader(new File(dir,
		// "config.properties"));
		// propPhases.load(configReader);
		// configReader.close();
		//
		// HashSet<String> setIds = new HashSet<String>();
		// for (String key : propPhases.stringPropertyNames()) {
		// setIds.add(key.substring(0, key.lastIndexOf('_') + 1));
		// }
		//			
		// for (String id : setIds) {
		// AnimPhase li = new AnimPhase();
		// li.load(propPhases, id);
		// addNewAnimPhase(li);
		// }
		//
		// } catch (IOException ex) {
		// ex.printStackTrace();
		// }

		currentHead = getCreateAnimTilePhase("default", AnimTilePhaseType.HEAD, AnimActivityType.STANDING, AnimDirection.LEFT, 1);
		currentBody = getCreateAnimTilePhase("default", AnimTilePhaseType.BODY, AnimActivityType.STANDING, AnimDirection.LEFT, 1);
		currentLeg = getCreateAnimTilePhase("default", AnimTilePhaseType.LEGS, AnimActivityType.STANDING, AnimDirection.LEFT, 1);
	}

	public void saveAnims() {
		saveAnimGroup(new File("gfx/default"));
	}

	private void saveAnimGroup(File dir) {
		for (File file : dir.listFiles()) {
			if (file.isFile() && !file.isHidden()) {
				file.delete();
			}
		}

		for (AnimTilePhase tilePhase : lstAnimTilePhase) {
			File file = new File(dir, tilePhase.getType() + "_" + tilePhase.getActivityType() + "_" + tilePhase.getDirection() + "_"
			        + tilePhase.getPhaseNumber() + ".png");
			tilePhase.saveToFile(file);
		}

		// Properties propPhases = new Properties();
		//
		// try {
		// FileWriter configWriter = new FileWriter(new File(dir,
		// "config.properties"));
		// propPhases.store(configWriter, "");
		// configWriter.close();
		// } catch (IOException ex) {
		// ex.printStackTrace();
		// }
	}

	public void addNewAnimTilePhase(AnimTilePhase newPhase) {
		lstAnimTilePhase.add(newPhase);
	}

	public void saveAll() {
		saveAnims();
	}

	public AnimTilePhase getAnimTilePhase(String groupName, AnimTilePhaseType type, AnimActivityType activityType, AnimDirection direction, int phaseNumber) {
		for (AnimTilePhase li : lstAnimTilePhase) {
			if (li.getGroupName().equals(groupName) && li.getType().equals(type) && li.getActivityType().equals(activityType)
			        && li.getDirection().equals(direction) && li.getPhaseNumber() == phaseNumber) {
				return li;
			}
		}
		return null;
	}

	public AnimTilePhase getCreateAnimTilePhase(String groupName, AnimTilePhaseType type, AnimActivityType activityType, AnimDirection direction,
	        int phaseNumber) {
		AnimTilePhase tile = getAnimTilePhase(groupName, type, activityType, direction, phaseNumber);
		if (tile == null) {
			tile = new AnimTilePhase(groupName, type, activityType, direction, phaseNumber);
			lstAnimTilePhase.add(tile);
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
