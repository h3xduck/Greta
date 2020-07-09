package com.marsanpat.greta.Utils.Notes;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.marsanpat.greta.Database.Element;
import com.marsanpat.greta.Database.User;
import com.marsanpat.greta.ui.notes.NotesFragment;

import java.util.Date;

public class NoteManager {

    /**
     * @param input
     * @param id
     * @return 0 on success, -1 on error
     */
    public static int saveNote(String input, long id){
        if(!input.equals("")){
            Element elem = new Element();
            elem.setContent(input);
            if(id==0){
                long time = System.currentTimeMillis();
                elem.setId(time);
            }else{

                elem.setId(id);
            }
            elem.setLastModification(new Date(System.currentTimeMillis()));
            elem.save();
            NotesFragment.newElement = elem;
            Log.d("debug", "Saved id"+elem.getId()+ " content"+elem.getContent());
            return 0;
        }else{
            return -1;
        }
    }
}
