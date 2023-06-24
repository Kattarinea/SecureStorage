package com.example.securestorage2_0;

import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Crypt {
    SecretKeySpec secretKeySpec;
    public Crypt(String pass)
    {
        byte[] keyByte = pass.getBytes();
        secretKeySpec = new SecretKeySpec(keyByte, "DES");
    }

    public String EncryptPass(String text) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher c = Cipher.getInstance("DES");
        c.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] textBytes = c.doFinal(text.getBytes());
        String cipherText = Base64.encodeToString(textBytes,Base64.DEFAULT);
        return cipherText;
    }

    public String DecryptPass(String cipherText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] textBytes = cipher.doFinal(Base64.decode(cipherText,Base64.DEFAULT));
        String text = new String(textBytes);
        return text;
    }

}
