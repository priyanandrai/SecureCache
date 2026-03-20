package com.securecache.JumbleFunction;

import com.securecache.secureinterface.JumbleFunctionInterface;

/**
 * F11 – Caesar Byte Shift: adds a fixed shift constant to every byte (mod 256).
 *   jumbleData  : result[i] = (data[i] + SHIFT) mod 256
 *   reassemble  : result[i] = (data[i] - SHIFT + 256) mod 256
 *   SHIFT = 83 is chosen to be non-trivially invertible and coprime with 256.
 */
public class CaesarByteJumble implements JumbleFunctionInterface {

    private static final int SHIFT = 83;

    @Override
    public byte[] jumbleData(byte[] data) {
        if (data == null) return null;
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) ((data[i] & 0xFF) + SHIFT);
        }
        return result;
    }

    @Override
    public byte[] reassemble(byte[] data) {
        if (data == null) return null;
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) ((data[i] & 0xFF) - SHIFT + 256);
        }
        return result;
    }
}

