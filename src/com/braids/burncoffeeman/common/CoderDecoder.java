package com.braids.burncoffeeman.common;

public interface CoderDecoder {

	byte[] code();

	int decode(byte[] bytes, int offset);
}
