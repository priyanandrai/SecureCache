package com.securecache.JumbleFunction;

import com.securecache.secureinterface.JumbleFunctionInterface;

/**
 * F2 – Block Swap: splits the array into two halves and swaps them.
 *   jumbleData  : [A | B] → [B | A]
 *   reassemble  : [B | A] → [A | B]   (inverse aware of differing half sizes)
 */
public class StringLogic implements JumbleFunctionInterface {

	@Override
	public byte[] jumbleData(byte[] data) {
		if (data == null || data.length < 2) return data;
		int firstLen  = data.length / 2;
		int secondLen = data.length - firstLen;
		byte[] result = new byte[data.length];
		System.arraycopy(data, firstLen, result, 0,         secondLen);
		System.arraycopy(data, 0,        result, secondLen, firstLen);
		return result;
	}

	@Override
	public byte[] reassemble(byte[] data) {
		if (data == null || data.length < 2) return data;
		// After jumbleData the layout is [B(secondLen) | A(firstLen)]
		// where firstLen = original/2, secondLen = original - firstLen
		int firstLen  = data.length / 2;          // original A length
		int secondLen = data.length - firstLen;    // original B length
		byte[] result = new byte[data.length];
		System.arraycopy(data, secondLen, result, 0,        firstLen);
		System.arraycopy(data, 0,         result, firstLen, secondLen);
		return result;
	}

}
