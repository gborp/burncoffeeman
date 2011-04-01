package com.braids.burncoffeeman.common;

public enum BodyPart {
	HEAD(8), BODY(4), LEGS(4);

	private int height;

	BodyPart(int height) {
		this.height = height;
	}

	public int getHeight() {
		return height;
	}
}
