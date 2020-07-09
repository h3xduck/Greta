package com.marsanpat.greta.Utils.Encryption;

import android.util.Log;
import android.util.Pair;

import com.marsanpat.greta.Activities.MainActivity;
import com.marsanpat.greta.Database.Element;
import com.marsanpat.greta.Database.Salt;
import com.marsanpat.greta.Database.Salt_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import static com.tozny.crypto.android.AesCbcWithIntegrity.generateKeyFromPassword;
import static com.tozny.crypto.android.AesCbcWithIntegrity.generateSalt;
import static com.tozny.crypto.android.AesCbcWithIntegrity.saltString;

public class CryptoUtils {

    //Implement it from 0 in th future. Using java-aes-crypto library for now.

    public CryptoUtils (){

    }

    public static Pair<AesCbcWithIntegrity.SecretKeys,String> genKeyFromPassword(String passwordInput){
        try{
            String salt = saltString(generateSalt());
            Log.d("debug", "Salt: " + salt);
            AesCbcWithIntegrity.SecretKeys key = generateKeyFromPassword(passwordInput, salt);
            return new Pair<>(key,salt);
        }catch(GeneralSecurityException ex){
            Log.w("debug", ex);
            return null;
        }
    }

    public static String encrypt(String plaintext, AesCbcWithIntegrity.SecretKeys keys){
        try{
            AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = AesCbcWithIntegrity.encrypt(plaintext, keys);
            String ciphertext = cipherTextIvMac.toString();
            return ciphertext;
        }catch(Exception ex){
            Log.w("debug", ex);
            return null;
        }
    }

    public static String decrypt(String cipherText, AesCbcWithIntegrity.SecretKeys keys) throws UnsupportedEncodingException, GeneralSecurityException{
        AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = new AesCbcWithIntegrity.CipherTextIvMac(cipherText);
        String plainText = AesCbcWithIntegrity.decryptString(cipherTextIvMac, keys);
        return plainText;
    }

    public static String retrieveSaltFromElement(long id) throws NullPointerException{
        Salt salt = SQLite.select()
                .from(Salt.class)
                .where(Salt_Table.element_id.is(id))
                .querySingle();
        //Could be null
        return salt.getSalt();
    }

    public static AesCbcWithIntegrity.SecretKeys getKeyFromPasswordAndSalt(String password, String salt){
        try{
            return generateKeyFromPassword(password, salt);
        }catch(GeneralSecurityException ex){
            Log.w("debug", ex);
            return null;
        }
    }


}
