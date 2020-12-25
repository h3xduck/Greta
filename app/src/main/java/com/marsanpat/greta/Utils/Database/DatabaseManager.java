package com.marsanpat.greta.Utils.Database;

import android.util.Log;

import com.marsanpat.greta.Database.Element;
import com.marsanpat.greta.Database.Element_Table;
import com.marsanpat.greta.Database.MyDatabase;
import com.marsanpat.greta.Database.Salt;
import com.marsanpat.greta.Database.Salt_Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Date;
import java.util.List;

public class DatabaseManager {

    public void insertElement(Element element){
        element.setLastModification(new Date(System.currentTimeMillis()));
        element.save();
    }

    public Element insertElement(long id, String content, boolean encrypted, int priority){
        Element element = new Element();
        element.setId(id);
        element.setContent(content);
        element.setEncrypted(encrypted);
        element.setLastModification(new Date(System.currentTimeMillis()));
        element.setPriority(priority);
        element.save();
        return element;
    }

    public void deleteElement(long id){
        SQLite.delete()
                .from(Element.class)
                .where(Element_Table.id.is(id))
                .execute();

        //We also need to remove any salt of this element, avoiding unnecessary tuples.
        if(existsSalt(id)){
            deleteSalt(id);
        }
    }

    public Element getSingleElement(long id){
        Element e = SQLite.select()
                .from(Element.class)
                .where(Element_Table.id.is(id))
                .querySingle();
        return e;
    }

    public List<Element> getListOfElement(){
        List<Element> elementList = SQLite.select()
                .from(Element.class)
                .queryList();
        return elementList;
    }

    public boolean existsElement(long id){
        Element e = SQLite.select()
                .from(Element.class)
                .where(Element_Table.id.is(id))
                .querySingle();
        if(e!=null){
            return true;
        }
        return false;
    }

    public void insertSalt(Element element, String saltInput) throws IllegalArgumentException{
        if(existsElement(element.getId())){
            Salt salt = new Salt();
            salt.setElement(element);
            salt.setSalt(saltInput);
            salt.save();
        }else{
            throw new IllegalArgumentException("The element does not meet foreign key requirements");
        }
    }

    public void insertSalt(long idElement, String salt) throws IllegalArgumentException{
        Element element = getSingleElement(idElement);
        if(element==null){
            throw new IllegalArgumentException("The element does not meet foreign key requirements");
        }
        insertSalt(element,salt);
    }

    public void insertSalt(Salt salt){
        salt.save();
    }

    public Salt getSingleSalt(long idElement){
        Salt salt = SQLite.select()
                .from(Salt.class)
                .where(Salt_Table.element_id.is(idElement))
                .querySingle();
        return salt;
    }

    public List<Salt> getListOfSalt(){
        List<Salt> saltList = SQLite.select()
                .from(Salt.class)
                .queryList();
        return saltList;
    }

    public boolean existsSalt(long idElement){
        if(getSingleSalt(idElement)!=null){
            return true;
        }
        return false;
    }

    public void deleteSalt(long idElement){
        SQLite.delete()
                .from(Salt.class)
                .where(Salt_Table.element_id.is(idElement))
                .execute();
    }

    public void deleteSalt(Element element){
        deleteSalt(element.getId());
    }


    public void setElementFavorite(long idElement, int favourite){
        SQLite.update(Element.class)
                .set(Element_Table.priority.eq(favourite))
                .where(Element_Table.id.is(idElement))
                .execute();
        Log.d("debug", "setElementFavorite"+favourite);
    }


}
