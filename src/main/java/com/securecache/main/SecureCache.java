package com.securecache.main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.SerializationUtils;

import com.securecache.JumbleFunction.StringLogic;
import com.securecache.JumbleFunction.Stringmagic;
import com.securecache.JumbleFunction.Stringtrick;
import com.securecache.cipher.Cipher;
import com.securecache.secureinterface.JumbleFunctionInterface;
import com.securecache.secureinterface.Sources;

/**
 * 
 * @author Amit Rai (amitrai5100@gmail.com) 
 * @param <K> the type of keys maintained by Secure Cache
 * @param <V> the type of mapped values
 */
public class SecureCache<K,V> {

	/**
	 * 
	 */
	Sources<K,V> sources = null;

	Cipher cipher = new Cipher();

	HashMap<K, byte[]> hashMap = new HashMap<K, byte[]>();

	ArrayList<JumbleFunctionInterface> jumbleFunctions;

	/**
	 * @param sources it provide call back to key , if user want load value at run time.
	 */
	public SecureCache(Sources<K,V> sources) {

		this.sources = sources;

		jumbleFunctions = new ArrayList<JumbleFunctionInterface>() {
			/**
			 * -
			 */
			private static final long serialVersionUID = 1L;

			{
				add(new Stringmagic());
				add(new StringLogic());
				add (new Stringtrick());
			}};
	}

	/*
	 * Default constructor , default jumble function will use. 
	 */

	public SecureCache() {
		jumbleFunctions = new ArrayList<JumbleFunctionInterface>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				add(new Stringmagic());
				add(new StringLogic());
				add (new Stringtrick());
			}};
	}

	/*
	 * @param jumbleFunctions If user want to deciede to user their own function can set here 
	 */
	public void setJumbleFunction(ArrayList<JumbleFunctionInterface> jumbleFunctions){
		this.jumbleFunctions = jumbleFunctions;
	}
	

	public void put(K key, V value) {
		try {
			if(value != null) {
				hashMap.put(key, cipher.protectData(SerializationUtils.serialize((Serializable) value), jumbleFunctions));
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	/**
	 * 
	 * @param key key to store data in map 
	 * @return will return plain value in respect to Key 
	 * 
	 */
	public synchronized V get(K key) {
		try {
			V value = null;
			byte[] secureText  =  hashMap.get(key);
			if(secureText == null) {

				if(sources == null) {
					return null;
				}
				synchronized (sources) {
					if(sources == null) {
						return null;
					}
					value =	 sources.loadValue(key);

					
					//
					if(value != null) {
						System.out.println("t "+ value.getClass().getName());
						hashMap.put(key, cipher.protectData(SerializationUtils.serialize((Serializable) value), jumbleFunctions));
					}

				}

			}else {

				byte[] secureData = cipher.revealData(secureText, jumbleFunctions);
				value = SerializationUtils.deserialize(secureData);

			}

			return  value;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}


	}
}
