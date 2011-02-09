package com.braids.burncoffeeman.editor;

import java.util.Comparator;

class AnimTilePhaseComparator implements Comparator<AnimTilePhase> {

	public int compare(AnimTilePhase o1, AnimTilePhase o2) {
		if (o1.getGroupName().compareTo(o2.getGroupName()) == 0) {
			return o1.getGroupName().compareTo(o2.getGroupName());
		}
		if (o1.getType().compareTo(o2.getType()) == 0) {
			return o1.getType().compareTo(o2.getType());
		}
		if (o1.getDirection().compareTo(o2.getDirection()) == 0) {
			return o1.getDirection().compareTo(o2.getDirection());
		}
		if (o1.getDirection().compareTo(o2.getDirection()) == 0) {
			return o1.getDirection().compareTo(o2.getDirection());
		}
		if (o1.getActivityType().compareTo(o2.getActivityType()) == 0) {
			return o1.getActivityType().compareTo(o2.getActivityType());
		}

		if (o1.getPhaseNumber() < o2.getPhaseNumber()) {
			return -1;
		}
		if (o1.getPhaseNumber() > o2.getPhaseNumber()) {
			return 1;
		}
		return 0;
	}

}
