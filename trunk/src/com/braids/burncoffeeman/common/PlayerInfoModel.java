package com.braids.burncoffeeman.common;

import java.awt.Color;
import java.nio.ByteBuffer;

public class PlayerInfoModel implements CoderDecoder {

	String name;
	Color  color1;
	Color  color2;
	String gfxHeadGroup;
	String gfxBodyGroup;
	String gfxLegsGroup;

	public byte[] code() {
		byte[] nameAsBytes = name.getBytes(Constants.UTF_8);
		byte[] gfxHeadGroupAsBytes = gfxHeadGroup.getBytes(Constants.UTF_8);
		byte[] gfxBodyGroupAsBytes = gfxBodyGroup.getBytes(Constants.UTF_8);
		byte[] gfxLegsGroupAsBytes = gfxLegsGroup.getBytes(Constants.UTF_8);

		ByteBuffer bb = ByteBuffer.allocate(1 + 3 + 3 + 1 + nameAsBytes.length + 1 + gfxHeadGroupAsBytes.length + 1 + gfxBodyGroupAsBytes.length + 1
		        + gfxLegsGroupAsBytes.length);

		bb.put((byte) PacketMessageType.PLAYER_INFO.ordinal());

		bb.put((byte) color1.getRed());
		bb.put((byte) color1.getGreen());
		bb.put((byte) color1.getBlue());
		bb.put((byte) color2.getRed());
		bb.put((byte) color2.getGreen());
		bb.put((byte) color2.getBlue());

		bb.put((byte) nameAsBytes.length);
		bb.put(nameAsBytes);

		bb.put((byte) gfxHeadGroupAsBytes.length);
		bb.put(gfxHeadGroupAsBytes);

		bb.put((byte) gfxBodyGroupAsBytes.length);
		bb.put(gfxBodyGroupAsBytes);

		bb.put((byte) gfxLegsGroupAsBytes.length);
		bb.put(gfxLegsGroupAsBytes);

		return bb.array();
	}

	public int decode(byte[] bytes, int offset) {

		int initialOffset = offset;

		color1 = new Color(Helper.byteToInt(bytes[offset + 0]), Helper.byteToInt(bytes[offset + 1]), Helper.byteToInt(bytes[offset + 2]));
		offset += 3;
		color2 = new Color(Helper.byteToInt(bytes[offset + 0]), Helper.byteToInt(bytes[offset + 1]), Helper.byteToInt(bytes[offset + 2]));
		offset += 3;
		int nameAsBytesSize = Helper.byteToInt(bytes[offset]);
		name = new String(bytes, offset + 1, nameAsBytesSize, Constants.UTF_8);
		offset += 1 + nameAsBytesSize;

		int gfxHeadGroupAsBytesSize = Helper.byteToInt(bytes[offset]);
		gfxHeadGroup = new String(bytes, offset + 1, gfxHeadGroupAsBytesSize, Constants.UTF_8);
		offset += 1 + gfxHeadGroupAsBytesSize;

		int gfxBodyGroupAsBytesSize = Helper.byteToInt(bytes[offset]);
		gfxBodyGroup = new String(bytes, offset + 1, gfxBodyGroupAsBytesSize, Constants.UTF_8);
		offset += 1 + gfxHeadGroupAsBytesSize;

		int gfxLegsGroupAsBytesSize = Helper.byteToInt(bytes[offset]);
		gfxLegsGroup = new String(bytes, offset + 1, gfxLegsGroupAsBytesSize, Constants.UTF_8);
		offset += 1 + gfxLegsGroupAsBytesSize;

		return offset - initialOffset;
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

	public String getGfxHeadGroup() {
		return this.gfxHeadGroup;
	}

	public void setGfxHeadGroup(String gfxHeadGroup) {
		this.gfxHeadGroup = gfxHeadGroup;
	}

	public String getGfxBodyGroup() {
		return this.gfxBodyGroup;
	}

	public void setGfxBodyGroup(String gfxBodyGroup) {
		this.gfxBodyGroup = gfxBodyGroup;
	}

	public String getGfxLegsGroup() {
		return this.gfxLegsGroup;
	}

	public void setGfxLegsGroup(String gfxLegsGroup) {
		this.gfxLegsGroup = gfxLegsGroup;
	}

}
