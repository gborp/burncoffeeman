package com.braids.burncoffeeman.common;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.ByteBuffer;
import java.util.EnumSet;

public class CodecBuilder {

	private ByteBuffer bb;

	public CodecBuilder() {
		bb = ByteBuffer.allocate(256 * 1024);
	}

	public static byte[] auto(Object instance) {
		try {
			CodecBuilder cb = new CodecBuilder();

			int byteForBooleans = 0;
			int iByteForB = 0;
			Field[] fields = instance.getClass().getDeclaredFields();
			Field.setAccessible(fields, true);
			for (Field f : instance.getClass().getDeclaredFields()) {
				f.setAccessible(true);
				Transfer an = f.getAnnotation(Transfer.class);
				if (f.getAnnotation(Transfer.class) != null) {
					TransferType transferType = an.value();
					if (transferType == TransferType.AUTO) {
						Class<?> type = f.getType();
						if (type.isPrimitive()) {
							if (type.equals(Integer.TYPE)) {
								transferType = TransferType.INT;
							} else if (type.equals(Byte.TYPE)) {
								transferType = TransferType.BYTE;
							} else if (type.equals(Short.TYPE)) {
								transferType = TransferType.SHORT;
							} else if (type.equals(Boolean.TYPE)) {
								transferType = TransferType.BOOLEAN;
							}
						} else if (type.isEnum()) {
							transferType = TransferType.ENUM;
						} else if (type.equals(EnumSet.class)) {
							transferType = TransferType.ENUMSET;
						}
					}

					switch (transferType) {
						case BOOLEAN:
							byteForBooleans <<= 1;
							byteForBooleans |= f.getBoolean(instance) ? 1 : 0;
							iByteForB++;
							if (iByteForB > 7) {
								cb.putByte(byteForBooleans);
								iByteForB = 0;
								byteForBooleans = 0;
							}
							break;
						case BYTE:
							cb.putByte(f.getInt(instance));
							break;
						case ENUM:
							cb.putEnum((Enum<?>) f.get(instance));
							break;
						case ENUMSET:
							Class<?> enumSetClass = (Class<?>) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0];
							cb.putEnumSet((EnumSet<?>) f.get(instance), enumSetClass);
							break;
						case INT:
							cb.putInt(f.getInt(instance));
							break;
						case SHORT:
							cb.putShort(f.getInt(instance));
							break;
						default:
							throw new RuntimeException("Unimplemented class: " + f.getType().getName());
					}
				}
			}

			if (iByteForB > 0) {
				cb.putByte(byteForBooleans);
			}

			return cb.getResult();
		} catch (IllegalArgumentException ex) {
			throw new RuntimeException(ex);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void putByte(int value) {
		bb.put((byte) value);
	}

	public void putShort(int value) {
		bb.putShort((short) value);
	}

	public void putInt(int value) {
		bb.putInt(value);
	}

	public void putEnum(Enum<?> e) {
		bb.put((byte) e.ordinal());
	}

	public <T extends Enum<T>> void putEnumSet(EnumSet<T> enumSet, Class<?> enumType) {

		int noOfEnums = enumType.getEnumConstants().length;
		if (noOfEnums > 31) {
			throw new RuntimeException("Out of bounds");
		}
		int result = 0;
		for (T li : enumSet) {
			result += 2 << li.ordinal();
		}

		while (noOfEnums > 0) {
			bb.put((byte) (result & 0xff));
			noOfEnums -= 8;
			result >>= 8;
		}
	}

	public byte[] getResult() {
		byte[] result = new byte[bb.position()];
		System.arraycopy(bb.array(), 0, result, 0, result.length);
		return result;
	}

}
