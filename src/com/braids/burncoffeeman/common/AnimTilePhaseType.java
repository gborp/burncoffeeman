package com.braids.burncoffeeman.common;

public enum AnimTilePhaseType {
	HEAD(8), BODY(4), LEGS(4);

	private int height;

	AnimTilePhaseType(int height) {
		this.height = height;
	}

	public int getHeight() {
		return height;
	}
}
