package com.securecache.secureinterface;

import java.io.Serializable;

/**
 * 
 * @author Amit rai 
 *
 * @param <T> this is value return by call back function for storing value with respect with Key 
 */
public abstract class Sources<K, V> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6139476498857336494L;

	/**
	 * 
	 * @param v return value regarding the key to Store
	 * @return
	 */
	public abstract V loadValue(K v);
}
