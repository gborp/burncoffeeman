package com.braids.burncoffeeman.server;

import java.util.EnumSet;

import com.braids.burncoffeeman.common.BombModel;
import com.braids.burncoffeeman.common.BombPhases;
import com.braids.burncoffeeman.common.BombType;
import com.braids.burncoffeeman.common.Constants;
import com.braids.burncoffeeman.common.Direction;
import com.braids.burncoffeeman.common.Item;
import com.braids.burncoffeeman.common.LevelModel;
import com.braids.burncoffeeman.common.LevelTileModel;
import com.braids.burncoffeeman.common.Wall;

public class Bomb {

	private BombModel          model;
	private boolean            firstCycle;
	private boolean            stateChanged;
	private Direction          direction;
	private BombPhases         phase;
	private int                flyingTargetX;
	private int                flyingTargetY;
	/** -1: don't tick */
	private int                tickingCountdown;
	private int                triggerPlayer;
	private EnumSet<Direction> setExcludedDirection;

	public Bomb(int playerId, int x, int y, int range, Direction direction) {
		model = new BombModel();
		model.setId(GameManager.getInstance().getNextBombId());
		model.setBombOwnerId(playerId);
		model.setType(BombType.NORMAL);
		model.setX(x);
		model.setY(y);
		model.setRange(range);
		setDirection(direction);
		setPhase(BombPhases.FLYING);
		tickingCountdown = Constants.BOMB_DETONATION_ITERATIONS;
		setExcludedDirection = EnumSet.noneOf(Direction.class);
		setFlyingTargetX(getX());
		setFlyingTargetY(getY());

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

	public void resetStateChanged() {
		stateChanged = false;
	}

	public void cycle() {

		GameManager gm = GameManager.getInstance();

		LevelModel levelModel = GameManager.getInstance().getLevelModel();

		if (phase == BombPhases.FLYING) {
			stateChanged = true;
			int newPosX = getX() + getDirection().getXMultiplier() * Constants.BOMB_FLYING_SPEED;
			int newPosY = getY() + getDirection().getYMultiplier() * Constants.BOMB_FLYING_SPEED;

			if (newPosX < 0) {
				model.setX(levelModel.getWidth() * Constants.LEVEL_COMPONENT_GRANULARITY - 1);
			} else if (newPosX > levelModel.getWidth() * Constants.LEVEL_COMPONENT_GRANULARITY - 1) {
				model.setX(0);
			} else {
				model.setX(newPosX);
			}
			if (newPosY < 0) {
				model.setY(levelModel.getHeight() * Constants.LEVEL_COMPONENT_GRANULARITY - 1);
			} else if (newPosY > levelModel.getHeight() * Constants.LEVEL_COMPONENT_GRANULARITY - 1) {
				model.setY(0);
			} else {
				model.setY(newPosY);
			}

			boolean reachedPotentialTargetPosition = false;
			if ((getDirection().getXMultiplier() != 0) && (Math.abs(getX() - getFlyingTargetX()) > Constants.LEVEL_COMPONENT_GRANULARITY)) {
				;
			} else if ((getDirection().getYMultiplier() != 0) && (Math.abs(getY() - getFlyingTargetY()) > Constants.LEVEL_COMPONENT_GRANULARITY)) {
				;
			} else {
				if ((getDirection() == Direction.LEFT) && (model.getX() <= getFlyingTargetX())) {
					reachedPotentialTargetPosition = true;
				}
				if ((getDirection() == Direction.RIGHT) && (model.getX() >= getFlyingTargetX())) {
					reachedPotentialTargetPosition = true;
				}
				if ((getDirection() == Direction.UP) && (model.getY() <= getFlyingTargetY())) {
					reachedPotentialTargetPosition = true;
				}
				if ((getDirection() == Direction.DOWN) && (model.getY() >= getFlyingTargetY())) {
					reachedPotentialTargetPosition = true;
				}
			}

			if (reachedPotentialTargetPosition) {
				boolean permanentTargetPosition = true;
				LevelTileModel levelComponent = levelModel.getTile(getComponentPosX(), getComponentPosY());
				if ((levelComponent.getWall() != Wall.GROUND) || ((levelComponent.getWall() == Wall.GROUND) && (levelComponent.getItem() != Item.NONE))) {
					permanentTargetPosition = false;
				} else if (gm.isBombAtComponentPosition(getComponentPosX(), getComponentPosY())) {
					permanentTargetPosition = false;
				} else if (gm.isPlayerAtComponentPositionExcludePlayer(getComponentPosX(), getComponentPosY(), null)) {
					permanentTargetPosition = false;
				}

				if (permanentTargetPosition) {
					setPhase(BombPhases.STANDING);
					setX(getFlyingTargetX());
					setY(getFlyingTargetY());
				} else {
					setX(getFlyingTargetX());
					setY(getFlyingTargetY());
					setDirection(gm.getRandomDirection());
				}
				gm.validateAndSetFlyingTargetPosX(this, getFlyingTargetX() + getDirectionXMultiplier() * Constants.LEVEL_COMPONENT_GRANULARITY);
				gm.validateAndSetFlyingTargetPosY(this, getFlyingTargetY() + getDirectionYMultiplier() * Constants.LEVEL_COMPONENT_GRANULARITY);
			}
		} else if (phase == BombPhases.ROLLING) {
			stateChanged = true;
			if (gm.canBombRollToComponentPosition(this, getComponentPosX() + getDirectionXMultiplier(), getComponentPosY() + getDirectionYMultiplier())) {
				// if (MathHelper.checkRandomEvent(model.getCrazyPercent() /
				// (CoreConsts.LEVEL_COMPONENT_GRANULARITY /
				// CoreConsts.BOMB_ROLLING_SPEED))) {
				// Directions newDirection =
				// Directions.values()[gameCoreHandler.getRandom().nextInt(Directions.values().length)];
				// if (newDirection.equals(model.getDirection()) ||
				// newDirection.equals(model.getDirection().getOpposite())) {
				// newDirection = newDirection.getTurnLeft();
				// }
				// if (gameCoreHandler.canBombRollToComponentPosition(model,
				// model.getComponentPosX() + newDirection.getXMultiplier(),
				// model
				// .getComponentPosY()
				// + newDirection.getYMultiplier())) {
				// model.setDirection(newDirection);
				// }
				// }

				setX(getX() + Constants.BOMB_ROLLING_SPEED * getDirectionXMultiplier());
				setY(getY() + Constants.BOMB_ROLLING_SPEED * getDirectionYMultiplier());

				LevelTileModel levelComponent = levelModel.getTile(getComponentPosX(), getComponentPosY());
				if (levelComponent.getItem() != Item.NONE) {
					levelComponent.setItem(Item.NONE);
				}
			} else {
				// if (isDetonatingOnHit()) {
				// model.setAboutToDetonate(true);
				// } else {
				alignPosXToComponentCenter();
				alignPosYToComponentCenter();

				if (getType() == BombType.JELLY) {
					setDirection(getDirection().getOpposite());
				} else {
					setPhase(BombPhases.STANDING);
				}
				// }
			}
		}

		if (firstCycle) {
			firstCycle = false;
			stateChanged = true;
		}

		if (phase == BombPhases.ROLLING || phase == BombPhases.STANDING) {
			if (tickingCountdown == 0) {
				setPhase(BombPhases.ABOUT_TO_DETONATE);
				tickingCountdown--;
			} else if (tickingCountdown > 0) {
				tickingCountdown--;
			}
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
		if (this.phase != phase) {
			stateChanged = true;
			this.phase = phase;
			if (phase == BombPhases.FLYING) {
				// tickingCountdown = -1;
			} else if (phase == BombPhases.DETONATED) {
				model.setType(BombType.REMOVE);
			}
		}
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
		return getDirection().getXMultiplier();
	}

	public int getDirectionYMultiplier() {
		return getDirection().getYMultiplier();
	}

	public int getComponentPosX() {
		return model.getX() / Constants.LEVEL_COMPONENT_GRANULARITY;
	}

	public int getComponentPosY() {
		return model.getY() / Constants.LEVEL_COMPONENT_GRANULARITY;
	}

	public boolean isAboutToDetonate() {
		return phase == BombPhases.ABOUT_TO_DETONATE;
	}

	public boolean isDetonated() {
		return phase == BombPhases.DETONATED;
	}

	public void setTriggererPlayer(int triggerPlayer) {
		this.triggerPlayer = triggerPlayer;
	}

	public int getTriggererPlayer() {
		return triggerPlayer;
	}

	public int getRange() {
		return model.getRange();
	}

	public void addExcludedDetonationDirection(Direction direction) {
		setExcludedDirection.add(direction);
	}

	public boolean isExcludedDetonationDirection(Direction direction) {
		return setExcludedDirection.contains(direction);
	}

	private void alignPosXToComponentCenter() {
		setX(getX() + Constants.LEVEL_COMPONENT_GRANULARITY / 2 - getX() % Constants.LEVEL_COMPONENT_GRANULARITY);
	}

	/**
	 * Aligns the x coordinate of the position to be at the center of the
	 * component this position is on.
	 */
	private void alignPosYToComponentCenter() {
		setY(getY() + Constants.LEVEL_COMPONENT_GRANULARITY / 2 - getY() % Constants.LEVEL_COMPONENT_GRANULARITY);
	}

}
