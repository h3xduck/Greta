package com.marsanpat.greta.Utils.Database;

import com.marsanpat.greta.Database.Element;
import com.marsanpat.greta.Database.Element_Table;
import com.marsanpat.greta.Database.Salt;
import com.marsanpat.greta.Database.Salt_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Date;
import java.util.List;

public class DatabaseManager {

    public void insertElement(Element element){
        element.setLastModification(new Date(System.currentTimeMillis()));
        element.save();
    }

    public void insertElement(long id, String content, boolean encrypted){
        Element element = new Element();
        element.setId(id);
        element.setContent(content);
        element.setEncrypted(encrypted);
        element.setLastModification(new Date(System.currentTimeMillis()));
        element.save();
    }

    public void deleteElement(long id){
        SQLite.delete()
                .from(Element.class)
                .where(Element_Table.id.is(id))
                .execute();
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
}
