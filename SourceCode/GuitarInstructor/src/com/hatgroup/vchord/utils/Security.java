package com.hatgroup.vchord.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.hatgroup.vchord.common.Constants;

public class Security {

	public static String keyGeneration(){
		String encStr = encrypt(Constants.PRIVATE_KEY, Constants.PRIVATE_KEY);
		return encStr.substring(0, 16);
	}

	public static String decrypt(String input, String key) {
		byte[] output = null;
		try {
			SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skey);
			output = cipher.doFinal(Base64.decodeBase64(input.getBytes()));
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return new String(output);
	}
	
	public static String encrypt(String input, String key) {
		byte[] crypted = null;
		try {
			SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skey);
			crypted = cipher.doFinal(input.getBytes());
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return new String(Base64.encodeBase64(crypted));
	}

}
