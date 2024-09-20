package com.securecache.Loader;

import java.io.Serializable;

/**
 * 
 * @author Amit rai 
 *
 * @param <T> this is value return by call back function for storing value with respect with Key 
 */
public abstract class SourcesLoader<Key, Value> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6139476498857336494L;

	/**
	 * 
	 * @param Value return value regarding the key to Store
	 * @return
	 */
	public abstract Value loadValue(Key k);
}
