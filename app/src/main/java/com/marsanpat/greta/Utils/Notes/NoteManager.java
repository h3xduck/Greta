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
    public static int saveNote(String input, long id, boolean isEncrypted){
        if(!input.equals("")){
            Element elem = new Element();
            elem.setContent(input);
            if(id==0){
                Log.d("debug", "saving a NEW element");
                long time = System.currentTimeMillis();
                elem.setId(time);
            }else{
                elem.setId(id);
            }
            elem.setLastModification(new Date(System.currentTimeMillis()));
            elem.setEncrypted(isEncrypted);
            elem.save();
            NotesFragment.newElement = elem;
            Log.d("debug", "Saved id: "+elem.getId()+ " Content: "+elem.getContent());
            return 0;
        }else{
            return -1;
        }
    }

    public static int saveNoteAndEncrypt(String input, long id, String password, boolean isEncrypted){
        if(!input.equals("")){
            Element elem = new Element();

            //We encrypt the input before saving
            Salt salt = SQLite.select()
                    .from(Salt.class)
                    .where(Salt_Table.element_id.is(id))
                    .querySingle();
            //Now let's encrypt the element again.
            AesCbcWithIntegrity.SecretKeys key = CryptoUtils.getKeyFromPasswordAndSalt(password, salt.getSalt());
            String cipherText = CryptoUtils.encrypt(input, key);

            elem.setContent(cipherText);
            if(id==0){
                Log.d("debug", "saving a NEW element");
                long time = System.currentTimeMillis();
                elem.setId(time);
            }else{
                elem.setId(id);
            }
            //We need to update the element which the salt references
            salt.setElement(elem);
            elem.setLastModification(new Date(System.currentTimeMillis()));
            elem.setEncrypted(isEncrypted);
            elem.save();
            NotesFragment.newElement = elem;
            Log.d("debug", "Saved id: "+elem.getId()+ " Content: "+elem.getContent());
            return 0;
        }else{
            return -1;
        }
    }

    public static int deleteEverything(){
        if(deleteAllElements()==-1){
            return -1;
        }
        if(deleteAllSalts()==-1){
            return -1;
        }

        return 0;
    }

    public static int deleteAllSalts(){
        try{
            Delete.tables(Salt.class);
        }catch(Exception ex) {
            return -1;
        }
        return 0;
    }

    public static int deleteAllElements(){
        try{
            Delete.tables(Element.class);
        }catch(Exception ex) {
            return -1;
        }
        return 0;
    }

}
