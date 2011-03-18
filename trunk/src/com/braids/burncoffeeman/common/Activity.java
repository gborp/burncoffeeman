package com.braids.burncoffeeman.common;

import java.util.ArrayList;

public enum Activity {
	/** Standing activity. */
	STANDING(1, true, true),
	/** Standing activity. */
	STANDING_WITH_BOMB(1, true, false),
	/** Walking activity. */
	WALKING(4, true, true),
	/** Walking with bomb activity. */
	WALKING_WITH_BOMB(4, true, false),
	/** Kicking activity. */
	KICKING(4, false, true),
	/** Kicking activity. */
	KICKING_WITH_BOMB(4, false, false),
	/** Punching activity. */
	PUNCHING(4, false, true),
	/** Picking up activity. */
	PICKING_UP(4, false, false),
	/** Dying activity. */
	DYING(4, false, false);

	/**
	 * The number of game iterations of the activity for a one-time play. After
	 * that, it may or may not be repeated based on the repeatable attribute.
	 */
	public final int     activityIterations;
	/**
	 * Tells whether this activity is repeatable by itself if player input
	 * doesn't change.
	 */
	public final boolean repeatable;

	public final boolean hasOwnGfx;

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
	private Activity(int activityIterations, boolean repeatable, boolean hasOwnGfx) {
		this.activityIterations = activityIterations;
		this.repeatable = repeatable;
		this.hasOwnGfx = hasOwnGfx;
	}

	public int getIterations() {
		return activityIterations;
	}

	public boolean hasOwnGfx() {
		return hasOwnGfx;
	}

	public static int getNumberOfOwnGfx() {
		int result = 0;
		for (Activity a : Activity.values()) {
			if (a.hasOwnGfx()) {
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
