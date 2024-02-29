package com.virtualwallet.utils;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
public class AESUtil {
    private static final String AES = "AES";
    private static final byte[] keyValue = new byte[]
            { 'Q', 'M', 'L', 'U', 'T', 'I', 'C', 'H', 'U', 'S', 'H', 'K', 'I', 'A', '5', '6' };

    public static String encrypt(String Data) throws Exception {
        SecretKey key = generateKey();
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        return Base64.getEncoder().encodeToString(encVal);
    }

    public static String decrypt(String encryptedData) throws Exception {
        SecretKey key = generateKey();
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = Base64.getDecoder().decode(encryptedData);
        byte[] decValue = c.doFinal(decodedValue);
        return new String(decValue);
    }

    private static SecretKey generateKey() throws Exception {
        SecretKeySpec key = new SecretKeySpec(keyValue, AES);
        return key;
    }
}
