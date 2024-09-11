package com.securecache.utility;

public class Utils {

	public static int getRandomNumber(int min, int max) {
		return (int) (((Math.random() * (max - min)) + min));
	}
	
	public static byte[] combineAllArray(byte[]... arrays) {
		try {
			
		
		if(arrays == null)
			return null;

		int length = 0;
		for (byte[] array : arrays) {
			length += array.length;
		}
		byte[] result = new byte[length];
		int pos = 0;
		for (byte[] array : arrays) {
			System.arraycopy(array, 0, result, pos, array.length);
			pos += array.length;
		}
		return result;
		} catch (Exception e) {
			return null;
		}
	}
}
