package com.braids.burncoffeeman.common;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

public class Helper {

	public static int byteToInt(byte b) {
		return (int) b & 0xFF;
	}

	public static int bytesToInt(byte hi, byte lo) {
		return (int) (hi & 0xFF) * 256 + (int) (lo & 0xFF);
	}

	public static void putShortIntToBuffer(ByteBuffer bb, int i) {
		bb.put((byte) (i / 256));
		bb.put((byte) (i & 255));
	}

	public static byte[] readByteArray(byte[] input, int offset) {
		int gfxLegsBytesSize = Helper.bytesToInt(input[offset], input[offset + 1]);
		byte[] result = new byte[gfxLegsBytesSize];
		System.arraycopy(input, offset + 2, result, 0, gfxLegsBytesSize);
		return result;
	}

	public static byte[] byteBufferToByteArray(ByteBuffer bb) {
		byte[] bbOutArray = new byte[bb.position()];
		bb.position(0);
		bb.get(bbOutArray);
		return bbOutArray;
	}

	public static int getCenterOfTileX(int componentX) {
		return (int) (componentX * Constants.COMPONENT_SIZE_IN_VIRTUAL + 0.5 * Constants.COMPONENT_SIZE_IN_VIRTUAL);
	}

	public static int getCenterOfTileY(int componentY) {
		return (int) (componentY * Constants.COMPONENT_SIZE_IN_VIRTUAL + 0.5 * Constants.COMPONENT_SIZE_IN_VIRTUAL);
	}

	public static byte[] getFileAsByteArray(File file) throws IOException {
		BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
		byte[] bytes = new byte[(int) file.length()];
		is.read(bytes);
		is.close();
		return bytes;
	}

	public static BufferedImage loadImageFromByteArray(byte[] bytes) throws IOException {
		ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		BufferedImage image = ImageIO.read(is);
		is.close();
		return image;
	}
}
