package com.securecache.JumbleFunction;

import com.securecache.secureinterface.JumbleFunctionInterface;

/**
 * F5 – XOR Masking: XORs each byte with its own index (mod 256).
 *   Self-inverse: applying twice restores original data.
 *   result[i] = data[i] XOR (i & 0xFF)
 */
public class XorMaskJumble implements JumbleFunctionInterface {

    @Override
    public byte[] jumbleData(byte[] data) {
        if (data == null) return null;
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) (data[i] ^ (i & 0xFF));
        }
        return result;
    }

    @Override
    public byte[] reassemble(byte[] data) {
        return jumbleData(data); // self-inverse: XOR twice = identity
    }
}

