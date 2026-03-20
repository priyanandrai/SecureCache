package com.securecache.JumbleFunction;

import com.securecache.secureinterface.JumbleFunctionInterface;

/**
 * F8 – Adjacent Pair Swap: swaps bytes at positions (2k, 2k+1).
 *   Self-inverse: swapping a pair twice restores original.
 *   Odd-length arrays leave the final byte in place.
 *   Example: [a,b,c,d,e] → [b,a,d,c,e]
 */
public class PairSwapJumble implements JumbleFunctionInterface {

    @Override
    public byte[] jumbleData(byte[] data) {
        if (data == null || data.length < 2) return data;
        byte[] result = data.clone();
        for (int i = 0; i + 1 < result.length; i += 2) {
            byte tmp      = result[i];
            result[i]     = result[i + 1];
            result[i + 1] = tmp;
        }
        return result;
    }

    @Override
    public byte[] reassemble(byte[] data) {
        return jumbleData(data); // self-inverse
    }
}

