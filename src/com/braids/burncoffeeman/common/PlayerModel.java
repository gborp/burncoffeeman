package com.braids.burncoffeeman.common;

import java.nio.ByteBuffer;
import java.util.EnumSet;

public class PlayerModel implements CoderDecoder {

	int                      playerId;
	int                      x;
	int                      y;
	Direction                direction;
	Activity                 activity;
	int                      speed;
	int                      vitality;
	boolean                  isHurted;
	boolean                  isHurt;
	boolean                  isMagic;
	private EnumSet<Disease> disease;

	public PlayerModel() {
		disease = EnumSet.noneOf(Disease.class);
	}

	public byte[] code() {
		ByteBuffer bb = ByteBuffer.allocate(1 + 2 + 2 + 7);

		bb.put((byte) PacketMessageType.PLAYER_MODEL.ordinal());

		Helper.putShortIntToBuffer(bb, x);
		Helper.putShortIntToBuffer(bb, y);
		bb.put((byte) direction.ordinal());
		bb.put((byte) activity.ordinal());
		bb.put((byte) speed);
		bb.put((byte) vitality);
		bb.put((byte) playerId);

		byte effects = (byte) ((isHurted ? 1 : 0) + (isHurt ? 2 : 0) + (isMagic ? 4 : 0));

		bb.put(effects);

		byte diseaseByte = 0;
		for (Disease d : disease) {
			switch (d) {
				case BOMB_SHITTING:
					diseaseByte |= 1;
					break;
				case NO_BOMB:
					diseaseByte |= 2;
					break;
				case COLOR_BLIND:
					diseaseByte |= 4;
					break;
				case FAST_DETONATION:
					diseaseByte |= 8;
					break;
				case RABBIT:
					diseaseByte |= 16;
					break;
				case REVERSE:
					diseaseByte |= 32;
					break;
				case SHORT_RANGE:
					diseaseByte |= 64;
					break;
				case SLOW_MOTION:
					diseaseByte |= 128;
					break;
			}
		}
		bb.put(diseaseByte);

		if (bb.position() != bb.capacity()) {
			throw new IllegalStateException("Error in coder: " + bb.position() + " " + bb.capacity());
		}

		return bb.array();
	}

	public int decode(byte[] bytes, int offset) {

		x = Helper.bytesToInt(bytes[offset], bytes[offset + 1]);
		y = Helper.bytesToInt(bytes[offset + 2], bytes[offset + 3]);
		direction = Direction.values()[bytes[offset + 4]];
		activity = Activity.values()[bytes[offset + 5]];
		speed = bytes[offset + 6];
		vitality = bytes[offset + 7];
		playerId = bytes[offset + 8];
		byte effects = bytes[offset + 9];
		isHurted = (effects & 1) != 0;
		isHurt = (effects & 2) != 0;
		isMagic = (effects & 4) != 0;

		byte diseasesByte = bytes[offset + 10];
		disease = EnumSet.noneOf(Disease.class);
		if ((diseasesByte & 1) != 0) {
			disease.add(Disease.BOMB_SHITTING);
		}
		if ((diseasesByte & 2) != 0) {
			disease.add(Disease.NO_BOMB);
		}
		if ((diseasesByte & 4) != 0) {
			disease.add(Disease.COLOR_BLIND);
		}
		if ((diseasesByte & 8) != 0) {
			disease.add(Disease.FAST_DETONATION);
		}
		if ((diseasesByte & 16) != 0) {
			disease.add(Disease.RABBIT);
		}
		if ((diseasesByte & 32) != 0) {
			disease.add(Disease.REVERSE);
		}
		if ((diseasesByte & 64) != 0) {
			disease.add(Disease.SHORT_RANGE);
		}

		if ((diseasesByte & 128) != 0) {
			disease.add(Disease.SLOW_MOTION);
		}

		return 11;
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

}
