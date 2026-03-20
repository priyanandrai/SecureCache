package com.securecache.JumbleFunction;

import com.securecache.secureinterface.JumbleFunctionInterface;

/**
 * F3 – Circular Rotation: rotates bytes left by ROTATION positions.
 *   jumbleData  : rotate left  by ROTATION
 *   reassemble  : rotate right by ROTATION  (exact inverse)
 */
public class CircularRotationJumble implements JumbleFunctionInterface {

    private static final int ROTATION = 3;

    @Override
    public byte[] jumbleData(byte[] data) {
        if (data == null || data.length == 0) return data;
        int n = data.length;
        int r = ROTATION % n;
        if (r == 0) return data.clone();
        byte[] result = new byte[n];
        System.arraycopy(data, r,     result, 0,     n - r);
        System.arraycopy(data, 0,     result, n - r, r);
        return result;
    }

    @Override
    public byte[] reassemble(byte[] data) {
        if (data == null || data.length == 0) return data;
        int n = data.length;
        int r = ROTATION % n;
        if (r == 0) return data.clone();
        // rotate right by r = rotate left by (n - r)
        byte[] result = new byte[n];
        System.arraycopy(data, n - r, result, 0, r);
        System.arraycopy(data, 0,     result, r, n - r);
        return result;
    }
}

