package com.braids.burncoffeeman.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.braids.burncoffeeman.common.BombModel;
import com.braids.burncoffeeman.common.BombType;

public class Bombs {

	private HashMap<Integer, BombModel> mapBombModel;

	public Bombs() {
		mapBombModel = new HashMap<Integer, BombModel>();
	}

	public synchronized void setBombModel(BombModel data) {
		if (data.getType() == BombType.REMOVE) {
			mapBombModel.remove(data.getId());
		} else {
			mapBombModel.put(data.getId(), data);
		}
	}

	public synchronized Collection<BombModel> getBombModels() {
		return new ArrayList<BombModel>(mapBombModel.values());
	}
}
