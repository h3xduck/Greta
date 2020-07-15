package com.marsanpat.greta.Database;

import android.util.Log;

import com.marsanpat.greta.Utils.Encryption.CryptoUtils;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Date;


@Table(database = MyDatabase.class)
public class Element extends BaseModel {

    @Column
    @PrimaryKey
    long id;

    @Column String content;

    @Column
    Date lastModification;

    @Column
    boolean encrypted = false;

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Date getLastModification() {
        return lastModification;
    }

    public void setLastModification(Date lastModification) {
        this.lastModification = lastModification;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public static boolean isElementEncrypted(long id){
        Element e = SQLite.select()
                .from(Element.class)
                .where(Element_Table.id.is(id))
                .querySingle();
        try{
            return e.isEncrypted();
        }catch(NullPointerException ex){
            return false;
        }
    }

    public static Element searchElement(long id){
        Element e = SQLite.select()
                .from(Element.class)
                .where(Element_Table.id.is(id))
                .querySingle();
        return e;
    }


    /**
     * Returns an element with contents decrypted. Does not save it in the DB
     * @param element
     * @param password
     * @return
     */
    public static Element decryptElement(Element element, String password) throws GeneralSecurityException, UnsupportedEncodingException {
        String ciphertext = element.getContent();
        String salt = CryptoUtils.retrieveSaltFromElement(element.getId());
        AesCbcWithIntegrity.SecretKeys key = CryptoUtils.getKeyFromPasswordAndSalt(password, salt);
        String plaintext = CryptoUtils.decrypt(ciphertext, key);
        Log.d("debug", "Element with id "+element.getId()+ "was decrypted to "+plaintext);

        //We return a new element, we do not want to overwrite the one we were given
        Element elem = new Element();
        elem.setId(element.getId());
        elem.setContent(plaintext);
        elem.setLastModification(element.getLastModification());
        elem.setEncrypted(false);
        return elem;
    }

    @Override
    public String toString(){
        return "Content: "+this.getContent()+"\nID: "+this.getId()+"\nEncrypted?: "+this.isEncrypted()+"\nLastMod: "+this.getLastModification().toString();
    }

}