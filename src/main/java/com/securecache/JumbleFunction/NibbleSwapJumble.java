package com.securecache.JumbleFunction;

import com.securecache.secureinterface.JumbleFunctionInterface;

/**
 * F6 – Nibble Swap: swaps the high and low 4-bit nibbles of every byte.
 *   Self-inverse: swapping nibbles twice restores original.
 *   result[i] = ((data[i] & 0x0F) << 4) | ((data[i] & 0xFF) >>> 4)
 */
public class NibbleSwapJumble implements JumbleFunctionInterface {

    @Override
    public byte[] jumbleData(byte[] data) {
        if (data == null) return null;
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            int b = data[i] & 0xFF;
            result[i] = (byte) (((b & 0x0F) << 4) | (b >>> 4));
        }
        return result;
    }

    @Override
    public byte[] reassemble(byte[] data) {
        return jumbleData(data); // self-inverse
    }
}

