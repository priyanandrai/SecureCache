package com.securecache.cipher;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.securecache.utility.Utils;

public class KeyGenerator {

	public SecretKeySpec getKey() throws InvalidKeySpecException, NoSuchAlgorithmException {

		PBEKeySpec pbeKeySpec = new PBEKeySpec((Utils.getRandomNumber(100000, 900000)+"").toCharArray(),(Utils.getRandomNumber(100000, 900000)+"").getBytes(), 100, 256);

		SecretKey pbeKey = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(pbeKeySpec);

		SecretKeySpec keySpec =  new SecretKeySpec(pbeKey.getEncoded(), "AES");
		return keySpec;

	}
	
	public SecretKeySpec getKey(byte[] keyData) throws InvalidKeySpecException, NoSuchAlgorithmException {
		return  new SecretKeySpec(keyData, "AES");
	}

}
