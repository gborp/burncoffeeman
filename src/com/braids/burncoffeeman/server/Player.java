package com.braids.burncoffeeman.server;

import java.io.IOException;
import java.net.Socket;
import java.util.EnumMap;
import java.util.EnumSet;

import com.braids.burncoffeeman.common.Activity;
import com.braids.burncoffeeman.common.BombPhases;
import com.braids.burncoffeeman.common.ClientInputModel;
import com.braids.burncoffeeman.common.Constants;
import com.braids.burncoffeeman.common.Direction;
import com.braids.burncoffeeman.common.Disease;
import com.braids.burncoffeeman.common.Fire;
import com.braids.burncoffeeman.common.Helper;
import com.braids.burncoffeeman.common.Item;
import com.braids.burncoffeeman.common.LevelModel;
import com.braids.burncoffeeman.common.LevelTileModel;
import com.braids.burncoffeeman.common.PlayerInfoModel;
import com.braids.burncoffeeman.common.PlayerModel;
import com.braids.burncoffeeman.common.Wall;

public class Player {

	private GameManager                          gameManager;
	private PlayerModel                          model;
	private boolean                              action1;
	private boolean                              action2;
	private PlayerInfoModel                      playerInfo;
	private PlayerProcessInput                   processInput;
	private PlayerProcessOutput                  processOutput;

	private boolean                              firstCycle;             ;
	private boolean                              stateChanged;
	private ClientInputModel                     unprocessedClientInput;
	private ClientInputModel                     clientInput;
	private ClientInputModel                     prevClientInput;
	private EnumMap<Item, Integer>               mapItems;
	private EnumSet<Item>                        lstNonAccumItems;

	private int                                  iterationCounter;
	private com.braids.burncoffeeman.server.Bomb pickedUpBomb;

	public Player(Socket socket, int playerId) throws IOException {
		gameManager = GameManager.getInstance();
		model = new PlayerModel();
		model.setPlayerId(playerId);
		model.setDirection(Direction.DOWN);
		model.setActivity(Activity.STANDING);
		model.setX(Constants.COMPONENT_SIZE_IN_VIRTUAL + Constants.COMPONENT_SIZE_IN_VIRTUAL / 2);
		model.setY(Constants.COMPONENT_SIZE_IN_VIRTUAL + Constants.COMPONENT_SIZE_IN_VIRTUAL / 2);
		model.setSpeed(Constants.BOMBERMAN_BASIC_SPEED);
		model.setVitality(100);

		mapItems = new EnumMap<Item, Integer>(Item.class);
		for (Item i : Item.values()) {
			mapItems.put(i, 0);
		}
		mapItems.put(Item.BOMB, 4);

		lstNonAccumItems = EnumSet.noneOf(Item.class);

		clientInput = new ClientInputModel();
		prevClientInput = clientInput;
		firstCycle = true;

		processInput = new PlayerProcessInput(socket, this);
		processOutput = new PlayerProcessOutput(socket, this);
		new Thread(processInput).start();
		processInput.waitUntilReady();
	}

	public void setPlayerInfoModel(PlayerInfoModel playerInfo) {
		this.playerInfo = playerInfo;
	}

	public void addClientInput(ClientInputModel data) {
		synchronized (this) {
			unprocessedClientInput = data;
		}
	}

	private ClientInputModel getClientInput() {
		synchronized (this) {
			ClientInputModel result = unprocessedClientInput;
			unprocessedClientInput = null;
			return result;
		}
	}

	public void sendToClient(byte[] data) {
		processOutput.append(data);
	}

	private int getComponentPosX() {
		return model.getX() / Constants.LEVEL_COMPONENT_GRANULARITY;
	}

	private int getComponentPosY() {
		return model.getY() / Constants.LEVEL_COMPONENT_GRANULARITY;
	}

	private boolean canPlayerStepToPosition(int posX, int posY) {
		int componentPosX = posX / Constants.LEVEL_COMPONENT_GRANULARITY;
		int componentPosY = posY / Constants.LEVEL_COMPONENT_GRANULARITY;

		Wall wall = gameManager.getLevelModel().getTile(componentPosX, componentPosY).getWall();

		// if (model.hasNonAccumItem(Items.WALL_CLIMBING)) {
		// if ((wall == Walls.CONCRETE) || (wall == Walls.DEATH) || (wall ==
		// Walls.DEATH_WARN) || (wall == Walls.GATEWAY_ENTRANCE)
		// || (wall == Walls.GATEWAY_EXIT)) {
		// return false;
		// }
		// } else {
		if (wall != Wall.GROUND) {
			return false;
		}
		// }

		// if (gameCoreHandler.isBombAtComponentPosition(componentPosX,
		// componentPosY)) {
		// return false;
		// }

		return true;
	}

	private boolean determineNewDirection() {
		final int posX = model.getX();
		final int posY = model.getY();

		final int movementCorrectionSensitivity = Constants.COMPONENT_SIZE_IN_VIRTUAL * Constants.MOVEMENT_CORRECTION_SENSITIVITY / 200;

		if (clientInput.isDownPress()) {
			model.setDirection(Direction.DOWN);
			if (!canPlayerStepToPosition(posX, posY + (Constants.LEVEL_COMPONENT_GRANULARITY / 2 + 1))) {
				if (posX % Constants.LEVEL_COMPONENT_GRANULARITY < movementCorrectionSensitivity) {
					if (canPlayerStepToPosition(posX - Constants.LEVEL_COMPONENT_GRANULARITY, posY)
					        && canPlayerStepToPosition(posX - Constants.LEVEL_COMPONENT_GRANULARITY, posY + Constants.LEVEL_COMPONENT_GRANULARITY)) {
						model.setDirection(Direction.LEFT);
						return true;
					}
				}

				if (posX % Constants.LEVEL_COMPONENT_GRANULARITY >= Constants.LEVEL_COMPONENT_GRANULARITY - movementCorrectionSensitivity) {

					if (canPlayerStepToPosition(posX + Constants.LEVEL_COMPONENT_GRANULARITY, posY)
					        && canPlayerStepToPosition(posX + Constants.LEVEL_COMPONENT_GRANULARITY, posY + Constants.LEVEL_COMPONENT_GRANULARITY)) {
						model.setDirection(Direction.RIGHT);
						return true;
					}
				}
			} else {
				if (posX % Constants.LEVEL_COMPONENT_GRANULARITY < Constants.LEVEL_COMPONENT_GRANULARITY / 2) {
					if (!canPlayerStepToPosition(posX - Constants.LEVEL_COMPONENT_GRANULARITY, posY + Constants.LEVEL_COMPONENT_GRANULARITY)) {
						model.setDirection(Direction.RIGHT);
						return true;
					}
				}
				if (posX % Constants.LEVEL_COMPONENT_GRANULARITY > Constants.LEVEL_COMPONENT_GRANULARITY / 2) {
					if (!canPlayerStepToPosition(posX + Constants.LEVEL_COMPONENT_GRANULARITY, posY + Constants.LEVEL_COMPONENT_GRANULARITY)) {
						model.setDirection(Direction.LEFT);
						return true;
					}
				}
			}
		} else if (clientInput.isUpPress()) {
			model.setDirection(Direction.UP);
			if (!canPlayerStepToPosition(posX, posY - (Constants.LEVEL_COMPONENT_GRANULARITY / 2 + 1))) { // The

				if (posX % Constants.LEVEL_COMPONENT_GRANULARITY < movementCorrectionSensitivity) {
					if (canPlayerStepToPosition(posX - Constants.LEVEL_COMPONENT_GRANULARITY, posY)
					        && canPlayerStepToPosition(posX - Constants.LEVEL_COMPONENT_GRANULARITY, posY - Constants.LEVEL_COMPONENT_GRANULARITY)) {
						model.setDirection(Direction.LEFT);
						return true;
					}
				}

				if (posX % Constants.LEVEL_COMPONENT_GRANULARITY >= Constants.LEVEL_COMPONENT_GRANULARITY - movementCorrectionSensitivity) {
					if (canPlayerStepToPosition(posX + Constants.LEVEL_COMPONENT_GRANULARITY, posY)
					        && canPlayerStepToPosition(posX + Constants.LEVEL_COMPONENT_GRANULARITY, posY - Constants.LEVEL_COMPONENT_GRANULARITY)) {
						model.setDirection(Direction.RIGHT);
						return true;
					}
				}
			}

			else {
				if (posX % Constants.LEVEL_COMPONENT_GRANULARITY < Constants.LEVEL_COMPONENT_GRANULARITY / 2) {
					if (!canPlayerStepToPosition(posX - Constants.LEVEL_COMPONENT_GRANULARITY, posY - Constants.LEVEL_COMPONENT_GRANULARITY)) {
						model.setDirection(Direction.RIGHT);
						return true;
					}
				}
				if (posX % Constants.LEVEL_COMPONENT_GRANULARITY > Constants.LEVEL_COMPONENT_GRANULARITY / 2) {
					if (!canPlayerStepToPosition(posX + Constants.LEVEL_COMPONENT_GRANULARITY, posY - Constants.LEVEL_COMPONENT_GRANULARITY)) {
						model.setDirection(Direction.LEFT);
						return true;
					}
				}
			}
		} else if (clientInput.isLeftPress()) {
			model.setDirection(Direction.LEFT);
			if (!canPlayerStepToPosition(posX - (Constants.LEVEL_COMPONENT_GRANULARITY / 2 + 1), posY)) { // The
				if (posY % Constants.LEVEL_COMPONENT_GRANULARITY < movementCorrectionSensitivity) {

					if (canPlayerStepToPosition(posX, posY - Constants.LEVEL_COMPONENT_GRANULARITY)
					        && canPlayerStepToPosition(posX - Constants.LEVEL_COMPONENT_GRANULARITY, posY - Constants.LEVEL_COMPONENT_GRANULARITY)) {
						model.setDirection(Direction.UP);
						return true;
					}
				}

				if (posY % Constants.LEVEL_COMPONENT_GRANULARITY >= Constants.LEVEL_COMPONENT_GRANULARITY - movementCorrectionSensitivity) {

					if (canPlayerStepToPosition(posX, posY + Constants.LEVEL_COMPONENT_GRANULARITY)
					        && canPlayerStepToPosition(posX - Constants.LEVEL_COMPONENT_GRANULARITY, posY + Constants.LEVEL_COMPONENT_GRANULARITY)) {
						model.setDirection(Direction.DOWN);
						return true;
					}
				}
			} else {
				if (posY % Constants.LEVEL_COMPONENT_GRANULARITY < Constants.LEVEL_COMPONENT_GRANULARITY / 2) {
					if (!canPlayerStepToPosition(posX - Constants.LEVEL_COMPONENT_GRANULARITY, posY - Constants.LEVEL_COMPONENT_GRANULARITY)) {
						model.setDirection(Direction.DOWN);
						return true;
					}
				}
				if (posY % Constants.LEVEL_COMPONENT_GRANULARITY > Constants.LEVEL_COMPONENT_GRANULARITY / 2) {
					if (!canPlayerStepToPosition(posX - Constants.LEVEL_COMPONENT_GRANULARITY, posY + Constants.LEVEL_COMPONENT_GRANULARITY)) {
						model.setDirection(Direction.UP);
						return true;
					}
				}
			}
		} else if (clientInput.isRightPress()) {
			model.setDirection(Direction.RIGHT);
			if (!canPlayerStepToPosition(posX + (Constants.LEVEL_COMPONENT_GRANULARITY / 2 + 1), posY)) {
				if (posY % Constants.LEVEL_COMPONENT_GRANULARITY < movementCorrectionSensitivity) {
					if (canPlayerStepToPosition(posX, posY - Constants.LEVEL_COMPONENT_GRANULARITY)
					        && canPlayerStepToPosition(posX + Constants.LEVEL_COMPONENT_GRANULARITY, posY - Constants.LEVEL_COMPONENT_GRANULARITY)) {
						model.setDirection(Direction.UP);
						return true;
					}
				}

				if (posY % Constants.LEVEL_COMPONENT_GRANULARITY >= Constants.LEVEL_COMPONENT_GRANULARITY - movementCorrectionSensitivity) {
					if (canPlayerStepToPosition(posX, posY + Constants.LEVEL_COMPONENT_GRANULARITY) // If
					        && canPlayerStepToPosition(posX + Constants.LEVEL_COMPONENT_GRANULARITY, posY + Constants.LEVEL_COMPONENT_GRANULARITY)) { // ...and
						model.setDirection(Direction.DOWN);
						return true;
					}
				}
			} else {
				if (posY % Constants.LEVEL_COMPONENT_GRANULARITY < Constants.LEVEL_COMPONENT_GRANULARITY / 2) {
					if (!canPlayerStepToPosition(posX + Constants.LEVEL_COMPONENT_GRANULARITY, posY - Constants.LEVEL_COMPONENT_GRANULARITY)) {
						model.setDirection(Direction.DOWN);
						return true;
					}
				}
				if (posY % Constants.LEVEL_COMPONENT_GRANULARITY > Constants.LEVEL_COMPONENT_GRANULARITY / 2) {
					if (!canPlayerStepToPosition(posX + Constants.LEVEL_COMPONENT_GRANULARITY, posY + Constants.LEVEL_COMPONENT_GRANULARITY)) {
						model.setDirection(Direction.UP);
						return true;
					}
				}
			}
		}

		return false;
	}

	private boolean isDirectionKeyPressed() {
		return clientInput.isLeftPress() || clientInput.isRightPress() || clientInput.isUpPress() || clientInput.isDownPress();
	}

	public int getDirectionXMultiplier() {
		if (model.getDirection() == Direction.LEFT) {
			return -1;
		} else if (model.getDirection() == Direction.RIGHT) {
			return 1;
		} else {
			return 0;
		}
	}

	public int getDirectionYMultiplier() {
		if (model.getDirection() == Direction.UP) {
			return -1;
		} else if (model.getDirection() == Direction.DOWN) {
			return 1;
		} else {
			return 0;
		}
	}

	private void setActivity(Activity activity) {
		if (model.getActivity() != activity) {
			stateChanged = true;
			model.setActivity(activity);
		}
	}

	private void processActionsAndHandleActivityTransitions() {
		switch (model.getActivity()) {

			case STANDING:
				if (isDirectionKeyPressed()) {
					setActivity(Activity.WALKING);
				}
				if (clientInput.isAction1Press()) {
					handleFunction1WithoutBomb();
				}
				if (clientInput.isAction1Press() && !prevClientInput.isAction1Press()) {
					handleFunction1WithoutBomb();
				} else if (clientInput.isAction2Press() && !prevClientInput.isAction2Press()) {
					handleFunction2();
				}
				break;

			case STANDING_WITH_BOMB:
				if (isDirectionKeyPressed()) {
					setActivity(Activity.WALKING_WITH_BOMB);
				}
				if (!clientInput.isAction1Press()) {
					throwBombAway();
				}
				break;

			case WALKING:
				if (!isDirectionKeyPressed()) {
					setActivity(Activity.STANDING);
				}
				if (clientInput.isAction1Press() && !prevClientInput.isAction1Press()) {
					handleFunction1WithoutBomb();
				} else if (clientInput.isAction2Press() && !prevClientInput.isAction2Press()) {
					handleFunction2();
				}
				break;

			case WALKING_WITH_BOMB:
				if (!isDirectionKeyPressed()) {
					setActivity(Activity.STANDING_WITH_BOMB);
				}
				if (!clientInput.isAction1Press()) {
					throwBombAway();
				}
				break;

			case KICKING:
				if (getIterationCounter() == Activity.KICKING.activityIterations - 1) {
					setActivity(isDirectionKeyPressed() ? Activity.WALKING : Activity.STANDING);
				}
				break;

			case KICKING_WITH_BOMB:
				if (getIterationCounter() == Activity.KICKING_WITH_BOMB.activityIterations - 1) {
					setActivity(isDirectionKeyPressed() ? Activity.WALKING_WITH_BOMB : Activity.STANDING_WITH_BOMB);
				}
				break;

			case PUNCHING:
				if (getIterationCounter() == Activity.PUNCHING.activityIterations - 1) {
					setActivity(isDirectionKeyPressed() ? Activity.WALKING : Activity.STANDING);
				}
				break;

			case PICKING_UP:
				if (getIterationCounter() == Activity.PICKING_UP.activityIterations - 1) {
					setActivity(isDirectionKeyPressed() ? Activity.WALKING_WITH_BOMB : Activity.STANDING_WITH_BOMB);
				}
				break;

			case DYING:
				break;

		}
	}

	private void handleFunction2() {
	// TODO Auto-generated method stub

	}

	private void stepPlayer(final int invocationDepth) {
		boolean wasStepCutInOrderToTurn = false;

		Activity activity = model.getActivity();

		if ((activity == Activity.WALKING) || (activity == Activity.WALKING_WITH_BOMB) || ((activity == Activity.PUNCHING) && isDirectionKeyPressed())) {
			boolean movementCorrectionActivated = determineNewDirection();

			int speed = model.getSpeed();
			// TODO
			// + model.getEffectiveRollerSkates() *
			// Constants.BOBMERMAN_ROLLER_SKATES_SPEED_INCREMENT;
			if (speed > Constants.BOBMERMAN_MAX_SPEED) {
				speed = Constants.BOBMERMAN_MAX_SPEED;
			}

			boolean needsToBeContained = false;
			final int posXAhead = model.getX() + getDirectionXMultiplier() * Constants.LEVEL_COMPONENT_GRANULARITY;
			final int posYAhead = model.getY() + getDirectionYMultiplier() * Constants.LEVEL_COMPONENT_GRANULARITY;
			if (movementCorrectionActivated || (!movementCorrectionActivated && !canPlayerStepToPosition(posXAhead, posYAhead))) {
				needsToBeContained = true;
			}

			if (needsToBeContained) {
				int newSpeed = -1;
				final boolean bombAhead = gameManager.isBombAtComponentPosition(posXAhead / Constants.LEVEL_COMPONENT_GRANULARITY, posYAhead
				        / Constants.LEVEL_COMPONENT_GRANULARITY);

				switch (model.getDirection()) {
					case LEFT:
						if (bombAhead && (model.getX() % Constants.LEVEL_COMPONENT_GRANULARITY < Constants.LEVEL_COMPONENT_GRANULARITY / 2)) {
							newSpeed = 0;
						} else {
							newSpeed = speed - (Constants.LEVEL_COMPONENT_GRANULARITY / 2 - (model.getX() - speed) % Constants.LEVEL_COMPONENT_GRANULARITY);
						}
						break;
					case RIGHT:
						if (bombAhead && (model.getX() % Constants.LEVEL_COMPONENT_GRANULARITY > Constants.LEVEL_COMPONENT_GRANULARITY / 2)) {
							newSpeed = 0;
						} else {
							newSpeed = speed - ((model.getX() + speed) % Constants.LEVEL_COMPONENT_GRANULARITY - Constants.LEVEL_COMPONENT_GRANULARITY / 2);
						}
						break;
					case UP:
						if (bombAhead && (model.getY() % Constants.LEVEL_COMPONENT_GRANULARITY < Constants.LEVEL_COMPONENT_GRANULARITY / 2)) {
							newSpeed = 0;
						} else {
							newSpeed = speed - (Constants.LEVEL_COMPONENT_GRANULARITY / 2 - (model.getY() - speed) % Constants.LEVEL_COMPONENT_GRANULARITY);
						}
						break;
					case DOWN:
						if (bombAhead && (model.getY() % Constants.LEVEL_COMPONENT_GRANULARITY > Constants.LEVEL_COMPONENT_GRANULARITY / 2)) {
							newSpeed = 0;
						} else {
							newSpeed = speed - ((model.getY() + speed) % Constants.LEVEL_COMPONENT_GRANULARITY - Constants.LEVEL_COMPONENT_GRANULARITY / 2);
						}
						break;
				}

				if ((newSpeed >= 0) && (newSpeed < speed)) {
					// If it's negative, it's beyond one level component, but
					// that case we surly don't need to change speed
					if (movementCorrectionActivated) {
						wasStepCutInOrderToTurn = true;
					}
					speed = newSpeed;
				}
			}

			LevelModel levelModel = gameManager.getLevelModel();
			if (speed > 0) {
				model.setAnimationPhase((model.getAnimationPhase() + speed) & 0xffff);

				model.setX(model.getX() + getDirectionXMultiplier() * speed);
				model.setY(model.getY() + getDirectionYMultiplier() * speed);
				stateChanged = true;
				// TODO
				// checkAndHandleItemPickingUp();
			}
		}

		if (wasStepCutInOrderToTurn && (invocationDepth == 0)) {
			stepPlayer(invocationDepth + 1); // Without this it gives a feeling
			// of stucking for a moment on
			// turns!!
		}
	}

	private boolean hasBlueGloves() {
		return true;
	}

	private boolean hasLiner() {
		return false;
	}

	private boolean hasNonAccumItem(Item item) {
		return lstNonAccumItems.contains(item);
	}

	private int getNumberOfItem(Item item) {
		return mapItems.get(item);
	}

	private void setNumberOfItem(Item item, int count) {
		mapItems.put(item, count);
	}

	private void handleFunction1WithoutBomb() {
		if (model.getDisease().contains(Disease.NO_BOMB)) {
			return;
		}

		int playerComponentPosX = getComponentPosX();
		int playerComponentPosY = getComponentPosY();
		int componentPosX = playerComponentPosX;
		int componentPosY = playerComponentPosY;
		int maxPlacableBombs = 4;

		LevelTileModel tile = gameManager.getLevelModel().getTile(componentPosX, componentPosY);

		if (tile.getFire() != Fire.NONE) {
			return;
		}

		Bomb bombAtPos = gameManager.getBombAtComponentPosition(componentPosX, componentPosY);

		if (bombAtPos != null) {
			if (hasBlueGloves()) {
				if (bombAtPos.getBombOwnerId() != model.getPlayerId()) {
					return;
				}
				pickedUpBomb = bombAtPos;
				gameManager.removeBomb(bombAtPos);
				setActivity(Activity.PICKING_UP);
				return;
			}
			// if (model.hasNonAccumItem(Items.BOMB_SPRINKLE)) {
			// maxPlacableBombs =
			// model.accumulateableItemQuantitiesMap.get(Items.BOMB);
			// // The position of the first bomb is ahead of us.
			// componentPosX += model.getDirectionXMultiplier();
			// componentPosY += model.getDirectionYMultiplier();
			// }
		}

		LevelModel levelModel = gameManager.getLevelModel();

		int bombsCount = getNumberOfItem(Item.BOMB);
		for (int i = 0; i < maxPlacableBombs; i++) {
			LevelTileModel comp = levelModel.getTile(componentPosX, componentPosY);
			Wall wallInPosition = comp.getWall();

			if (gameManager.isBombAtComponentPosition(componentPosX, componentPosY) || (wallInPosition != Wall.GROUND)
			        || ((wallInPosition == Wall.GROUND) && (comp.getItem() != Item.NONE))) {
				break;
			}
			if ((componentPosX != playerComponentPosX) || (componentPosY != playerComponentPosY)) {
				if (gameManager.isPlayerAtComponentPositionExcludePlayer(componentPosX, componentPosY, model)) {
					break;
				}
			}

			bombsCount--;
			setNumberOfItem(Item.BOMB, bombsCount);

			// int bombRange = model.hasNonAccumItem(Items.SUPER_FIRE) ?
			// CoreConsts.SUPER_FIRE_RANGE :
			// model.accumulateableItemQuantitiesMap.get(Items.FIRE) + 1;
			int bombRange = 3;

			// if (model.getOwnedDiseases().containsKey(Diseases.SHORT_RANGE)) {
			// bombRange = 2;
			// }

			gameManager.addBomb(new Bomb(model.getPlayerId(), Helper.getCenterOfTileX(componentPosX), Helper.getCenterOfTileX(componentPosY), bombRange));

			// if (hasNonAccumItem(Items.JELLY)) {
			// newBombModel.setType(BombTypes.JELLY);
			// } else if (model.hasNonAccumItem(Items.TRIGGER) &&
			// (model.getPlacableTriggeredBombs() > 0)) {
			// newBombModel.setType(BombTypes.TRIGGERED);
			// model.setPlacableTriggeredBombs(model.getPlacableTriggeredBombs()
			// - 1);
			// } else {
			// newBombModel.setType(BombTypes.NORMAL);
			// }

			// if
			// (model.getOwnedDiseases().containsKey(Diseases.FAST_DETONATION))
			// {
			// newBombModel.setTickingIterations(CoreConsts.BOMB_DETONATION_ITERATIONS
			// * 3 / 4);
			// }
			componentPosX += getDirectionXMultiplier();
			componentPosY += getDirectionYMultiplier();
		}
	}

	private void throwBombAway() {

		// pickedUpBomb.setTickingIterations(0);
		pickedUpBomb.setDirection(model.getDirection());
		pickedUpBomb.setX(getComponentPosX() * Constants.LEVEL_COMPONENT_GRANULARITY + Constants.LEVEL_COMPONENT_GRANULARITY / 2);
		pickedUpBomb.setY(getComponentPosY() * Constants.LEVEL_COMPONENT_GRANULARITY + Constants.LEVEL_COMPONENT_GRANULARITY / 2);

		pickedUpBomb.setPhase(BombPhases.FLYING);

		gameManager.validateAndSetFlyingTargetPosX(pickedUpBomb, pickedUpBomb.getX() + pickedUpBomb.getDirectionXMultiplier() * Constants.BOMB_FLYING_DISTANCE);
		gameManager.validateAndSetFlyingTargetPosY(pickedUpBomb, pickedUpBomb.getY() + pickedUpBomb.getDirectionYMultiplier() * Constants.BOMB_FLYING_DISTANCE);

		gameManager.addBomb(pickedUpBomb);
		pickedUpBomb = null;

		setActivity(model.getActivity() == Activity.STANDING_WITH_BOMB ? Activity.STANDING : Activity.WALKING);
	}

	// /**
	// * Tries to kick.
	// */
	// private void tryToKick() {
	// final int componentPosXAhead = model.getComponentPosX() +
	// model.getDirectionXMultiplier();
	// final int componentPosYAhead = model.getComponentPosY() +
	// model.getDirectionYMultiplier();
	//
	// final Integer bombIndexAhead =
	// gameCoreHandler.getBombIndexAtComponentPosition(componentPosXAhead,
	// componentPosYAhead);
	// if (bombIndexAhead == null) {
	// return;
	// }
	//
	// final BombModel bombModel =
	// gameCoreHandler.getBombModels().get(bombIndexAhead);
	//
	// final int componentPosXAheadAhead = componentPosXAhead +
	// model.getDirectionXMultiplier();
	// final int componentPosYAheadAhead = componentPosYAhead +
	// model.getDirectionYMultiplier();
	//
	// if (!gameCoreHandler.canBombRollToComponentPosition(bombModel,
	// componentPosXAheadAhead, componentPosYAheadAhead)) {
	// return;
	// }
	//
	// // Activity can be PUNCHING!!!!
	// model.setActivity(model.getActivity() == Activities.WALKING_WITH_BOMB ?
	// Activities.KICKING_WITH_BOMB : Activities.KICKING);
	// bombModel.setPhase(BombPhases.ROLLING);
	// bombModel.setDirection(model.getDirection()); // We punch in our
	// // direction
	// if (getModel().hasNonAccumItem(Items.CRAZY_BOOTS)) {
	// bombModel.setCrazyPercent(0.2f);
	// } else {
	// bombModel.setCrazyPercent(0);
	// }
	//
	// // We align the bomb to the center based on the kicking direction
	// if (bombModel.getDirectionXMultiplier() != 0) {
	// bombModel.alignPosYToComponentCenter();
	// }
	// if (bombModel.getDirectionYMultiplier() != 0) {
	// bombModel.alignPosXToComponentCenter();
	// }
	// }

	public void cycle() {
		stateChanged = false;

		ClientInputModel newClientInput = getClientInput();
		if (newClientInput != null) {
			prevClientInput = clientInput;
			clientInput = newClientInput;
		}
		processActionsAndHandleActivityTransitions();
		stepPlayer(0);

		if (getIterationCounter() + 1 < model.getActivity().activityIterations) {
			nextIteration();
		} else if (model.getActivity().repeatable) {
			setIterationCounter(0);
		}

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

	public int getIterationCounter() {
		return iterationCounter;
	}

	public void setIterationCounter(final int iterationCounter) {
		this.iterationCounter = iterationCounter;
	}

	public void nextIteration() {
		iterationCounter++;
	}

	public String getGfxHeadGroup() {
		return playerInfo.getGfxHeadGroup();
	}

	public String getGfxBodyGroup() {
		return playerInfo.getGfxBodyGroup();
	}

	public String getGfxLegsGroup() {
		return playerInfo.getGfxLegsGroup();
	}
}
