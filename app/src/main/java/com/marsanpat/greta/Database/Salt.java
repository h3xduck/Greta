package com.marsanpat.greta.Database;


import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;



@Table(database = MyDatabase.class)
public class Salt extends BaseModel {

    @Column @PrimaryKey @ForeignKey(saveForeignKeyModel = false)
    Element element;

    @Column String salt;

    public void setElement(Element element){
        this.element = element;
    }

    public Element getElement(){
        return this.element;
    }

    public void setSalt(String salt){
        this.salt = salt;
    }

    public String getSalt(){
        return this.salt;
    }

}
