package com.marsanpat.greta.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.marsanpat.greta.R;
import com.marsanpat.greta.Utils.Dialog.ToastManager;
import com.marsanpat.greta.Utils.Notes.NoteManager;
import com.marsanpat.greta.ui.notes.NotesFragment;

public class EditNoteActivity extends AppCompatActivity {
    private EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editnote);
        final String startingText = getIntent().getStringExtra("Initial Text");
        final long detectedId = getIntent().getLongExtra("ID", 0);
        final String encryptionPassword = getIntent().getStringExtra("password");

        //If the note is encrypted, we show an image of a locked lock, otherwise we show an open lock
        //By default it is an open lock, we just change it otherwise
        ImageView lockImage = (ImageView)findViewById(R.id.editnote_background);
        if(encryptionPassword!=null){
            lockImage.setImageResource(R.drawable.ic_lock_locked);
        }
        //We make the image a bit more transparent, so that it does not interfere with written text
        //Keep in mind max is 255
        lockImage.setImageAlpha(60);


        text = (EditText)findViewById(R.id.inputNote);
        text.setText(startingText);

        final NoteManager noteManager = new NoteManager();
        final ToastManager toastManager = new ToastManager();
        Button saveBut = findViewById(R.id.saveBut);
        saveBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String input = text.getText().toString();
                if(encryptionPassword!=null){ //Means we need to encrypt the element later
                    if(noteManager.saveNoteAndEncrypt(input, detectedId, encryptionPassword, true)==-1){
                        //error
                        toastManager.showSimpleToast(getApplicationContext(), "Please write something before saving", 1);
                    }else{
                        toastManager.showSimpleToast(getApplicationContext(), "Note saved", 1);

                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                }else{
                    if(noteManager.saveNote(input, detectedId, false)==-1){
                        //error
                        toastManager.showSimpleToast(getApplicationContext(), "Please write something before saving", 1);
                    }else{
                        toastManager.showSimpleToast(getApplicationContext(), "Note saved", 1);

                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                }
            }
        });

        Button exitBut = findViewById(R.id.exitBut);
        exitBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }
}
