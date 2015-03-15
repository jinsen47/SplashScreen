package com.example.splashscreen.webservice;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Builder {
	public static String makeMD5String(String source){
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(source.getBytes());
			byte[] dig = md.digest();
		    return bytesToString(dig);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static final char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',  'e', 'f'};
	
	private static String bytesToString(byte[] source){
		char[] target = new char[source.length*2];
		for (int i = 0; i!=source.length;i++){
			byte b = source[i];
			target[i*2] = hexChars[(b >> 4) & 0x0f];
			target[i*2+1] = hexChars[b & 0x0f];
		}
		return new String(target);
	}
}
