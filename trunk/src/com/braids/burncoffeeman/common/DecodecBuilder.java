package com.braids.burncoffeeman.common;

import java.util.EnumSet;

public class DecodecBuilder {

	private final byte[] bytes;
	private int          offset;
	private final int    initialOffset;

	public DecodecBuilder(byte[] bytes, int offset) {
		this.bytes = bytes;
		this.offset = offset;
		initialOffset = offset;
	}

	public int getByte() {
		int result = bytes[offset] & 0xff;
		offset += 1;
		return result;
	}

	public int getShort() {
		int result = Helper.bytesToInt(bytes[offset], bytes[offset + 1]);
		offset += 2;
		return result;
	}

	public <T extends Enum<T>> T getEnum(Class<T> enumClass) {
		T result = enumClass.getEnumConstants()[bytes[offset]];
		offset += 1;
		return result;
	}

	public <T extends Enum<T>> EnumSet<T> getEnumSet(Class<T> enumType) {
		EnumSet<T> result = EnumSet.noneOf(enumType);

		int noOfEnums = enumType.getEnumConstants().length;
		if (noOfEnums > 31) {
			throw new RuntimeException("Out of bounds");
		}

		int enums = 0;
		while (noOfEnums > 0) {
			enums <<= 8;
			enums += getByte();
			noOfEnums -= 8;
		}

		int index = 1;
		for (T li : enumType.getEnumConstants()) {
			if ((enums & index) != 0) {
				result.add(li);
			}
			index <<= 1;
		}

		return result;
	}

	public int getOffsetIncrement() {
		return offset - initialOffset;
	}

}
