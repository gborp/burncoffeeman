package com.braids.burncoffeeman.common;

import java.awt.Color;
import java.nio.ByteBuffer;

public class PlayerInfoModel implements CoderDecoder {

	String name;
	Color  color1;
	Color  color2;

	public byte[] code() {
		ByteBuffer bb = ByteBuffer.allocate(1 + 3 + 3 + name.length() * 2);

		bb.put((byte) PacketMessageType.PLAYER_INFO.ordinal());

		bb.put((byte) color1.getRed());
		bb.put((byte) color1.getGreen());
		bb.put((byte) color1.getBlue());
		bb.put((byte) color2.getRed());
		bb.put((byte) color2.getGreen());
		bb.put((byte) color2.getBlue());
		byte[] nameAsBytes = name.getBytes(Constants.UTF_8);
		bb.put((byte) nameAsBytes.length);
		bb.put(nameAsBytes);
		return bb.array();
	}

	public int decode(byte[] bytes, int offset) {
		color1 = new Color(Helper.byteToInt(bytes[offset + 0]), Helper.byteToInt(bytes[offset + 1]), Helper.byteToInt(bytes[offset + 2]));
		color2 = new Color(Helper.byteToInt(bytes[offset + 3]), Helper.byteToInt(bytes[offset + 4]), Helper.byteToInt(bytes[offset + 5]));
		int nameAsBytesSize = Helper.byteToInt(bytes[offset + 6]);
		name = new String(bytes, offset + 7, nameAsBytesSize, Constants.UTF_8);

		return 3 + 3 + 1 + nameAsBytesSize;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Color getColor1() {
		return this.color1;
	}

	public void setColor1(Color color1) {
		this.color1 = color1;
	}

	public Color getColor2() {
		return this.color2;
	}

	public void setColor2(Color color2) {
		this.color2 = color2;
	}

}
