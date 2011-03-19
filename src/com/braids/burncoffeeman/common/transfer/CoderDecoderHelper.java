package com.braids.burncoffeeman.common.transfer;

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class CoderDecoderHelper {

	private static HashMap<Class<?>, Map<Field, TransferType>> mapCache = new HashMap<Class<?>, Map<Field, TransferType>>();

	public static synchronized Map<Field, TransferType> getFieldsWithTransferTypes(Object instance) {

		Map<Field, TransferType> result = mapCache.get(instance.getClass());

		if (result == null) {
			result = new HashMap<Field, TransferType>();

			for (Field f : instance.getClass().getDeclaredFields()) {
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
					result.put(f, transferType);
				}
			}

			mapCache.put(instance.getClass(), result);
		}

		return result;
	}
}
