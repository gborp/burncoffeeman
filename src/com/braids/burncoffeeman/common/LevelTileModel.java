package com.braids.burncoffeeman.common;

import java.nio.ByteBuffer;

public class LevelTileModel implements CoderDecoder {

	byte x;
	byte y;
	Item item;
	Wall wall;
	Fire fire;
	int  fireOwnerId;

	public byte[] code() {
		ByteBuffer bb = ByteBuffer.allocate(1 + 6);

		bb.put((byte) PacketMessageType.LEVEL_TILE.ordinal());

		bb.put(x);
		bb.put(y);
		bb.put((byte) item.ordinal());
		bb.put((byte) wall.ordinal());
		bb.put((byte) fire.ordinal());
		bb.put((byte) fireOwnerId);
		return bb.array();
	}

	public int decode(byte[] bytes, int offset) {
		x = bytes[offset + 0];
		y = bytes[offset + 1];
		item = Item.values()[bytes[offset + 2]];
		wall = Wall.values()[bytes[offset + 3]];
		fire = Fire.values()[bytes[offset + 4]];
		fireOwnerId = bytes[offset + 5];

		return 6;
	}

	public int getX() {
		return this.x;
	}

	public void setX(int x) {
		this.x = (byte) x;
	}

	public int getY() {
		return this.y;
	}

	public void setY(int y) {
		this.y = (byte) y;
	}

	public Item getItem() {
		return this.item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Wall getWall() {
		return this.wall;
	}

	public void setWall(Wall wall) {
		this.wall = wall;
	}

	public Fire getFire() {
		return this.fire;
	}

	public void setFire(Fire fire) {
		this.fire = fire;
	}

	public int getFireOwnerId() {
		return this.fireOwnerId;
	}

	public void setFireOwnerId(int fireOwnerId) {
		this.fireOwnerId = fireOwnerId;
	}

	public boolean hasFire() {
		return fire != Fire.NONE;
	}

}
