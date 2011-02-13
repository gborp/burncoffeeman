package com.braids.burncoffeeman.common;

public enum Direction {
	UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0);

	private final int xMultiplier;
	private final int yMultiplier;

	private Direction(int xMultiplier, int yMultiplier) {
		this.xMultiplier = xMultiplier;
		this.yMultiplier = yMultiplier;
	}

	public int getXMultiplier() {
		return xMultiplier;
	}

	public int getYMultiplier() {
		return yMultiplier;
	}

	public Direction getOpposite() {
		switch (this) {
			case UP:
				return DOWN;
			case DOWN:
				return UP;
			case LEFT:
				return RIGHT;
			case RIGHT:
				return LEFT;

		}
		return null;
	}
}
