package com.braids.burncoffeeman.client;

import java.util.Collection;
import java.util.HashMap;

import com.braids.burncoffeeman.common.PlayerModel;

public class Players {

	private HashMap<Integer, PlayerModel> mapPlayerModel;

	public Players() {
		mapPlayerModel = new HashMap<Integer, PlayerModel>();
	}

	public synchronized void setPlayerModel(PlayerModel data) {
		mapPlayerModel.put(data.getPlayerId(), data);
	}

	public synchronized Collection<PlayerModel> getPlayerModels() {
		return mapPlayerModel.values();
	}
}
