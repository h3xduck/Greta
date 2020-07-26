package com.marsanpat.greta.Database;

import android.util.Log;

import com.marsanpat.greta.Utils.Encryption.CryptoUtils;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Comparator;
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

    @Column
    int priority = 0; //by default

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

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    @Deprecated
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


    @Override
    public String toString(){
        return "Content: "+this.getContent()+"\nID: "+this.getId()+"\nEncrypted?: "+this.isEncrypted()+"\nLastMod: "+this.getLastModification().toString()
                +"\nPriority: "+this.priority+"\n";
    }

    /*public void setNoteData(NoteData noteData) {
        this.noteData = noteData;
    }

    public NoteData getNoteData(){
        return this.noteData;
    }*/

    public boolean equals(Object object){
        if (object == null) return false;
        if (object == this) return true;
        if (!(object instanceof Element)) return false;
        Element element = (Element) object;
        return ((Element) object).getId() == this.getId();

    }

}

