package com.braids.burncoffeeman.server;

import com.braids.burncoffeeman.common.BombModel;
import com.braids.burncoffeeman.common.BombPhases;
import com.braids.burncoffeeman.common.BombType;
import com.braids.burncoffeeman.common.Direction;

public class Bomb {

	private BombModel  model;
	private boolean    firstCycle;
	private boolean    stateChanged;
	private Direction  direction;
	private BombPhases phase;
	private int        flyingTargetX;
	private int        flyingTargetY;

	public Bomb(int playerId, int x, int y, int range) {
		model = new BombModel();
		model.setId(GameManager.getInstance().getNextBombId());
		model.setBombOwnerId(playerId);
		model.setType(BombType.NORMAL);
		model.setX(x);
		model.setY(y);
		model.setRange(range);

		firstCycle = true;
	}

	public int getX() {
		return model.getX();
	}

	public int getBombOwnerId() {
		return this.model.getBombOwnerId();
	}

	public BombType getType() {
		return this.model.getType();
	}

	public int getY() {
		return this.model.getY();
	}

	public int getZ() {
		return this.model.getZ();
	}

	public int hashCode() {
		return this.model.hashCode();
	}

	public void setBombOwnerId(int bombOwnerId) {
		this.model.setBombOwnerId(bombOwnerId);
	}

	public void setType(BombType type) {
		this.model.setType(type);
	}

	public void setX(int x) {
		this.model.setX(x);
	}

	public void setY(int y) {
		this.model.setY(y);
	}

	public void setZ(int z) {
		this.model.setZ(z);
	}

	public int getId() {
		return model.getId();
	}

	public void cycle() {
		stateChanged = false;

		if (firstCycle) {
			firstCycle = false;
			stateChanged = true;
		}
	}

	public byte[] getCodedModel() {
		return model.code();
	}

	public boolean isStateChanged() {
		return stateChanged;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public void setPhase(BombPhases phase) {
		this.phase = phase;
	}

	public Direction getDirection() {
		return this.direction;
	}

	public BombPhases getPhase() {
		return this.phase;
	}

	public int getFlyingTargetX() {
		return this.flyingTargetX;
	}

	public void setFlyingTargetX(int flyingTargetX) {
		this.flyingTargetX = flyingTargetX;
	}

	public int getFlyingTargetY() {
		return this.flyingTargetY;
	}

	public void setFlyingTargetY(int flyingTargetY) {
		this.flyingTargetY = flyingTargetY;
	}

	public int getDirectionXMultiplier() {
		if (getDirection() == Direction.LEFT) {
			return -1;
		} else if (getDirection() == Direction.RIGHT) {
			return 1;
		} else {
			return 0;
		}
	}

	public int getDirectionYMultiplier() {
		if (getDirection() == Direction.UP) {
			return -1;
		} else if (getDirection() == Direction.DOWN) {
			return 1;
		} else {
			return 0;
		}
	}
}
