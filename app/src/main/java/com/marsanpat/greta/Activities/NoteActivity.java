package com.marsanpat.greta.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.marsanpat.greta.Database.Element;
import com.marsanpat.greta.Database.User;
import com.marsanpat.greta.Database.User_Table;
import com.marsanpat.greta.R;
import com.marsanpat.greta.ui.gallery.GalleryFragment;
import com.raizlabs.android.dbflow.sql.language.SQLite;

public class NoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        String userName = getIntent().getStringExtra("User Name");
        final User user = queryUser(userName);

        Button saveBut = findViewById(R.id.saveBut);
        saveBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText text = (EditText)findViewById(R.id.inputNote);
                saveNote(view, text, user);
                //If we pressed the button from this specific fragment, the adapter needs to be updated so that changes appear instantly
                //TODO this is a bit hacky
                try{
                    GalleryFragment.contents.add(calculatePreview(text.getText().toString()));
                    GalleryFragment.adapter.notifyDataSetChanged();
                }catch(NullPointerException ex){
                    //We were not in the GalleryFragment, no update was performed
                }

            }
        });

        Button exitBut = findViewById(R.id.exitBut);
        exitBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
    }

    public void saveNote(View view, EditText text, User user){
        String input = text.getText().toString();
        if(!input.equals("")){
            Element elem = new Element();
            elem.setName(input);
            elem.setUser(user);
            long time = System.currentTimeMillis();
            elem.setId(time);
            elem.save();
            Snackbar.make(view, "Note saved", Snackbar.LENGTH_LONG)
                    .show();
        }else{
            Snackbar.make(view, "Please write something before saving", Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    public User queryUser(String name){
        User user = SQLite.select()
                .from(User.class)
                .where(User_Table.name.is(name))
                .querySingle();

        if(user!=null){
            return user;
        }else{
            Log.e("Error","User could not be found");
            //TODO: throw new UserNotFoundException;
            return null;
        }
    }

    private String calculatePreview(String txt){
        if(txt.length()>GalleryFragment.MAXIMUM_PREVIEW_LENGTH){
            return txt.substring(0,16)+"...";
        }
        return txt;
    }
}
