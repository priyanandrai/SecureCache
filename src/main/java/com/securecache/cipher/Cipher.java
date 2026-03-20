package com.securecache.cipher;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.securecache.main.Constant;
import com.securecache.secureinterface.JumbleFunctionInterface;

public class Cipher {
	private static final int GCM_TAG_LENGTH_BITS = 128;
	private static final int GCM_IV_LENGTH_BYTES = 12;
	private static final int MAX_JUMBLE_STAGES = 3;

	KeyGenerator genrator = new KeyGenerator();
	SecureRandom secureRandom = new SecureRandom();

	public byte[] revealData(byte[] blob, ArrayList<JumbleFunctionInterface> jumbleFunctions)
			throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException,
			InvalidKeyException, NoSuchPaddingException {
		if (blob == null || jumbleFunctions == null || jumbleFunctions.isEmpty()) {
			return null;
		}

		ByteBuffer envelope = ByteBuffer.wrap(blob);
		int version = Byte.toUnsignedInt(envelope.get());
		if (version != Constant.CACHE_VERSION) {
			throw new IllegalArgumentException("Unsupported cipher envelope version: " + version);
		}

		int stageCount = Byte.toUnsignedInt(envelope.get());
		byte[] stageSequence = new byte[stageCount];
		envelope.get(stageSequence);

		int ivLength = Byte.toUnsignedInt(envelope.get());
		byte[] iv = new byte[ivLength];
		envelope.get(iv);

		int payloadLength = envelope.getInt();
		if (payloadLength < 0 || payloadLength > envelope.remaining()) {
			throw new IllegalArgumentException("Invalid payload length in envelope");
		}

		byte[] payload = new byte[payloadLength];
		envelope.get(payload);

		for (int i = stageCount - 1; i >= 0; i--) {
			int functionIndex = Byte.toUnsignedInt(stageSequence[i]);
			payload = jumbleFunctions.get(functionIndex).reassemble(payload);
		}

		ByteBuffer frame = ByteBuffer.wrap(payload);
		int cipherLength = frame.getInt();
		if (cipherLength < 0 || cipherLength > frame.remaining()) {
			throw new IllegalArgumentException("Invalid ciphertext length in payload");
		}

		byte[] cipherText = new byte[cipherLength];
		frame.get(cipherText);

		int hiddenKeyLength = frame.remaining();
		if (hiddenKeyLength != Constant.KEY_LENGTH) {
			throw new IllegalArgumentException("Invalid hidden key length in payload");
		}

		byte[] hiddenKey = new byte[hiddenKeyLength];
		frame.get(hiddenKey);

		byte[] recoveredKey = xorBytes(hiddenKey, sha256(cipherText));
		SecretKeySpec key = genrator.getKey(recoveredKey);

		try {
			javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/GCM/NoPadding");
			cipher.init(javax.crypto.Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
			return cipher.doFinal(cipherText);
		} catch (GeneralSecurityException e) {
			throw new InvalidKeyException("Unable to decrypt payload", e);
		}
	}

	public byte[] protectData(byte[] value, ArrayList<JumbleFunctionInterface> jumbleFunctions)
			throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException,
			InvalidKeyException, NoSuchPaddingException {
		if (value == null || jumbleFunctions == null || jumbleFunctions.isEmpty()) {
			return null;
		}

		SecretKeySpec key = genrator.getKey();
		byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
		secureRandom.nextBytes(iv);

		byte[] cipherText;
		try {
			javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/GCM/NoPadding");
			cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
			cipherText = cipher.doFinal(value);
		} catch (GeneralSecurityException e) {
			throw new InvalidKeyException("Unable to encrypt payload", e);
		}

		byte[] hiddenKey = xorBytes(key.getEncoded(), sha256(cipherText));
		ByteBuffer frame = ByteBuffer.allocate(4 + cipherText.length + hiddenKey.length);
		frame.putInt(cipherText.length);
		frame.put(cipherText);
		frame.put(hiddenKey);
		byte[] payload = frame.array();

		int stageCount = Math.min(MAX_JUMBLE_STAGES, jumbleFunctions.size());
		byte[] stageSequence = new byte[stageCount];
		for (int i = 0; i < stageCount; i++) {
			int functionIndex = secureRandom.nextInt(jumbleFunctions.size());
			stageSequence[i] = (byte) functionIndex;
			payload = jumbleFunctions.get(functionIndex).jumbleData(payload);
		}

		ByteBuffer envelope = ByteBuffer
				.allocate(1 + 1 + stageSequence.length + 1 + iv.length + 4 + payload.length);
		envelope.put((byte) Constant.CACHE_VERSION);
		envelope.put((byte) stageSequence.length);
		envelope.put(stageSequence);
		envelope.put((byte) iv.length);
		envelope.put(iv);
		envelope.putInt(payload.length);
		envelope.put(payload);

		return envelope.array();
	}

	private byte[] sha256(byte[] data) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		return digest.digest(data);
	}

	private byte[] xorBytes(byte[] left, byte[] right) {
		int size = Math.min(left.length, right.length);
		byte[] output = new byte[size];
		for (int i = 0; i < size; i++) {
			output[i] = (byte) (left[i] ^ right[i]);
		}
		return output;
	}
}






