package com.braids.burncoffeeman.editor;

import java.io.File;
import java.util.List;

import com.braids.burncoffeeman.common.Activity;
import com.braids.burncoffeeman.common.AnimTilePhase;
import com.braids.burncoffeeman.common.AnimTilePhaseType;
import com.braids.burncoffeeman.common.Direction;
import com.braids.burncoffeeman.common.GraphicsTemplateManager;

public class EditorManager {

	private static EditorManager    singleton;

	private GraphicsTemplateManager gtm;

	private AnimTilePhase           currentHead;
	private AnimTilePhase           currentBody;
	private AnimTilePhase           currentLeg;
	private AnimTilePhase           copyHead;
	private AnimTilePhase           copyBody;
	private AnimTilePhase           copyLeg;

	public static void init() {
		singleton = new EditorManager();
		singleton.doInit();
	}

	public static EditorManager getInstance() {
		return singleton;
	}

	public EditorManager() {}

	private void doInit() {
		gtm = GraphicsTemplateManager.init();
		gtm.loadAnims(new File("gfx"));
		currentHead = getCreateAnimTilePhase("default", AnimTilePhaseType.HEAD, Activity.STANDING, Direction.LEFT, 1);
		currentBody = getCreateAnimTilePhase("default", AnimTilePhaseType.BODY, Activity.STANDING, Direction.LEFT, 1);
		currentLeg = getCreateAnimTilePhase("default", AnimTilePhaseType.LEGS, Activity.STANDING, Direction.LEFT, 1);
	}

	public AnimTilePhase getCreateAnimTilePhase(String groupName, AnimTilePhaseType type, Activity activityType, Direction direction, int phaseNumber) {
		return gtm.getAnimPhase(groupName, type, activityType, direction, phaseNumber);
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

	public List<String> getGroupListForHead() {
		return gtm.getGroupListForHead();
	}

	public List<String> getGroupListForBody() {
		return gtm.getGroupListForBody();
	}

	public List<String> getGroupListForLegs() {
		return gtm.getGroupListForLegs();
	}

	public void saveAll() {
		gtm.saveAll();
	}

}
