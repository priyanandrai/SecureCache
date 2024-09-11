package com.securecache.cipher;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.securecache.main.Constant;
import com.securecache.secureinterface.JumbleFunctionInterface;
import com.securecache.utility.Utils;

public class Cipher {
	KeyGenerator genrator = new KeyGenerator();

	public byte[] revealData(byte[] v,  ArrayList<JumbleFunctionInterface> jumbleFunctions) throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchPaddingException {
		byte[][] part = divideArray(v, 2);
		//TODO Need to validate key Size and Operation , current we are sopporting Key Size of 32(256)
		System.out.println("part "+ new String(part[0]));
		System.out.println("part1 key "+ new String(part[1]));
		
		int keySize = Integer.parseInt(new String(part[0]));
		System.out.println("keySize "+ keySize);
		byte[] encode = new byte[keySize];
		
		 part = divideArray(part[1], 1);
		
		System.out.println("key index  "+ new String(part[0]));
		System.out.println("remainning index  "+ new String(part[1]));
		 
		part = divideArray(part[1], Integer.parseInt(new String(part[0])));
		 
		int KeyStartingIndex = Integer.parseInt(new String(part[0]));
		System.out.println("ket satarting index "+ KeyStartingIndex);
		 part = divideArray(part[1],KeyStartingIndex );
		 
		System.out.println("part "+ new String(part[0]));
		System.out.println("part1 "+ new String(part[1]));
		
		System.out.println("part[1] "+ part[1].length);
		System.out.println("encode" +encode.length);
		System.arraycopy(part[1], 0  , encode, 0  , encode.length);
		
		System.out.println("*****************"+ new String(encode) );
		
		byte[] firstPart = divideArray(part[0], KeyStartingIndex)[0];
		
		System.out.println("part[1] "+ part[1].length);
		System.out.println("KeyStartingIndex + encode.length "+ KeyStartingIndex + encode.length);
		
		byte[] secondPart = divideArray(part[1], 0 + encode.length)[1];
		
		System.out.println("encode "+ new String(encode));
		byte[] finalData = Utils.combineAllArray(firstPart, secondPart);
		System.out.println("finalData "+ new String(finalData));
		System.out.println("finalData "+ finalData.length);
		
		System.out.println("******************"+ new String(encode));
		System.out.println("******************"+ encode.length);

		SecretKeySpec key = genrator.getKey(encode);
		System.out.println("******************"+ finalData);
		 part = divideArray(finalData,1 );
		 
		 System.out.println("part "+ new String(part[0]));
			System.out.println("part1 "+ new String(part[1]));
		 
		byte[] dataTorestore = 	jumbleFunctions.get(Integer.parseInt(new String(part[0]))).Reassbamble(part[1]);
			
		System.out.println("dataTorestore "+ new String(dataTorestore));
		System.out.println("dataTorestore "+ dataTorestore.length);
		javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES");
		cipher.init(javax.crypto.Cipher.DECRYPT_MODE, key);
		byte[] revealText = cipher.doFinal(dataTorestore);

		return revealText;
	} 
	public byte[] protectData(byte[] v, ArrayList<JumbleFunctionInterface> jumbleFunctions) throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchPaddingException {
		try {

			SecretKeySpec key = genrator.getKey();

			javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES");

			cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key);

			byte[] cipherText = cipher.doFinal(v);
			System.out.println("******************"+ new String(cipherText));
			System.out.println("******************"+ cipherText.length);
			
			System.out.println("******************"+ new String(key.getEncoded()));
			System.out.println("******************"+ key.getEncoded().length);

			int index = Utils.getRandomNumber(0, jumbleFunctions.size());//

			byte[] toReturnData =  jumbleFunctions.get(index).JumbleData(cipherText);

			byte [] hbhbytes = (index+"").getBytes();

			System.out.println("toReturnData "+ new String(toReturnData));
			System.out.println("hbhbytes "+ new String(hbhbytes));
			toReturnData = Utils.combineAllArray(hbhbytes,toReturnData);
			System.out.println("toReturnData "+ new String(toReturnData));


			index = Utils.getRandomNumber(0, toReturnData.length);

			return creatMagicData(toReturnData,index, key.getEncoded() , Constant.KeyLength );

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}


	}
	private byte[] creatMagicData(byte[] toReturnData, int index, byte[] encoded, int keyLength) throws Exception{

		byte[][] part = divideArray(toReturnData, index);
		
		byte[] part1 = part[0];
		byte[] part2 = part[1];

		return  Utils.combineAllArray((keyLength+"").getBytes(),((index+"").length()+"").getBytes(),(index+"").getBytes(),part1,encoded, part2);


	} 

	private byte[][] divideArray(byte[] toReturnData, int index){
		byte[][] bs = new byte[2][];
		bs[0] = new byte[index];
		bs[1] = new byte[toReturnData.length-index];
		
		System.arraycopy(toReturnData, 0           , bs[0], 0     , bs[0].length);
		System.arraycopy(toReturnData, bs[0].length, bs[1], 0     , bs[1].length);
		
		return bs;
	}
}






