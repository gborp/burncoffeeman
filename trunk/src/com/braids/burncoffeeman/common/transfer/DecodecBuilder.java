package com.braids.burncoffeeman.common.transfer;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.EnumSet;
import java.util.Map;

import com.braids.burncoffeeman.common.Constants;
import com.braids.burncoffeeman.common.Helper;

public class DecodecBuilder {

	private final byte[] bytes;
	private int          offset;
	private final int    initialOffset;

	public DecodecBuilder(byte[] bytes, int offset) {
		this.bytes = bytes;
		this.offset = offset;
		initialOffset = offset;
	}

	public static int auto(Object instance, byte[] bytes, int offset) {
		try {
			DecodecBuilder db = new DecodecBuilder(bytes, offset);

			int byteForBooleans = 0;
			int iByteForB = 0;

			Map<Field, TransferType> mapTransferTypes = CoderDecoderHelper.getFieldsWithTransferTypes(instance);
			for (Field f : instance.getClass().getDeclaredFields()) {

				if (f.getAnnotation(SuppressDecode.class) != null) {
					continue;
				}

				f.setAccessible(true);

				TransferType transferType = mapTransferTypes.get(f);
				if (iByteForB > 0 && transferType != TransferType.BOOLEAN) {
					iByteForB = 0;
				}

				switch (transferType) {
					case BOOLEAN:
						if (iByteForB == 0) {
							byteForBooleans = db.getByte();
						}
						f.setBoolean(instance, (byteForBooleans & (1 << iByteForB)) != 0);

						iByteForB++;
						if (iByteForB > 7) {
							iByteForB = 0;
						}
						break;
					case BYTE:
						f.setByte(instance, (byte) db.getByte());
						break;
					case ENUM:
						f.set(instance, db.getEnum((Class<? extends Enum>) f.getGenericType()));
						break;
					case ENUMSET:
						Class<? extends Enum> enumSetClass = (Class<? extends Enum>) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0];
						f.set(instance, db.getEnumSet(enumSetClass));
						break;
					case INT:
						f.setInt(instance, db.getInt());
						break;
					case SHORT:
						f.setShort(instance, (short) db.getShort());
						break;
					case COLOR:
						f.set(instance, new Color(db.getByte(), db.getByte(), db.getByte()));
						break;
					case STRING:
						int valueAsBytesSize = db.getByte();
						String value = new String(db.getBytes(valueAsBytesSize), Constants.UTF_8);
						f.set(instance, value);
						break;
					default:
						throw new RuntimeException("Unimplemented class: " + f.getType().getName());
				}
			}

			return db.getOffsetIncrement();
		} catch (IllegalArgumentException ex) {
			throw new RuntimeException(ex);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
	}

	public int getByte() {
		int result = bytes[offset] & 0xff;
		offset += 1;
		return result;
	}

	public byte[] getBytes(int count) {
		byte[] result = new byte[count];
		System.arraycopy(bytes, offset, result, 0, count);
		offset += count;
		return result;
	}

	public int getShort() {
		int result = Helper.bytesToInt(bytes[offset], bytes[offset + 1]);
		offset += 2;
		return result;
	}

	public int getInt() {
		int result = (int) ((((bytes[offset] & 0xff) << 24) | ((bytes[offset + 1] & 0xff) << 16) | ((bytes[offset + 1] & 0xff) << 8) | ((bytes[offset + 1] & 0xff) << 0)));
		offset += 4;
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
