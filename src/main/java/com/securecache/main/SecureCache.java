package com.securecache.main;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.commons.lang3.SerializationUtils;

import com.securecache.JumbleFunction.StringLogic;
import com.securecache.JumbleFunction.Stringmagic;
import com.securecache.JumbleFunction.Stringtrick;
import com.securecache.JumbleFunction.CircularRotationJumble;
import com.securecache.JumbleFunction.EvenOddJumble;
import com.securecache.JumbleFunction.XorMaskJumble;
import com.securecache.JumbleFunction.NibbleSwapJumble;
import com.securecache.JumbleFunction.ByteInversionJumble;
import com.securecache.JumbleFunction.PairSwapJumble;
import com.securecache.JumbleFunction.TriBlockJumble;
import com.securecache.JumbleFunction.InterleaveJumble;
import com.securecache.JumbleFunction.CaesarByteJumble;
import com.securecache.Loader.SourcesLoader;
import com.securecache.cipher.Cipher;
import com.securecache.dataHandler.TimeBasedHashMap;
import com.securecache.secureinterface.JumbleFunctionInterface;


/**
 * 
 * @author Amit Rai (amitrai5100@gmail.com) 
 * @param <K> the type of keys maintained by Secure Cache
 * @param <V> the type of mapped values
 */
public class SecureCache<Key,Value> {

	/**
	 * 
	 */
	SourcesLoader<Key,Value> sources = null;

	Cipher cipher = new Cipher();
	TimeBasedHashMap<Key, byte[]> hashMap = null;

	ArrayList<JumbleFunctionInterface> jumbleFunctions;

	private static ArrayList<JumbleFunctionInterface> buildDefaultJumbleFunctions() {
		ArrayList<JumbleFunctionInterface> list = new ArrayList<JumbleFunctionInterface>();
		list.add(new Stringmagic());            // F0  – Identity
		list.add(new StringLogic());            // F1  – Block Swap
		list.add(new Stringtrick());            // F2  – Byte Reversal
		list.add(new CircularRotationJumble()); // F3  – Circular Rotation (left by 3)
		list.add(new EvenOddJumble());          // F4  – Even-Odd Permutation
		list.add(new XorMaskJumble());          // F5  – XOR Masking (index-based)
		list.add(new NibbleSwapJumble());       // F6  – Nibble Swap
		list.add(new ByteInversionJumble());    // F7  – Byte Inversion (bitwise NOT)
		list.add(new PairSwapJumble());         // F8  – Adjacent Pair Swap
		list.add(new TriBlockJumble());         // F9  – Tri-Block Rotation
		list.add(new InterleaveJumble());       // F10 – Interleave Halves
		list.add(new CaesarByteJumble());       // F11 – Caesar Byte Shift (shift=83)
		return list;
	}

	/**
	 * @param sources it provide call back to key , if user want load value at run time.
	 */
	private  SecureCache(SourcesLoader<Key,Value> sources) {

		this.sources = sources;
		this.hashMap = new TimeBasedHashMap<>(10000000);
		jumbleFunctions = buildDefaultJumbleFunctions();
	}
	

	/*
	 * Default constructor , default jumble function will use. 
	 */

	public SecureCache() {
		this.hashMap = new TimeBasedHashMap<>(10000000);
		jumbleFunctions = buildDefaultJumbleFunctions();
	}

	/*
	 * @param jumbleFunctions If user want to deciede to user their own function can set here 
	 */
	public void setJumbleFunction(ArrayList<JumbleFunctionInterface> jumbleFunctions){
		this.jumbleFunctions = jumbleFunctions;
	}
	

	public synchronized void put(Key key, Value value) {
		try {
			if(value != null) {
				hashMap.putValue(key, cipher.protectData(SerializationUtils.serialize((Serializable) value), jumbleFunctions));
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
	public synchronized Value get(Key key) {
		try {
			Value value = null;
			byte[] secureText  =  hashMap.getValue(key);
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
						hashMap.putValue(key, cipher.protectData(SerializationUtils.serialize((Serializable) value), jumbleFunctions));
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
	
	/**
	 * 
	 * @param key key to store data in map 
	 * @return will return plain value in respect to Key 
	 * 
	 */
	public synchronized boolean remove(Key key) {
		byte[] data = hashMap.removeValue(key);
		if(data == null) {
			return false;
		}else {
			return true;
		}
	}
	
	public static class SecureCacheBuilder<Key, Value>{
		SourcesLoader< Key, Value> loader;
		
		  public SecureCacheBuilder<Key, Value> Loader(SourcesLoader< Key, Value> loader) {
	            this.loader = loader;
	            
	            return this;
	        }


		public   SecureCache<Key, Value> build() {
			// TODO Auto-generated method stub
			return new SecureCache<Key, Value>(loader);
		}

	}


}


