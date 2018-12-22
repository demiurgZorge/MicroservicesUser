package com.microservices.user.core.crypto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class AesCrypt {

    byte [] seed;
 
    public AesCrypt(String password) {
        seed = password.getBytes();
    }
 
    public String encrypt(String crypt) throws NoSuchAlgorithmException,
        InvalidKeyException,
        NoSuchPaddingException,
        IllegalBlockSizeException,
        BadPaddingException {
        byte[] rawKey = getRawKey(seed);
        byte[] result = new byte[0];
        result = encrypt(rawKey, crypt.getBytes());
        return toHex(result);
    }

    public String decrypt(String encrypted) throws NoSuchAlgorithmException,
        IllegalBlockSizeException,
        InvalidKeyException,
        NoSuchPaddingException,
        BadPaddingException {
        byte[] rawKey = new byte[0];
        rawKey = getRawKey(seed);
        byte[] enc = toByte(encrypted);
        byte[] result = new byte[0];
        result = AesCrypt.decrypt(rawKey, enc);
        return new String(result);
    }

    protected static byte[] getRawKey(byte[] password) throws NoSuchAlgorithmException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(password);
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }
 
    private static byte[] encrypt(byte[] raw, byte[] clear)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }
 
    protected static byte[] decrypt(byte[] raw, byte[] encrypted)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }
 
    protected static String toHex(byte [] buffer) {
        return DatatypeConverter.printBase64Binary(buffer);
    }
 
    protected static byte[] toByte(String hex) {
        return DatatypeConverter.parseBase64Binary(hex);
    } 
}