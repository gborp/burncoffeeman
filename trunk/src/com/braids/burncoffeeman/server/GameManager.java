package com.braids.burncoffeeman.server;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.braids.burncoffeeman.common.AnimTileModel;
import com.braids.burncoffeeman.common.AnimTilePhaseType;
import com.braids.burncoffeeman.common.BombPhases;
import com.braids.burncoffeeman.common.Constants;
import com.braids.burncoffeeman.common.Direction;
import com.braids.burncoffeeman.common.Fire;
import com.braids.burncoffeeman.common.GraphicsTemplateManager;
import com.braids.burncoffeeman.common.Helper;
import com.braids.burncoffeeman.common.Item;
import com.braids.burncoffeeman.common.LevelModel;
import com.braids.burncoffeeman.common.LevelTileModel;
import com.braids.burncoffeeman.common.PlayerModel;
import com.braids.burncoffeeman.common.Wall;

public class GameManager {

	private static GameManager      singleton;

	private GraphicsTemplateManager gtm;
	private int                     playerId;
	private int                     bombId;
	private List<Player>            lstPlayers;
	private List<Bomb>              lstBomb;
	private LevelModel              levelModel;
	private int                     fullMapSendPos;
	private boolean                 mapSentOnce;
	private boolean                 gfxSentOnce;
	private boolean                 clientWantStartMatch;
	private GamePhase               gamePhase;
	private ByteBuffer              bbOut;

	public GameManager() {
		lstPlayers = new ArrayList<Player>();
		lstBomb = new ArrayList<Bomb>();
		gamePhase = GamePhase.PRE_MATCH;
		bbOut = ByteBuffer.allocate(Constants.MAX_PACKET_SIZE);
		gtm = GraphicsTemplateManager.init();
		gtm.loadAnimOriginals(new File("gfx"));
	}

	public static void init() {
		singleton = new GameManager();
	}

	public static GameManager getInstance() {
		return singleton;
	}

	public void startMatch() {
		generateLevel();

		Timer timerMainCycle = new Timer(true);
		timerMainCycle.scheduleAtFixedRate(new TimerTask() {

			public void run() {
				mainCycle();
			}
		}, Constants.MAIN_CYCLE_PERIOD, Constants.MAIN_CYCLE_PERIOD);
	}

	public LevelModel getLevelModel() {
		return levelModel;
	}

	public void generateLevel() {
		levelModel = new LevelModel(Constants.LEVEL_WIDTH, Constants.LEVEL_HEIGHT);

		for (int y = 0; y < levelModel.getHeight(); y++) {
			for (int x = 0; x < levelModel.getWidth(); x++) {

				boolean ground = true;
				if (((x & 1) == 0 && (y & 1) == 0) || x == 0 || y == 0 || x == levelModel.getWidth() - 1 || y == levelModel.getHeight() - 1) {
					ground = false;
				}

				LevelTileModel ltm = new LevelTileModel();
				ltm.setX(x);
				ltm.setY(y);
				ltm.setItem(Item.NONE);
				ltm.setFire(Fire.NONE);
				if (ground) {

					ltm.setWall(Math.random() > 0.2 ? Wall.GROUND : Wall.BREAKABLE_WALL);
				} else {
					ltm.setWall(Wall.WALL);
				}

				levelModel.setTile(ltm);
			}
		}
		fullMapSendPos = 0;
		mapSentOnce = false;
	}

	private void sendMapSegment() {
		int count;
		if (mapSentOnce) {
			count = Constants.SEND_MAP_SEGMENT_PACKET_SIZE;
		} else {
			count = Constants.SEND_MAP_SEGMENT_BOOST_PACKET_SIZE;
		}

		for (int i = 0; i < count; i++) {
			if (fullMapSendPos >= levelModel.getWidth() * levelModel.getHeight()) {
				fullMapSendPos = 0;
				mapSentOnce = true;
			}
			LevelTileModel ltm = levelModel.getTile(fullMapSendPos % levelModel.getWidth(), fullMapSendPos / levelModel.getWidth());
			bbOut.put(ltm.code());
			fullMapSendPos++;
		}
	}

	public void mainCycle() {
		bbOut.clear();
		switch (gamePhase) {
			case PRE_MATCH:
				preMatchCycle();
				break;
			case STARTING_MATCH:
				startingMatchCycle();
				break;
			case MATCH:
				matchCycle();
				break;
			case GAME_OVER:
				gameOverCycle();
				break;
		}
		sendDataToClient();
	}

	public void preMatchCycle() {
		mapSentOnce = false;
		gfxSentOnce = false;
		if (clientWantStartMatch) {
			clientWantStartMatch = false;
			gamePhase = GamePhase.STARTING_MATCH;
		}
	}

	public void startingMatchCycle() {
		if (!mapSentOnce) {
			sendMapSegment();
		}
		if (!gfxSentOnce) {
			gfxSentOnce = true;

			HashSet<String> setHeadGroups = new HashSet<String>();
			HashSet<String> setBodyGroups = new HashSet<String>();
			HashSet<String> setLegsGroups = new HashSet<String>();
			for (Player player : lstPlayers) {
				setHeadGroups.add(player.getGfxHeadGroup());
				setBodyGroups.add(player.getGfxBodyGroup());
				setLegsGroups.add(player.getGfxLegsGroup());
			}

			for (String groupName : setHeadGroups) {
				sendGfx(groupName, AnimTilePhaseType.HEAD);
			}
			for (String groupName : setBodyGroups) {
				sendGfx(groupName, AnimTilePhaseType.BODY);
			}
			for (String groupName : setLegsGroups) {
				sendGfx(groupName, AnimTilePhaseType.LEGS);
			}
		}

		if (mapSentOnce && gfxSentOnce) {
			gamePhase = GamePhase.MATCH;
		}
	}

	private void sendGfx(String groupName, AnimTilePhaseType phaseType) {
		AnimTileModel atm = new AnimTileModel();
		atm.setGroupName(groupName);
		atm.setPhaseType(phaseType);
		atm.setGfx(gtm.getOriginalImage(groupName, phaseType));
		bbOut.put(atm.code());
	}

	public void matchCycle() {
		for (Player player : lstPlayers) {
			player.cycle();
		}

		for (Bomb bomb : lstBomb) {
			bomb.cycle();
		}

		checkAndHandleBombDetonations();

		sendMapSegment();
		for (Player player : lstPlayers) {
			if (player.isStateChanged()) {
				bbOut.put(player.getCodedModel());
			}
		}
		for (Bomb bomb : lstBomb) {
			if (bomb.isStateChanged()) {
				bbOut.put(bomb.getCodedModel());
			}
		}

		for (int i = lstBomb.size() - 1; i >= 0; i--) {
			Bomb bomb = lstBomb.get(i);
			if (bomb.isDetonated()) {
				if (bomb.getBombOwnerId() != -1) {
					// TODO
					// bombModel.getOwnerPlayer().accumulateableItemQuantitiesMap.put(Items.BOMB,
					// bombModel.getOwnerPlayer().accumulateableItemQuantitiesMap
					// .get(Items.BOMB) + 1);
				}
				lstBomb.remove(i);
			}
		}

	}

	private void checkAndHandleBombDetonations() {
		ArrayList<Bomb> detonatableBombModels = new ArrayList<Bomb>();
		boolean checkedAllBombModels;

		// First we check the fire triggered bombs...
		for (Bomb bomb : lstBomb) {
			LevelTileModel tile = levelModel.getTile(bomb.getComponentPosX(), bomb.getComponentPosY());
			if ((bomb.getPhase() != BombPhases.FLYING) && !bomb.isAboutToDetonate() && tile.hasFire()) {
				bomb.setPhase(BombPhases.ABOUT_TO_DETONATE);
				bomb.setTriggererPlayer(tile.getFireOwnerId());
			}
		}
		do {
			checkedAllBombModels = true;
			// The fact that we didn't check all of 'em will be known if we find
			// one that hasn't been checked out yet.

			for (Bomb bomb : lstBomb) {
				if (!bomb.isDetonated() && bomb.isAboutToDetonate()) {
					bomb.setTriggererPlayer(bomb.getBombOwnerId());
					detonatableBombModels.add(bomb);
					checkedAllBombModels = false;
					break;
				}
			}

			for (int i = 0; i < detonatableBombModels.size(); i++) {
				Bomb detonatedBombModel = detonatableBombModels.get(i);
				int detonatedBombComponentPosX = detonatedBombModel.getComponentPosX();
				int detonatedBombComponentPosY = detonatedBombModel.getComponentPosY();

				LevelTileModel tile = levelModel.getTile(detonatedBombComponentPosX, detonatedBombComponentPosY);
				tile.setFire(Fire.CROSSING);
				tile.setFireOwnerId(detonatedBombModel.getTriggererPlayer());

				for (Direction direction : Direction.values()) {
					for (int range = 1; range < detonatedBombModel.getRange(); range++) {
						if (detonatedBombModel.isExcludedDetonationDirection(direction)) {
							break;
						}

						int componentPosX = detonatedBombComponentPosX + direction.getXMultiplier() * range;
						int componentPosY = detonatedBombComponentPosY + direction.getYMultiplier() * range;

						if ((componentPosX < 0) || (componentPosX > levelModel.getWidth() - 1) || (componentPosY < 0)
						        || (componentPosY > levelModel.getHeight() - 1)) {
							break;
						}
						LevelTileModel levelComponent = levelModel.getTile(componentPosX, componentPosY);

						if (levelComponent.getWall() != Wall.GROUND && levelComponent.getWall() != Wall.BREAKABLE_WALL) {
							break;
						}

						Bomb bombModelAtComponentPos = getBombAtComponentPosition(componentPosX, componentPosY);

						if (bombModelAtComponentPos != null) {
							bombModelAtComponentPos.addExcludedDetonationDirection(direction.getOpposite());
							if (!bombModelAtComponentPos.isDetonated() && !detonatableBombModels.contains(bombModelAtComponentPos)) {
								bombModelAtComponentPos.setTriggererPlayer(detonatedBombModel.getTriggererPlayer());
								detonatableBombModels.add(bombModelAtComponentPos);
							}
							break;
						} else {
							// Now here we can set the fire...
							tile.setFire(direction.getXMultiplier() != 0 ? Fire.HORIZONTAL : Fire.VERTICAL);

							tile.setFireOwnerId(detonatedBombModel.getTriggererPlayer());

							if ((levelComponent.getWall() == Wall.BREAKABLE_WALL)
							        || ((levelComponent.getWall() == Wall.GROUND) && (levelComponent.getItem() != null))) {
								break;
							}
						}
					}
				}

				detonatedBombModel.setPhase(BombPhases.DETONATED);
			}

			detonatableBombModels.clear();

		} while (!checkedAllBombModels);
	}

	private void gameOverCycle() {}

	private void sendDataToClient() {
		if (bbOut.position() > 0) {
			byte[] bbOutArray = Helper.byteBufferToByteArray(bbOut);

			for (Player player : lstPlayers) {
				player.sendToClient(bbOutArray);
			}
		}
	}

	public void addPlayer(Socket socket) throws IOException {
		synchronized (lstPlayers) {
			Player player = new Player(socket, playerId);
			lstPlayers.add(player);
			playerId++;
		}
	}

	public void playerDisconnected(Player player) {
		synchronized (lstPlayers) {

		}
	}

	public boolean isBombAtComponentPosition(int x, int y) {
		return getBombAtComponentPosition(x, y) != null;
	}

	public Bomb getBombAtComponentPosition(int x, int y) {
		for (Bomb b : lstBomb) {
			if (b.getX() / Constants.COMPONENT_SIZE_IN_VIRTUAL == x && b.getY() / Constants.COMPONENT_SIZE_IN_VIRTUAL == y) {
				return b;
			}
		}
		return null;
	}

	public int getNextBombId() {
		bombId++;

		if (bombId > Constants.MAX_BOMB_ID) {
			bombId = 0;
		}

		return bombId;
	}

	public void addBomb(Bomb bomb) {
		lstBomb.add(bomb);
	}

	public void removeBomb(Bomb bomb) {
		lstBomb.remove(bomb);
	}

	public void validateAndSetFlyingTargetPosX(Bomb bomb, final int flyingTargetPosX) {
		LevelModel levelModel = getLevelModel();

		if (flyingTargetPosX < 0) {
			bomb.setFlyingTargetX((levelModel.getWidth() - 1) * Constants.LEVEL_COMPONENT_GRANULARITY + Constants.LEVEL_COMPONENT_GRANULARITY / 2);
		} else if (flyingTargetPosX > levelModel.getWidth() * Constants.LEVEL_COMPONENT_GRANULARITY) {
			bomb.setFlyingTargetX(Constants.LEVEL_COMPONENT_GRANULARITY / 2);
		} else {
			bomb.setFlyingTargetX(flyingTargetPosX);
		}
	}

	public void validateAndSetFlyingTargetPosY(Bomb bomb, final int flyingTargetPosY) {
		LevelModel levelModel = getLevelModel();

		if (flyingTargetPosY < 0) {
			bomb.setFlyingTargetY((levelModel.getHeight() - 1) * Constants.LEVEL_COMPONENT_GRANULARITY + Constants.LEVEL_COMPONENT_GRANULARITY / 2);
		} else if (flyingTargetPosY > levelModel.getHeight() * Constants.LEVEL_COMPONENT_GRANULARITY) {
			bomb.setFlyingTargetY(Constants.LEVEL_COMPONENT_GRANULARITY / 2);
		} else {
			bomb.setFlyingTargetY(flyingTargetPosY);
		}
	}

	public boolean isPlayerAtComponentPositionExcludePlayer(int componentPosX, int componentPosY, PlayerModel model) {
		// TODO Auto-generated method stub
		return false;
	}

	public void clientWantStartMatch() {
		clientWantStartMatch = true;
	}

}
