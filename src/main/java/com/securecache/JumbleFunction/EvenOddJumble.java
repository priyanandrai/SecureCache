package com.securecache.JumbleFunction;

import com.securecache.secureinterface.JumbleFunctionInterface;

/**
 * F4 – Even-Odd Permutation: groups even-indexed bytes first, then odd-indexed.
 *   jumbleData  : [a,b,c,d,e,f] → [a,c,e,b,d,f]
 *   reassemble  : [a,c,e,b,d,f] → [a,b,c,d,e,f]
 */
public class EvenOddJumble implements JumbleFunctionInterface {

    @Override
    public byte[] jumbleData(byte[] data) {
        if (data == null || data.length < 2) return data;
        int n = data.length;
        byte[] result = new byte[n];
        int idx = 0;
        for (int i = 0; i < n; i += 2) result[idx++] = data[i];   // even indices
        for (int i = 1; i < n; i += 2) result[idx++] = data[i];   // odd  indices
        return result;
    }

    @Override
    public byte[] reassemble(byte[] data) {
        if (data == null || data.length < 2) return data;
        int n = data.length;
        int evenCount = (n + 1) / 2;   // number of even-indexed bytes
        int oddCount  = n / 2;
        byte[] result = new byte[n];
        for (int i = 0; i < evenCount; i++) result[i * 2]     = data[i];
        for (int i = 0; i < oddCount;  i++) result[i * 2 + 1] = data[evenCount + i];
        return result;
    }
}

