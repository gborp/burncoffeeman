package com.braids.burncoffeeman.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.braids.burncoffeeman.common.PlayerInfoModel;
import com.braids.burncoffeeman.common.PlayerModel;

public class Players {

	private HashMap<Integer, PlayerModel>     mapPlayerModel;
	private HashMap<Integer, PlayerInfoModel> mapPlayerInfoModel;

	public Players() {
		mapPlayerModel = new HashMap<Integer, PlayerModel>();
		mapPlayerInfoModel = new HashMap<Integer, PlayerInfoModel>();
	}

	public synchronized void setPlayerModel(PlayerModel data) {
		mapPlayerModel.put(data.getPlayerId(), data);
	}

	public synchronized List<PlayerModel> getPlayerModels() {
		return new ArrayList<PlayerModel>(mapPlayerModel.values());
	}

	public synchronized void setPlayerInfoModel(PlayerInfoModel data) {
		mapPlayerInfoModel.put(data.getPlayerId(), data);
	}

	public synchronized PlayerInfoModel getPlayerInfoModel(Integer id) {
		return mapPlayerInfoModel.get(id);
	}
}
