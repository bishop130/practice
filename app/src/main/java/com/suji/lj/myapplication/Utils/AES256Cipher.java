package com.suji.lj.myapplication.Utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES256Cipher {

    //public static String secretKey = "PBKDF2WithHmacSHA1";

    public static byte[] ivByte = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    public static String AES_Encode(String str,byte[] secretKey) throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidAlgorithmParameterException {


        byte[] textBytes = str.getBytes("UTF-8");
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivByte);
        SecretKeySpec newKey = new SecretKeySpec(secretKey, "AES");
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);


        return Base64.encodeToString(cipher.doFinal(textBytes), 0);
    }


    public static String AES_Decode(String str,byte[] secretKey) throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        byte[] textBytes = Base64.decode(str,0);
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivByte);
        SecretKeySpec newKey = new SecretKeySpec(secretKey,"AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE,newKey,ivSpec);

        return new String(cipher.doFinal(textBytes),"UTF-8");



    }

    public static byte[] secretKeyGenerate() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        generator.init(256, secureRandom);

        return generator.generateKey().getEncoded();
    }


}
