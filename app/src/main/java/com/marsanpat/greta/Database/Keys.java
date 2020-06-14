package com.marsanpat.greta.Database;

import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

@Table(database = MyDatabase.class)
public class Keys extends BaseModel {
    @Column
    @PrimaryKey
    String user;

    @Column
    String key;

    public void setUser(String user){
        this.user = user;
    }

    public String getUser(){
        return this.user;
    }

    public void generateKey(){
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(128);
            SecretKey secret = generator.generateKey();
            byte[] binary = secret.getEncoded();
            String text = String.format("%032X", new BigInteger(+1, binary));
            Log.d("debug", text);
            this.key = text;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String getKey(){
        return this.key;
    }

}
