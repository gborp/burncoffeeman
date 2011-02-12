package com.braids.burncoffeeman.common;

import java.nio.ByteBuffer;

public class AnimTileModel implements CoderDecoder {

	String            groupName;
	AnimTilePhaseType phaseType;
	byte[]            gfx;

	public byte[] code() {
		byte[] nameAsBytes = groupName.getBytes(Constants.UTF_8);

		ByteBuffer bb = ByteBuffer.allocate(1 + 1 + nameAsBytes.length + 1 + 2 + gfx.length);

		bb.put((byte) PacketMessageType.ANIM_TILE_MODEL.ordinal());

		bb.put((byte) nameAsBytes.length);
		bb.put(nameAsBytes);

		bb.put((byte) phaseType.ordinal());

		Helper.putShortIntToBuffer(bb, gfx.length);
		bb.put(gfx);

		return bb.array();
	}

	public int decode(byte[] bytes, int offset) {

		int initialOffset = offset;

		int nameAsBytesSize = Helper.byteToInt(bytes[offset]);
		groupName = new String(bytes, offset + 1, nameAsBytesSize, Constants.UTF_8);
		offset += 1 + nameAsBytesSize;

		phaseType = AnimTilePhaseType.values()[bytes[offset]];
		offset += 1;

		int gfxBytesSize = Helper.bytesToInt(bytes[offset], bytes[offset + 1]);
		gfx = Helper.readByteArray(bytes, offset);
		offset += 2 + gfxBytesSize;

		return offset - initialOffset;
	}

	public String getGroupName() {
		return this.groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public AnimTilePhaseType getPhaseType() {
		return this.phaseType;
	}

	public void setPhaseType(AnimTilePhaseType phaseType) {
		this.phaseType = phaseType;
	}

	public byte[] getGfx() {
		return this.gfx;
	}

	public void setGfx(byte[] gfx) {
		this.gfx = gfx;
	}

}
