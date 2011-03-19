package com.braids.burncoffeeman.common;

import java.util.EnumSet;

import com.braids.burncoffeeman.common.transfer.CodecBuilder;
import com.braids.burncoffeeman.common.transfer.DecodecBuilder;
import com.braids.burncoffeeman.common.transfer.SuppressDecode;
import com.braids.burncoffeeman.common.transfer.Transfer;
import com.braids.burncoffeeman.common.transfer.TransferType;

public class PlayerModel implements CoderDecoder {

	@SuppressDecode
	@Transfer(TransferType.AUTO)
	PacketMessageType        packetMessageType = PacketMessageType.PLAYER_MODEL;
	@Transfer(TransferType.BYTE)
	int                      playerId;
	@Transfer(TransferType.SHORT)
	int                      x;
	@Transfer(TransferType.SHORT)
	int                      y;
	@Transfer(TransferType.AUTO)
	Direction                direction;
	@Transfer(TransferType.AUTO)
	Activity                 activity;
	@Transfer(TransferType.BYTE)
	int                      speed;
	@Transfer(TransferType.BYTE)
	int                      vitality;
	@Transfer(TransferType.AUTO)
	boolean                  isHurted;
	@Transfer(TransferType.AUTO)
	boolean                  isHurt;
	@Transfer(TransferType.AUTO)
	boolean                  isMagic;
	@Transfer(TransferType.AUTO)
	private EnumSet<Disease> disease;
	/** divide it with 100. transferred as short-int */
	@Transfer(TransferType.SHORT)
	private int              animationPhase;

	public PlayerModel() {
		disease = EnumSet.noneOf(Disease.class);
		speed = Constants.BOMBERMAN_BASIC_SPEED;
	}

	public byte[] code() {
		return CodecBuilder.auto(this);
	}

	public int decode(byte[] bytes, int offset) {

		return DecodecBuilder.auto(this, bytes, offset);
	}

	public int getPlayerId() {
		return this.playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = (byte) playerId;
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

	public Direction getDirection() {
		return this.direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public int getSpeed() {
		return this.speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getVitality() {
		return this.vitality;
	}

	public void setVitality(int vitality) {
		this.vitality = vitality;
	}

	public boolean isHurted() {
		return this.isHurted;
	}

	public void setHurted(boolean isHurted) {
		this.isHurted = isHurted;
	}

	public boolean isHurt() {
		return this.isHurt;
	}

	public void setHurt(boolean isHurt) {
		this.isHurt = isHurt;
	}

	public boolean isMagic() {
		return this.isMagic;
	}

	public void setMagic(boolean isMagic) {
		this.isMagic = isMagic;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof PlayerModel)) {
			return false;
		}
		PlayerModel other = (PlayerModel) obj;

		return playerId == other.playerId && x == other.x && y == other.y && direction == other.direction && speed == other.speed && vitality == other.vitality
		        && isHurted == other.isHurted && isHurt == other.isHurt && isMagic == other.isMagic && disease.equals(other.disease);
	}

	public Activity getActivity() {
		return this.activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public void setDisease(EnumSet<Disease> disease) {
		this.disease = disease;
	}

	public EnumSet<Disease> getDisease() {
		return disease;
	}

	public boolean hasDisease(Disease d) {
		return this.disease.contains(d);
	}

	public void clearDiseases() {
		disease.clear();
	}

	public void addDisease(Disease d) {
		disease.add(d);
	}

	public int getAnimationPhase() {
		return this.animationPhase;
	}

	public void setAnimationPhase(int animationPhase) {
		this.animationPhase = animationPhase;
	}

}
