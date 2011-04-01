package com.braids.burncoffeeman.editor;

import java.io.File;
import java.util.List;

import com.braids.burncoffeeman.common.Activity;
import com.braids.burncoffeeman.common.AnimTilePhase;
import com.braids.burncoffeeman.common.BodyPart;
import com.braids.burncoffeeman.common.Direction;
import com.braids.burncoffeeman.common.GraphicsTemplateManager;

public class EditorManager {

	private static EditorManager    singleton;

	private GraphicsTemplateManager gtm;

	private AnimTilePhase           currentHead;
	private AnimTilePhase           currentBody;
	private AnimTilePhase           currentLegs;
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
		currentHead = getAnimTilePhase("default", BodyPart.HEAD, Activity.STANDING, Direction.LEFT, 1);
		currentBody = getAnimTilePhase("default", BodyPart.BODY, Activity.STANDING, Direction.LEFT, 1);
		currentLegs = getAnimTilePhase("default", BodyPart.LEGS, Activity.STANDING, Direction.LEFT, 1);
	}

	public AnimTilePhase getAnimTilePhase(String groupName, BodyPart type, Activity activityType, Direction direction, int phaseNumber) {
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

	public AnimTilePhase getCurrentLegs() {
		return this.currentLegs;
	}

	public void setCurrentLegs(AnimTilePhase currentLeg) {
		this.currentLegs = currentLeg;
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
		copyLeg = currentLegs;
	}

	public void pasteLeg() {
		currentLegs.copyContentsFrom(copyLeg);
	}

	public void mirrorHead() {
		currentHead.mirror();
	}

	public void mirrorBody() {
		currentBody.mirror();
	}

	public void mirrorLeg() {
		currentLegs.mirror();
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

	public void pasteCurrentHeadPhaseToAll() {
		for (Activity activity : Activity.getAnimateds()) {
			for (int i = 1; i <= activity.getIterations(); i++) {
				getAnimTilePhase(currentHead.getGroupName(), BodyPart.HEAD, activity, currentHead.getDirection(), i).copyContentsFrom(
				        currentHead);
			}
		}
	}

	public void pasteCurrentBodyPhaseToAll() {
		for (Activity activity : Activity.getAnimateds()) {
			for (int i = 1; i <= activity.getIterations(); i++) {
				getAnimTilePhase(currentBody.getGroupName(), BodyPart.BODY, activity, currentBody.getDirection(), i).copyContentsFrom(
				        currentBody);
			}
		}
	}

	public void pasteCurrentLegsPhaseToAll() {
		for (Activity activity : Activity.getAnimateds()) {
			for (int i = 1; i <= activity.getIterations(); i++) {
				getAnimTilePhase(currentLegs.getGroupName(), BodyPart.LEGS, activity, currentLegs.getDirection(), i).copyContentsFrom(
				        currentLegs);
			}
		}
	}

}
