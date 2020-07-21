package com.marsanpat.greta.Utils.Notes;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentTransaction;

import com.marsanpat.greta.Database.Element;
import com.marsanpat.greta.Database.Salt;
import com.marsanpat.greta.Database.Salt_Table;
import com.marsanpat.greta.Database.User;
import com.marsanpat.greta.R;
import com.marsanpat.greta.Utils.Database.DatabaseManager;
import com.marsanpat.greta.Utils.Encryption.CryptoUtils;
import com.marsanpat.greta.ui.notes.NotesFragment;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.util.Date;

public class NoteManager {

    /**
     * @param input
     * @param id
     * @return 0 on success, -1 on error
     */
    public int saveNote(String input, long id, boolean isEncrypted){
        if(!input.equals("")){
            DatabaseManager databaseManager = new DatabaseManager();
            long idToInsert;
            if(id==0){
                Log.d("debug", "saving a NEW element");
                idToInsert = System.currentTimeMillis();
            }else{
                idToInsert = id;
            }
            Element insertedElement = databaseManager.insertElement(idToInsert, input, isEncrypted);
            NotesFragment.newElement = insertedElement;
            Log.d("debug", "Saved element: "+insertedElement.toString());
            return 0;
        }else{
            return -1;
        }
    }

    public int saveNoteAndEncrypt(String input, long id, String password, boolean isEncrypted){
        if(!input.equals("")){
            DatabaseManager databaseManager = new DatabaseManager();
            //We encrypt the input before saving
            Salt salt = databaseManager.getSingleSalt(id);
            //Now let's encrypt the element again.
            AesCbcWithIntegrity.SecretKeys key = CryptoUtils.getKeyFromPasswordAndSalt(password, salt.getSalt());
            String cipherText = CryptoUtils.encrypt(input, key);

            saveNote(cipherText,id,isEncrypted);
            return 0;
        }else{
            return -1;
        }
    }

    public int deleteEverything(){
        if(deleteAllElements()==-1){
            return -1;
        }
        if(deleteAllSalts()==-1){
            return -1;
        }

        return 0;
    }

    public int deleteAllSalts(){
        try{
            Delete.tables(Salt.class);
        }catch(Exception ex) {
            return -1;
        }
        return 0;
    }

    public int deleteAllElements(){
        try{
            Delete.tables(Element.class);
        }catch(Exception ex) {
            return -1;
        }
        return 0;
    }

}
