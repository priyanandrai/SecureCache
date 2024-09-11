package com.securecache.JumbleFunction;

import com.securecache.secureinterface.JumbleFunctionInterface;

public class Stringtrick implements JumbleFunctionInterface {

	@Override
	public byte[] JumbleData(byte[] array) {
		
		 if (array == null) {
	          return array;
	      }
	      int i = 0;
	      int j = array.length - 1;
	      byte tmp;
	      while (j > i) {
	          tmp = array[j];
	          array[j] = array[i];
	          array[i] = tmp;
	          j--;
	          i++;
	      }
	      
	      return array;
	}

	@Override
	public byte[] Reassbamble(byte[] array) {
		if (array == null) {
			return array;
		}
		int i = 0;
		int j = array.length - 1;
		byte tmp;
		while (j > i) {
			tmp = array[j];
			array[j] = array[i];
			array[i] = tmp;
			j--;
			i++;
		}

		return array;
	}

}
