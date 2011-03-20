package com.braids.burncoffeeman.common;

import com.braids.burncoffeeman.common.transfer.CodecBuilder;
import com.braids.burncoffeeman.common.transfer.DecodecBuilder;
import com.braids.burncoffeeman.common.transfer.SuppressDecode;
import com.braids.burncoffeeman.common.transfer.Transfer;
import com.braids.burncoffeeman.common.transfer.TransferType;

public class ClientInputModel implements CoderDecoder {

	@SuppressDecode
	@Transfer(TransferType.AUTO)
	PacketMessageType packetMessageType = PacketMessageType.CLIENT_INPUT;
	@Transfer(TransferType.AUTO)
	boolean           upPress;
	@Transfer(TransferType.AUTO)
	boolean           downPress;
	@Transfer(TransferType.AUTO)
	boolean           leftPress;
	@Transfer(TransferType.AUTO)
	boolean           rightPress;
	@Transfer(TransferType.AUTO)
	boolean           action1Press;
	@Transfer(TransferType.AUTO)
	boolean           action2Press;

	public byte[] code() {
		return CodecBuilder.auto(this);
	}

	public int decode(byte[] bytes, int offset) {
		return DecodecBuilder.auto(this, bytes, offset);
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
