package com.braids.burncoffeeman.common;

import java.nio.ByteBuffer;

public class BombModel implements CoderDecoder {

	int          id;
	int          x;
	int          y;
	byte         z;
	BombType     type;
	byte         bombOwnerId;
	private byte range;

	public byte[] code() {
		ByteBuffer bb = ByteBuffer.allocate(1 + 10);

		bb.put((byte) PacketMessageType.BOMB.ordinal());

		Helper.putShortIntToBuffer(bb, id);
		Helper.putShortIntToBuffer(bb, x);
		Helper.putShortIntToBuffer(bb, y);
		bb.put(z);
		bb.put((byte) type.ordinal());
		bb.put(bombOwnerId);
		bb.put(range);
		return bb.array();
	}

	public int decode(byte[] bytes, int offset) {
		id = Helper.bytesToInt(bytes[offset + 0], bytes[offset + 1]);
		x = Helper.bytesToInt(bytes[offset + 2], bytes[offset + 3]);
		y = Helper.bytesToInt(bytes[offset + 4], bytes[offset + 5]);
		z = bytes[offset + 6];
		type = BombType.values()[bytes[offset + 7]];
		bombOwnerId = bytes[offset + 8];
		range = bytes[offset + 9];

		return 10;
	}

	public int getX() {
		return this.x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return this.y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return this.z;
	}

	public void setZ(int z) {
		this.z = (byte) z;
	}

	public BombType getType() {
		return this.type;
	}

	public void setType(BombType type) {
		this.type = type;
	}

	public byte getBombOwnerId() {
		return this.bombOwnerId;
	}

	public void setBombOwnerId(int bombOwnerId) {
		this.bombOwnerId = (byte) bombOwnerId;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setRange(int range) {
		this.range = (byte) range;
	}

	public int getRange() {
		return Helper.byteToInt(range);
	}
}
