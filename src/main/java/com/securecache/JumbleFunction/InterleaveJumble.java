package com.securecache.JumbleFunction;

import com.securecache.secureinterface.JumbleFunctionInterface;

/**
 * F10 – Interleave Halves: splits array into two halves and interleaves bytes.
 *   jumbleData  : [A1,A2,A3, B1,B2,B3] → [A1,B1,A2,B2,A3,B3]
 *   reassemble  : de-interleaves even/odd positions back into two halves
 *   Odd-length arrays assign the extra byte to the first half.
 */
public class InterleaveJumble implements JumbleFunctionInterface {

    @Override
    public byte[] jumbleData(byte[] data) {
        if (data == null || data.length < 2) return data;
        int n     = data.length;
        int half1 = (n + 1) / 2;  // first half (ceiling)
        int half2 = n / 2;         // second half
        byte[] result = new byte[n];
        int idx = 0;
        for (int i = 0; i < half2; i++) {
            result[idx++] = data[i];         // first half byte
            result[idx++] = data[half1 + i]; // second half byte
        }
        if (half1 > half2) {
            result[idx] = data[half2];       // leftover byte from first half
        }
        return result;
    }

    @Override
    public byte[] reassemble(byte[] data) {
        if (data == null || data.length < 2) return data;
        int n     = data.length;
        int half1 = (n + 1) / 2;
        int half2 = n / 2;
        byte[] result = new byte[n];
        for (int i = 0; i < half2; i++) {
            result[i]         = data[i * 2];      // even-indexed → first half
            result[half1 + i] = data[i * 2 + 1]; // odd-indexed  → second half
        }
        if (half1 > half2) {
            result[half2] = data[n - 1];           // last element → middle of first half
        }
        return result;
    }
}

