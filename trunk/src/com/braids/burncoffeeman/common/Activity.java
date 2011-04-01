package com.braids.burncoffeeman.common;

import java.util.ArrayList;
import java.util.EnumSet;

public enum Activity {
	/** Standing activity. */
	STANDING(1, true, true, EnumSet.of(BodyPart.HEAD, BodyPart.BODY, BodyPart.LEGS)),
	/** Standing activity. */
	STANDING_WITH_BOMB(1, true, false, null),
	/** Walking activity. */
	WALKING(4, true, true, EnumSet.of(BodyPart.LEGS)),
	/** Walking with bomb activity. */
	WALKING_WITH_BOMB(4, true, false, null),
	/** Kicking activity. */
	KICKING(4, false, true, EnumSet.of(BodyPart.LEGS)),
	/** Kicking activity. */
	KICKING_WITH_BOMB(4, false, false, null),
	/** Punching activity. */
	PUNCHING(4, false, true, EnumSet.of(BodyPart.BODY)),
	/** Picking up activity. */
	PICKING_UP(4, false, false, null),
	/** Dying activity. */
	DYING(4, false, false, null);

	/**
	 * The number of game iterations of the activity for a one-time play. After
	 * that, it may or may not be repeated based on the repeatable attribute.
	 */
	public final int                activityIterations;
	/**
	 * Tells whether this activity is repeatable by itself if player input
	 * doesn't change.
	 */
	public final boolean            repeatable;

	public final boolean            hasOwnGfx;
	private final EnumSet<BodyPart> bodyParts;

	/**
	 * Creates a new Activities.
	 * 
	 * @param activityIterations
	 *            the number of game iterations of the activity for a one-time
	 *            play
	 * @param repeatable
	 *            tells whether this activity is repeatable once it has been
	 *            played over
	 */
	private Activity(int activityIterations, boolean repeatable, boolean hasOwnGfx, EnumSet<BodyPart> bodyParts) {
		this.activityIterations = activityIterations;
		this.repeatable = repeatable;
		this.hasOwnGfx = hasOwnGfx;
		this.bodyParts = bodyParts;
	}

	public int getIterations() {
		return activityIterations;
	}

	public boolean hasOwnGfx() {
		return hasOwnGfx;
	}

	public boolean hasOwnGfx(BodyPart bodyPart) {
		return hasOwnGfx && bodyParts.contains(bodyPart);
	}

	public EnumSet<BodyPart> getBodyParts() {
		return bodyParts;
	}

	public static int getNumberOfOwnGfx(BodyPart phaseType) {
		int result = 0;
		for (Activity a : Activity.values()) {
			if (a.hasOwnGfx(phaseType)) {
				result++;
			}
		}
		return result;
	}

	public static ArrayList<Activity> getAnimateds() {
		ArrayList<Activity> lstAnimatedActivities = new ArrayList<Activity>();
		for (Activity li : Activity.values()) {
			if (li.hasOwnGfx) {
				lstAnimatedActivities.add(li);
			}
		}
		return lstAnimatedActivities;
	}

}
