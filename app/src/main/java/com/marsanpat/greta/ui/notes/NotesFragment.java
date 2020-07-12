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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.marsanpat.greta.Activities.EditNoteActivity;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.marsanpat.greta.Activities.MainActivity.currentUser;

public class NotesFragment extends Fragment {

    private static ArrayAdapter<String> adapter;
    private static ArrayList<String> contents = new ArrayList<>();
    private static List<Long> contentIds = new ArrayList<>();
    public static final int MAXIMUM_PREVIEW_LENGTH = 100;
    public static Element newElement = null;
    private static Element lastClickedElement = null;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_notes, container, false);


        FloatingActionButton fab = root.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(root.getContext(), EditNoteActivity.class);
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
                long selectedElementId = contentIds.get(position);
                Element selectedElement = Element.searchElement(selectedElementId);

                Log.d("debug","Clicked: "+selectedElementId);
                rememberLastClickedElement(selectedElement);

                //Two options, the note might be encrypted or not. If it isn't, we launch the activity and let the user modify it. Otherwise we need the password (we only have encrypted garbage)
                if(Element.isElementEncrypted(selectedElementId)){
                    //Note encrypted
                    promptForPassword(getContext(),selectedElement, true, true);
                }else{
                    //Note not encrypted
                    launchNoteActivity(position);
                }

            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                //Get the element to operate with it
                final long elementId = contentIds.get(position);
                //The options are different depending on whether the element is encrypted or not
                String [] options;
                boolean encrypted;
                if(Element.isElementEncrypted(elementId)){
                    options = new String[]{"Edit", "Remove", "More Info", "Decrypt this note"};
                    encrypted = true;
                }else{
                    options = new String[]{"Edit", "Remove", "More Info", "Encrypt this note"};
                    encrypted = false;
                }
                showAlarmDialogForElement(getContext(), elementId, options, encrypted);

                return true; //If you set this to false, the onclick is performed anyways = don't do it
            }
        });

    }

    @Override
    public void onResume() {
        //We need to refresh the list adapter, including new elements, so that the fragment does not need to be re-created in order to show new elements
        super.onResume();
        refreshListView();
    }

    /**
     * Calculates preview of the note, which will be shown to the user
     * @param txt
     * @param isEncrypted
     * @return preview of the note
     */
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

    private void addToList(Element elem){
        //This adds the element to the string list and updates the adapter.
        //We should consider different ordering methods. For now, let's put the new elements on the top of the list
        contents.add(0,calculatePreview(elem.getContent(), elem.isEncrypted()));
        contentIds.add(0,elem.getId());
        try{
            adapter.notifyDataSetChanged();
        }catch(NullPointerException ex){
            Log.w("debug", "error adding to the list the element with id "+elem.getId());
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
        Intent intent = new Intent(getContext(), EditNoteActivity.class);
        intent.putExtra("Initial Text", element.getContent());
        intent.putExtra("ID", element.getId());
        intent.putExtra("password", password);
        startActivityForResult(intent,2);

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

        // Result of EditNoteActivity
        if(requestCode==2){
            if(resultCode == Activity.RESULT_OK){
                refreshListView();
            }
        }
    }

    private void promptForPassword(Context context, final Element element, final boolean keepEncryption, final boolean showNoteActivity){
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
                        Element decryptedElement = Element.decryptElement(element, password);
                        if(!showNoteActivity){
                            //Happens when the user only wants to decrypt the note, and not to show the note edit activity
                            NoteManager.saveNote(decryptedElement.getContent(),decryptedElement.getId(),false);
                            Log.d("debug", "asked not to show the noteactivity");
                            refreshListView();
                           return;
                        }

                        if(keepEncryption){
                            //If we are asked to keep the encryption, we must encrypt the note again before saving
                            launchNoteActivity(decryptedElement, password);
                        }else{
                            launchNoteActivity(decryptedElement, null);
                        }

                    }catch(Exception ex){
                        //Problems during decryption: wrong password or unsupported encoding for the device
                        Toast.makeText(getContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                        //TODO distinguish between the cases
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

    public void showAlarmDialogForElement(final Context context, final long elementId, final String[] options, final boolean encrypted){
        final Element toOperate = SQLite.select()
                .from(Element.class)
                .where(Element_Table.id.is(elementId))
                .querySingle();

        rememberLastClickedElement(toOperate);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Note Options");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int item) { //item is the option selected
                //Decide the message depending on the option selected
                String message="";
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
                        //No need to show any warning
                        String info = "Last Modification: "+toOperate.getLastModification().toString();
                        new AlertDialog.Builder(context)
                                .setTitle(options[item])
                                .setMessage(info)
                                .show();
                        break;
                    case 3:
                        //Encryption or decryption
                        if(encrypted){
                            message = "This message will be permanently decrypted";
                        }else{
                            message = "By encrypting this note, a password will be needed for any modification and its visualization";
                        }
                        break;
                    default:
                        message = "Not implemented";
                }

                //Showing the results, if the last dialog was just a warning
                List<Integer> optionsWithWarnings = Arrays.asList(0,1,3);
                if(optionsWithWarnings.contains(item)) {
                    new AlertDialog.Builder(context)
                            .setTitle(options[item])
                            .setMessage(message)
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //TODO REMOVE
                                    Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (item) {
                                        case 0:
                                            //Edit:
                                            launchNoteActivity(elementId);
                                            break;
                                        case 1:
                                            //Remove
                                            removeFromList(toOperate);
                                            //Remove the element from the DB too
                                            SQLite.delete()
                                                    .from(Element.class)
                                                    .where(Element_Table.id.is(elementId))
                                                    .execute();
                                            break;
                                        case 3:
                                            //Encryption or decryption
                                            if(encrypted){
                                                //We must save the element decrypted once we get the correct password
                                                promptForPassword(context, toOperate, false, false);
                                            }else{
                                                //We must save the element encrypted. We ask for a new password
                                                launchPasswordActivity(elementId);
                                            }
                                            break;
                                    }
                                }
                            })
                            .show();
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void rememberLastClickedElement(Element element){
        //This is not optimal, but we will keep track of which element the user clicked
        lastClickedElement = element;
        Log.d("debug", "the last clicked element was "+element.getContent()+" with id "+element.getId());
    }

    private void refreshListView(){
        if(newElement != null){
            Log.d("debug", "detected a new element with id "+newElement.getId()+ " and content "+newElement.getContent()+" in the DB");
            //A new element is in the db. This is a hacky but fast and easy way to know which one it is
            if(lastClickedElement!=null){
                //If we are here, it means that the note is being overwritten.
                Log.d("debug", "the element with id"+lastClickedElement.getId()+" and content "+lastClickedElement.getContent()+" is being overwritten\n by the element with id "+newElement.getId()+ "and content "+newElement.getId());
                removeFromList(lastClickedElement);
            }
            addToList(newElement);
        }
        Log.d("debug", "app refreshed, new elements set to null");
        Log.d("debug", "right now, the IDs on the list are: "+contentIds.toString());
        Log.d("debug", "and the contents on the list are: "+contents.toString());
        newElement = null;
        lastClickedElement = null;
    }

}
