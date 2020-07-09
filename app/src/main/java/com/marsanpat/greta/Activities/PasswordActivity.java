package com.marsanpat.greta.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.marsanpat.greta.Database.Element;
import com.marsanpat.greta.Database.Element_Table;
import com.marsanpat.greta.Database.Salt;
import com.marsanpat.greta.R;
import com.marsanpat.greta.Utils.Encryption.CryptoUtils;
import com.marsanpat.greta.Utils.Notes.NoteManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

public class PasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    //This is the id of the element selected by the user to be encrypted
        final long detectedId = getIntent().getLongExtra("ID", 0);
        //And now we get the element from the id
        final Element element = SQLite.select()
                .from(Element.class)
                .where(Element_Table.id.is(detectedId))
                .querySingle();

        Button accept = findViewById(R.id.button_accept_password);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //The user clicked to accept that password
                TextView passwordInput = findViewById(R.id.et_password);
                String password = passwordInput.getText().toString();
                if(password.equals("")){return;}

                //First, let's generate a good encryption key from the user password.
                //We receive the key and the salt
                //TODO ENSURE THE PASSWORD STRENGTH
                Pair<AesCbcWithIntegrity.SecretKeys,String> result = CryptoUtils.genKeyFromPassword(password);

                //Now we store the salt
                //Overwrites salt if already exists
                Salt salt = new Salt();
                salt.setSalt(result.second);
                salt.setElement(element);
                salt.save();

                //Now we encrypt the element contents
                String contents = element.getContent();
                String cipherText = CryptoUtils.encrypt(contents,result.first);

                //This cipherText will be stored now instead of the previous plaintext in the DB
                //We simply substitute the new contents in our element
                element.setEncrypted(true);
                NoteManager.saveNote(cipherText,detectedId);


                //Constructing the result intent, it is sent to the notesFragment
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK);
                finish();

            }
        });

        Button exit = findViewById(R.id.cancel_button);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Exit activity
                Intent intent = new Intent();
                setResult(Activity.RESULT_CANCELED);
                finish();
            }


        });
    }

}
