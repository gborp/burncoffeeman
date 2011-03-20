package com.braids.burncoffeeman.common.transfer;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.Map;

import com.braids.burncoffeeman.common.Constants;

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

			Map<Field, TransferType> mapTransferTypes = CoderDecoderHelper.getFieldsWithTransferTypes(instance);
			for (Field f : instance.getClass().getDeclaredFields()) {
				f.setAccessible(true);

				TransferType transferType = mapTransferTypes.get(f);

				if (iByteForB > 0 && transferType != TransferType.BOOLEAN) {
					cb.putByte(byteForBooleans);
					iByteForB = 0;
				}

				switch (transferType) {
					case BOOLEAN:
						if (f.getBoolean(instance)) {
							byteForBooleans |= 1 << iByteForB;
						}
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
					case COLOR:
						Color color = (Color) f.get(instance);
						cb.putByte(color.getRed());
						cb.putByte(color.getGreen());
						cb.putByte(color.getBlue());
						break;
					case STRING:
						byte[] nameAsBytes = ((String) f.get(instance)).getBytes(Constants.UTF_8);
						cb.putByte((byte) nameAsBytes.length);
						cb.putBytes(nameAsBytes);
						break;
					default:
						throw new RuntimeException("Unimplemented class: " + f.getType().getName());
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

	public int getOffset() {
		return bb.position();
	}

	public void putByte(int value) {
		bb.put((byte) value);
	}

	public void putBytes(byte[] value) {
		bb.put(value);
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
