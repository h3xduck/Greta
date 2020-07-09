package com.marsanpat.greta.Database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

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
}