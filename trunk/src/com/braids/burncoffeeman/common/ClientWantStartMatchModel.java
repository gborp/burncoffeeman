package com.braids.burncoffeeman.common;

import java.nio.ByteBuffer;

public class ClientWantStartMatchModel implements CoderDecoder {

	String name;

	public byte[] code() {
		byte[] nameAsBytes = name.getBytes(Constants.UTF_8);

		ByteBuffer bb = ByteBuffer.allocate(1 + 1 + nameAsBytes.length);

		bb.put((byte) PacketMessageType.CLIENT_WANT_START_MATCH.ordinal());

		bb.put((byte) nameAsBytes.length);
		bb.put(nameAsBytes);

		return bb.array();
	}

	public int decode(byte[] bytes, int offset) {

		int initialOffset = offset;

		int nameAsBytesSize = Helper.byteToInt(bytes[offset]);
		name = new String(bytes, offset + 1, nameAsBytesSize, Constants.UTF_8);
		offset += 1 + nameAsBytesSize;

		return offset - initialOffset;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
