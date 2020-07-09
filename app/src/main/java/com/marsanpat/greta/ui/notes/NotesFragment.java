package com.marsanpat.greta.ui.notes;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.marsanpat.greta.Activities.NoteActivity;
import com.marsanpat.greta.Activities.PasswordActivity;
import com.marsanpat.greta.Database.Element;
import com.marsanpat.greta.Database.Element_Table;
import com.marsanpat.greta.Database.Salt;
import com.marsanpat.greta.Database.Salt_Table;
import com.marsanpat.greta.R;
import com.marsanpat.greta.Utils.Encryption.CryptoUtils;
import com.marsanpat.greta.Utils.Notes.NoteManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import static com.marsanpat.greta.Activities.MainActivity.currentUser;
import static com.marsanpat.greta.Utils.Encryption.CryptoUtils.getKeyFromPasswordAndSalt;

public class NotesFragment extends Fragment {

    private NotesViewModel notesViewModel;
    private static ArrayAdapter<String> adapter;
    private static ArrayList<String> contents = new ArrayList<>();
    private static List<Long> contentIds = new ArrayList<>();
    public static final int MAXIMUM_PREVIEW_LENGTH = 100;
    public static Element newElement = null;
    private static Element lastClickedElement = null;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notesViewModel = ViewModelProviders.of(this).get(NotesViewModel.class);
        root = inflater.inflate(R.layout.fragment_gallery, container, false);


        FloatingActionButton fab = root.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                //notificationsUtils.sendNotificationInDefaultChannel("Title of not","Hey Its working",101);

                Intent intent = new Intent(root.getContext(), NoteActivity.class);
                intent.putExtra("User Name", currentUser);
                intent.putExtra("Initial Text", "");
                startActivity(intent);

            }
        });

        showResults(root);
        return root;
    }

    private void showResults(View root){
        //Dynamically show stored notes in the db
        final ListView lv = root.findViewById(R.id.listyView);
        lv.setClickable(true);

        final List<Element> elem = SQLite.select()
                .from(Element.class)
                .queryList();

        //Obtaining only the content of the elements in the list
        contents = new ArrayList<>();
        contentIds = new ArrayList<>();
        for(int ii=0; ii<elem.size(); ii++){
            addToList(elem.get(ii));
        }

        adapter = new ArrayAdapter<String>(root.getContext(),android.R.layout.simple_list_item_1, contents);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("debug","Clicked: "+contents.get(position));

                //Two options, the note might be encrypted or not. If it isn't, we launch the activity and let the user modify it. Otherwise we need the password (we only have encrypted garbage)
                if(Element.isElementEncrypted(contentIds.get(position))){
                    //Note encrypted
                    promptForPassword(getContext(),Element.searchElement(contentIds.get(position)));
                }else{
                    //Note not encrypted
                    launchNoteActivity(position);
                }

            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final CharSequence [] options = {"Edit", "Remove", "More Info", "Encrypt this note"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Note Options");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, final int item) { //item is the option selected
                        //Decide the message depending on the option selected
                        String message;
                        switch (item){
                            case 0:
                                //Edit
                                message = "Do you want to edit this note?";
                                break;
                            case 1:
                                //Remove
                                message = "Are you sure to remove this note?";
                                break;
                            case 2:
                                //More info
                                message = "Note properties will be displayed";
                                break;
                            case 3:
                                //Encryption
                                message = "By encrypting this note, a password will be needed for any modification and its visualization";
                                break;
                            default:
                                message = "Not implemented";
                        }

                        new AlertDialog.Builder(getContext())
                                .setTitle(options[item])
                                .setMessage(message)
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //TODO REMOVE
                                        Toast.makeText(getContext(),"Cancelled",Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //TODO CHANGE THIS WITH APPROPRIATE MESSAGE
                                        Toast.makeText(getContext(),"Accepted",Toast.LENGTH_SHORT).show();
                                        //Get the element to operate with it
                                        long id = contentIds.get(position);
                                        Element toOperate = SQLite.select()
                                                .from(Element.class)
                                                .where(Element_Table.id.is(id))
                                                .querySingle();

                                        switch (item){
                                            case 0:
                                                //Edit:
                                                launchNoteActivity(position);
                                                break;
                                            case 1:
                                                //Remove
                                                removeFromList(toOperate);
                                                //Remove the element from the DB too
                                                SQLite.delete()
                                                        .from(Element.class)
                                                        .where(Element_Table.id.is(id))
                                                        .execute();
                                                break;
                                            case 2:
                                                //More Info
                                                String info = "Last Modification: "+toOperate.getLastModification().toString();
                                                new AlertDialog.Builder(getContext())
                                                        .setTitle(options[item])
                                                        .setMessage(info)
                                                        .show();
                                                break;
                                            case 3:
                                                //Encryption
                                                launchPasswordActivity(position);
                                                break;
                                        }
                                    }
                                })
                                .show();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

                return true; //If you set this to false, the onclick is performed anyways = don't do it
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if(newElement != null){
            //A new element is in the db. This is a hacky but fast and easy way to know which one it is
            if(lastClickedElement!=null){
                //If we are here, it means that the note is being overwritten.
                removeFromList(lastClickedElement);
            }
            addToList(newElement);

            Log.d("debug", "added element");
        }
        newElement = null;
        lastClickedElement = null;
    }

    private String calculatePreview(String txt, boolean isEncrypted){ 
        String ENCRYPTED_NOTE_PREVIEW = "*************";
        if(isEncrypted) {
            Log.d("debug", "encryption detected on element with content "+txt);
            return ENCRYPTED_NOTE_PREVIEW;
        }else{
            if (txt.contains("\n")) {
                String first = txt.split("\n")[0];
                if (first.length() > MAXIMUM_PREVIEW_LENGTH) {
                    first = first.substring(0, MAXIMUM_PREVIEW_LENGTH - 4);
                }
                return first + "\n...";
            }
            if (txt.length() > MAXIMUM_PREVIEW_LENGTH) {
                return txt.substring(0, MAXIMUM_PREVIEW_LENGTH - 4) + "...";
            }
            return txt;
        }
    }

    private String calculatePreviewWithID(long id) {
        Element elem= SQLite.select()
                .from(Element.class)
                .where(Element_Table.id.is(id))
                .querySingle();
        return calculatePreview(elem.getContent(), elem.isEncrypted());
    }

    private void addToList(Element elem){
        //This adds the element to the string list and updates the adapter.
        //We should consider different ordering methods. For now, let's put the new elements on the top of the list
        contents.add(0,calculatePreview(elem.getContent(), elem.isEncrypted()));
        contentIds.add(0,elem.getId());
        try{
            adapter.notifyDataSetChanged();
        }catch(NullPointerException ex){
            //Occurs when OnCreate is called
        }

    }

    private void removeFromList(Element elem){
        contents.remove(calculatePreview(elem.getContent(), elem.isEncrypted()));
        contentIds.remove(elem.getId());
        adapter.notifyDataSetChanged();
    }

    private void launchNoteActivity (int arraysPosition){
        long id = contentIds.get(arraysPosition);
        launchNoteActivity(id);
    }

    private void launchNoteActivity (long id){

        //searching for the element in the db, with the id
        Element element = SQLite.select()
                .from(Element.class)
                .where(Element_Table.id.is(id))
                .querySingle()
                ;
        launchNoteActivity(element, null);
    }

    private void launchNoteActivity(Element element, @Nullable String password){
        Intent intent = new Intent(getContext(), NoteActivity.class);
        intent.putExtra("Initial Text", element.getContent());
        intent.putExtra("ID", element.getId());
        intent.putExtra("password", password);
        startActivity(intent);

        //If the element was encrypted, we need to encrypt it again after the user has modified it.
        if(element.isEncrypted()){
            //First we look for the salt used to encrypt this element
            Salt salt = SQLite.select()
                    .from(Salt.class)
                    .where(Salt_Table.element_id.is(element.getId()))
                    .querySingle();
            //Now let's encrypt the element again.
            AesCbcWithIntegrity.SecretKeys key = CryptoUtils.getKeyFromPasswordAndSalt(password, salt.getSalt());
            String contents = element.getContent();
            String cipherText = CryptoUtils.encrypt(contents, key);
            NoteManager.saveNote(cipherText, element.getId(), true);
        }

        //This is not optimal, but we will keep track of which element the user clicked
        lastClickedElement = element;
    }

    private void launchPasswordActivity (int arraysPosition){
        long idToSearch = this.contentIds.get(arraysPosition);
        launchPasswordActivity(idToSearch);
    }

    private void launchPasswordActivity (long id){
        Intent intent = new Intent(getContext(), PasswordActivity.class);
        intent.putExtra("ID", id);

        //searching for the element in the db, with the id
        Element element = SQLite.select()
                .from(Element.class)
                .where(Element_Table.id.is(id))
                .querySingle()
                ;
        lastClickedElement = element;
        startActivityForResult(intent,1);
    }

    // Call Back method
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // Result of PasswordActivity
        if(requestCode==1) {
            if(resultCode == Activity.RESULT_OK){

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //The user did not enter any password and cancelled the operation. We do nothing, since we no longer want to encrypt the note
            }
            //If there is no result code, it means that the user just exited the activity without entering the password
        }
    }

    private void promptForPassword(Context context, final Element element){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter encryption password");

        // Set up the input
        final EditText input = new EditText(context);
        // The input is password type
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = input.getText().toString();
                if(password.equals("")){
                    Toast.makeText(getContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show();
                }else{
                    try{
                        Element decryptedElement = decryptElement(element, password);
                        launchNoteActivity(element, password);
                    }catch(Exception ex){
                        //Problems during decryption: wrong password or unsupported encoding for the device
                        Toast.makeText(getContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     * Returns element with contents decrypted. Does not save it in the DB
     * @param element
     * @param password
     * @return
     */
    public Element decryptElement(Element element, String password) throws GeneralSecurityException, UnsupportedEncodingException {
        String ciphertext = element.getContent();
        String salt = CryptoUtils.retrieveSaltFromElement(element.getId());
        AesCbcWithIntegrity.SecretKeys key = CryptoUtils.getKeyFromPasswordAndSalt(password, salt);
        String plaintext = CryptoUtils.decrypt(ciphertext, key);
        Log.d("debug", "Element with id "+element.getId()+ "was decrypted to "+plaintext);

        element.setContent(plaintext);
        return element;
    }
}
