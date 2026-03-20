package com.securecache.secureinterface;

/**
 * This interface defines the methods for jumbling and reassembling data.
 * 
 * @author Amit Rai
 */
public interface JumbleFunctionInterface {

    /**
     * Jumbles the given data.
     *
     * @param data the data to be jumbled
     * @return the jumbled data as a byte array
     */
    byte[] jumbleData(byte[] data);

    /**
     * Reassembles the given jumbled data back to its original form.
     *
     * @param data the jumbled data to be reassembled
     * @return the original data as a byte array
     */
    byte[] reassemble(byte[] data);

    // Backward-compatible alias for older implementations and call sites.
    default byte[] JumbleData(byte[] data) {
        return jumbleData(data);
    }

    // Backward-compatible alias for older implementations and call sites.
    default byte[] Reassbamble(byte[] data) {
        return reassemble(data);
    }
}