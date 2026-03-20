package com.securecache.JumbleFunction;

import com.securecache.secureinterface.JumbleFunctionInterface;

public class Stringmagic  implements JumbleFunctionInterface{

	@Override
	public byte[] jumbleData(byte[] data) {
		 return data;
	}

	@Override
	public byte[] reassemble(byte[] data) {
		 return data;
	}

}
