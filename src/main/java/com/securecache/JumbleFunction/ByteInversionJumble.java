package com.securecache.JumbleFunction;

import com.securecache.secureinterface.JumbleFunctionInterface;

/**
 * F7 – Byte Inversion: applies bitwise NOT to every byte.
 *   Self-inverse: NOT(NOT(x)) = x
 *   result[i] = ~data[i]
 */
public class ByteInversionJumble implements JumbleFunctionInterface {

    @Override
    public byte[] jumbleData(byte[] data) {
        if (data == null) return null;
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) ~data[i];
        }
        return result;
    }

    @Override
    public byte[] reassemble(byte[] data) {
        return jumbleData(data); // self-inverse
    }
}

