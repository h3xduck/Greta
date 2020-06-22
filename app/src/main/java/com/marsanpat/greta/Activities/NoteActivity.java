package com.marsanpat.greta.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.marsanpat.greta.Database.Element;
import com.marsanpat.greta.Database.User;
import com.marsanpat.greta.Database.User_Table;
import com.marsanpat.greta.R;
import com.marsanpat.greta.ui.gallery.GalleryFragment;
import com.marsanpat.greta.ui.gallery.GalleryViewModel;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Date;

public class NoteActivity extends AppCompatActivity {
    private EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        String userName = getIntent().getStringExtra("User Name");
        final String startingText = getIntent().getStringExtra("Initial Text");
        final long detectedId = getIntent().getLongExtra("ID", 0);

        final User user = queryUser(userName);
        text = (EditText)findViewById(R.id.inputNote);
        text.setText(startingText);

        Button saveBut = findViewById(R.id.saveBut);
        saveBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String input = text.getText().toString();
                saveNote(view, input, user, detectedId);
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

    public void saveNote(View view, String input, User user, long id){
        if(!input.equals("")){
            Element elem = new Element();
            elem.setName(input);
            elem.setUser(user);
            if(id==0){
                long time = System.currentTimeMillis();
                elem.setId(time);
            }else{

                elem.setId(id);
            }
            elem.setLastModification(new Date(System.currentTimeMillis()));
            elem.save();
            GalleryFragment.newElement = elem;
            Log.d("debug", "Saved id"+elem.getId()+ " content"+elem.getName()+ " user"+elem.getUser().getName());

            Toast.makeText(getApplicationContext(), "Note saved", Toast.LENGTH_LONG)
                    .show();
            finish();
        }else{
            Toast.makeText(getApplicationContext(), "Please write something before saving", Toast.LENGTH_LONG)
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
