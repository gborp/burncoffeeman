package com.braids.burncoffeeman.common;

import java.nio.ByteBuffer;

public class GfxByteModel implements CoderDecoder {

	public static enum GfxByteModelType {
		ANIM_TILE, BOMB, BURNING, FIRE, ITEMS, WALL
	}

	private static final byte UNKNOWN_PHASE_TYPE = 100;

	GfxByteModelType          type;
	String                    groupName;
	BodyPart         phaseType;
	byte[]                    gfx;

	public byte[] code() {
		byte[] nameAsBytes;
		if (groupName == null) {
			nameAsBytes = new byte[0];
		} else {
			nameAsBytes = groupName.getBytes(Constants.UTF_8);
		}

		ByteBuffer bb = ByteBuffer.allocate(1 + 1 + 1 + nameAsBytes.length + 1 + 2 + gfx.length);

		bb.put((byte) PacketMessageType.GFX_BYTE_MODEL.ordinal());

		bb.put((byte) type.ordinal());

		bb.put((byte) nameAsBytes.length);
		if (nameAsBytes.length > 0) {
			bb.put(nameAsBytes);
		}

		if (phaseType != null) {
			bb.put((byte) phaseType.ordinal());
		} else {
			bb.put(UNKNOWN_PHASE_TYPE);
		}

		Helper.putShortIntToBuffer(bb, gfx.length);
		bb.put(gfx);

		return bb.array();
	}

	public int decode(byte[] bytes, int offset) {

		int initialOffset = offset;

		type = GfxByteModelType.values()[bytes[offset]];
		offset++;

		int nameAsBytesSize = Helper.byteToInt(bytes[offset]);
		if (nameAsBytesSize > 0) {
			groupName = new String(bytes, offset + 1, nameAsBytesSize, Constants.UTF_8);
		}
		offset += 1 + nameAsBytesSize;

		if (bytes[offset] != UNKNOWN_PHASE_TYPE) {
			phaseType = BodyPart.values()[bytes[offset]];
		}
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

	public BodyPart getPhaseType() {
		return this.phaseType;
	}

	public void setPhaseType(BodyPart phaseType) {
		this.phaseType = phaseType;
	}

	public byte[] getGfx() {
		return this.gfx;
	}

	public void setGfx(byte[] gfx) {
		this.gfx = gfx;
	}

	public GfxByteModelType getType() {
		return this.type;
	}

	public void setType(GfxByteModelType type) {
		this.type = type;
	}

}
