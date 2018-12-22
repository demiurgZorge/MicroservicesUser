package com.microservices.user.core.crypto;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;

public class CryptoToken {

    public static String get(String... str) {
        StringBuilder builder = new StringBuilder();
        for (String s : str) {
            builder.append(s);
        }
        String sumString = builder.toString();
        return sha256(sumString);
    }
    
    public static  String get(Object... obj) {
        StringBuilder builder = new StringBuilder();
        for (Object s : obj) {
            builder.append(s.toString());
        }
        String sumString = builder.toString();
        return sha256(sumString);
    }
    
	public static String sha256(byte[] input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] sha = digest.digest(input);
			String result = DatatypeConverter.printHexBinary(sha);
			return result;
		} catch (NoSuchAlgorithmException e) {
			throw(new RuntimeException(e));
		}
	}
	
	public static String generateSalt() {
		SecureRandom prng = new SecureRandom();
		return sha256(prng.generateSeed(8));
	}
	
	public static String sha256(String input) {
		try {
			return sha256(input.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw(new RuntimeException(e));
		}
	}
	
	public static String generateSha256Token(String secret, String time, String salt) {
		return sha256(secret + time + salt);
	}
	
	public static String generateSha256Token(String secret, Date date, double salt) {
	    return generateSha256Token(secret, Long.toString(date.getTime()), Double.toString(salt));
	}
}
