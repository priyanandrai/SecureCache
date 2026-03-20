package com.securecache.JumbleFunction;

import com.securecache.secureinterface.JumbleFunctionInterface;

/**
 * F9 – Tri-Block Rotation: splits array into 3 roughly equal blocks [A][B][C]
 *   and rotates them left by one block position.
 *   jumbleData  : [A][B][C] → [B][C][A]
 *   reassemble  : [B][C][A] → [A][B][C]
 *   Arrays shorter than 3 bytes are returned unchanged.
 */
public class TriBlockJumble implements JumbleFunctionInterface {

    @Override
    public byte[] jumbleData(byte[] data) {
        if (data == null || data.length < 3) return data;
        int block1 = data.length / 3;
        int block2 = data.length / 3;
        int block3 = data.length - block1 - block2;
        // [A(block1)][B(block2)][C(block3)] → [B][C][A]
        byte[] result = new byte[data.length];
        System.arraycopy(data, block1,          result, 0,             block2 + block3);
        System.arraycopy(data, 0,               result, block2 + block3, block1);
        return result;
    }

    @Override
    public byte[] reassemble(byte[] data) {
        if (data == null || data.length < 3) return data;
        int block1 = data.length / 3;
        int block2 = data.length / 3;
        int block3 = data.length - block1 - block2;
        // Input is [B(block2)][C(block3)][A(block1)] → recover [A][B][C]
        byte[] result = new byte[data.length];
        System.arraycopy(data, block2 + block3, result, 0,              block1); // A
        System.arraycopy(data, 0,               result, block1,         block2); // B
        System.arraycopy(data, block2,          result, block1 + block2, block3); // C
        return result;
    }
}

