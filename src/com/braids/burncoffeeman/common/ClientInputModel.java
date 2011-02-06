package com.braids.burncoffeeman.common;

public class ClientInputModel implements CoderDecoder {

	boolean upPress;
	boolean downPress;
	boolean leftPress;
	boolean rightPress;
	boolean action1Press;
	boolean action2Press;

	public byte[] code() {
		byte[] bytes = new byte[2];

		byte b1 = (byte) ((upPress ? 1 : 0) + (downPress ? 2 : 0) + (leftPress ? 4 : 0) + (rightPress ? 8 : 0) + (action1Press ? 16 : 0) + (action2Press ? 32
		        : 0));

		bytes[0] = (byte) PacketMessageType.CLIENT_INPUT.ordinal();
		bytes[1] = b1;
		return bytes;
	}

	public int decode(byte[] bytes, int offset) {
		byte b1 = bytes[offset];
		upPress = (b1 & 1) != 0;
		downPress = (b1 & 2) != 0;
		leftPress = (b1 & 4) != 0;
		rightPress = (b1 & 8) != 0;
		action1Press = (b1 & 16) != 0;
		action2Press = (b1 & 32) != 0;

		return 1;
	}

	public boolean isUpPress() {
		return this.upPress;
	}

	public void setUpPress(boolean upPress) {
		this.upPress = upPress;
	}

	public boolean isDownPress() {
		return this.downPress;
	}

	public void setDownPress(boolean downPress) {
		this.downPress = downPress;
	}

	public boolean isLeftPress() {
		return this.leftPress;
	}

	public void setLeftPress(boolean leftPress) {
		this.leftPress = leftPress;
	}

	public boolean isRightPress() {
		return this.rightPress;
	}

	public void setRightPress(boolean rightPress) {
		this.rightPress = rightPress;
	}

	public boolean isAction1Press() {
		return this.action1Press;
	}

	public void setAction1Press(boolean action1Press) {
		this.action1Press = action1Press;
	}

	public boolean isAction2Press() {
		return this.action2Press;
	}

	public void setAction2Press(boolean action2Press) {
		this.action2Press = action2Press;
	}

}
