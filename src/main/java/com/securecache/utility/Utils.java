package com.securecache.utility;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility class for various helper methods used throughout the application.
 */
public class Utils {

    /**
     * Generates a random number between the specified minimum and maximum values, inclusive.
     * 
     * @param min The lower bound (inclusive).
     * @param max The upper bound (inclusive).
     * @return A random number between min and max, inclusive.
     */
    public static int getRandomNumber(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    /**
     * Combines multiple byte arrays into a single byte array.
     * 
     * @param arrays Varargs of byte arrays to combine.
     * @return A single byte array that contains all the bytes from the input arrays in order, 
     *         or null if the input arrays are null or any exception occurs.
     */
    public static byte[] combineAllArray(byte[]... arrays) {
        if (arrays == null) {
            return null;
        }

        // Calculate the total length of the combined array
        int length = 0;
        for (byte[] array : arrays) {
            length += array.length;
        }

        // Create a result array of the calculated length
        byte[] result = new byte[length];
        int pos = 0;

        // Copy each array into the result array
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, pos, array.length);
            pos += array.length;
        }

        return result;
    }
}