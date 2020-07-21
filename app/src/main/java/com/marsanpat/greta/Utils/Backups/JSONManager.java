package com.marsanpat.greta.Utils.Backups;

import android.util.Log;

import androidx.core.util.Pair;

import com.marsanpat.greta.Database.Element;
import com.marsanpat.greta.Database.Salt;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class JSONManager {

    public JSONObject createJSONBackup() throws JSONException{
        JSONObject backup = new JSONObject();

        //First, let's export the elements table
        List<Element> elementsList = SQLite.select().from(Element.class).queryList();
        JSONArray elementsBackup = new JSONArray();
        for(int ii = 0; ii<elementsList.size(); ii++) {
            JSONObject object = new JSONObject();
            Element elem = elementsList.get(ii);
            object.put("Content", elem.getContent());
            object.put("ID", elem.getId());
            object.put("IsEncrypted", elem.isEncrypted());
            object.put("LastMod", elem.getLastModification().getTime());
            elementsBackup.put(object.toString());
            Log.d("debug", "Exported element: "+object.toString());
        }

        //Then we export the salts table
        List<Salt> saltsList = SQLite.select().from(Salt.class).queryList();
        JSONArray saltsBackup = new JSONArray();

        try{
            for(int ii = 0; ii<saltsList.size(); ii++) {
                JSONObject object = new JSONObject();
                Salt salt = saltsList.get(ii);
                object.put("ElementID", salt.getElement().getId());
                object.put("Salt", salt.getSalt());
                saltsBackup.put(object.toString());
            }
        }catch (NullPointerException ex){
            //No problems here, probably there saltslist is empty because there are no encrypted elements.
            Log.w("debug", "Something went wrong with the salt for an element..");
        }


        backup.put("ElementsTable:", elementsBackup);
        backup.put("SaltsTable:", saltsBackup);
        return backup;
    }



    public Pair<List<Element>, List<Salt>> extractJSONBackup(String jsonString)throws JSONException{
        List<Element> elementList = new ArrayList<Element>();
        List<Salt> saltList = new ArrayList<Salt>();

        JSONObject jsonObject  = new JSONObject(jsonString);
        JSONArray elementsTable = jsonObject.getJSONArray("ElementsTable:");
        JSONArray saltsTable = jsonObject.getJSONArray("SaltsTable:");

        Log.d("debug", "arrays extracted correctly: "+elementsTable.toString()+ "\nand: "+saltsTable.toString());

        for(int ii=0; ii<elementsTable.length(); ii++){
            String objectstr = elementsTable.getString(ii);
            JSONObject object = new JSONObject(objectstr);
            Log.d("debug", "extracted jsonobject: "+object.toString());
            Element element = new Element();
            element.setContent(object.getString("Content"));
            element.setId(object.getLong("ID"));
            element.setEncrypted(object.getBoolean("IsEncrypted"));
            element.setLastModification(new Date((object.getLong("LastMod"))));
            elementList.add(element);
        }

        for(int ii=0; ii<saltsTable.length(); ii++){
            String objectstr = saltsTable.getString(ii);
            JSONObject object = new JSONObject(objectstr);
            Log.d("debug", "extracted jsonobject: "+object.toString());
            Salt salt = new Salt();
            salt.setSalt(object.getString("Salt"));

            //We need to search in the previous list for the full info of the element. If we do not find it, we throw exception
            long elementID = object.getLong("ElementID");
            boolean result = false;
            for(int jj=0; jj<elementList.size(); jj++){
                Element elem = elementList.get(jj);
                if(elem.getId() == elementID) {
                    salt.setElement(elem);
                    result=true;
                }else if(jj==elementList.size()-1&&!result){
                    //If we are here and we still didn't find the element, something went really wrong at some point.
                    //Probably the json was manually modified
                    throw new JSONException("Malformed JSON: Does not respect database foreign keys");
                }
            }
            saltList.add(salt);
        }
        return new Pair(elementList, saltList);

    }
}
