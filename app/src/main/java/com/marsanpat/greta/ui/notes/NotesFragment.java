package com.marsanpat.greta.ui.notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.marsanpat.greta.Activities.MainActivity;
import com.marsanpat.greta.Activities.NoteActivity;
import com.marsanpat.greta.Activities.PasswordActivity;
import com.marsanpat.greta.Database.Element;
import com.marsanpat.greta.Database.Element_Table;
import com.marsanpat.greta.R;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

import static com.marsanpat.greta.Activities.MainActivity.currentUser;

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

        List<Element> elem = SQLite.select()
                .from(Element.class)
                //.where(Organization_Table.id.is(1))
                .where(Element_Table.user_id.is(MainActivity.currentUserId))
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
                Toast.makeText(getContext(), "Clicked: "+contents.get(position), Toast.LENGTH_SHORT).show();
                launchNoteActivity(position);

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
                                                String info = "User: "+toOperate.getUser().getName()+
                                                        "\nLast Modification: "+toOperate.getLastModification().toString();
                                                new AlertDialog.Builder(getContext())
                                                        .setTitle(options[item])
                                                        .setMessage(info)
                                                        .show();
                                                break;
                                            case 3:
                                                //Encryption
                                                launchPasswordActivity(position);

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
                //If this is null, it means that the note is new and not rewriting other.
                removeFromList(lastClickedElement);
            }
            addToList(newElement);

            Log.d("debug", "added element");
        }
        newElement = null;
        lastClickedElement = null;
    }

    private String calculatePreview(String txt){
        if(txt.contains("\n")){
            String first = txt.split("\n")[0];
            if(first.length()>MAXIMUM_PREVIEW_LENGTH){
                first = first.substring(0,MAXIMUM_PREVIEW_LENGTH-4);
            }
            return first+"\n...";
        }
        if(txt.length()>MAXIMUM_PREVIEW_LENGTH){
            return txt.substring(0,MAXIMUM_PREVIEW_LENGTH-4)+"...";
        }
        return txt;
    }

    private void addToList(Element elem){
        //This adds the element to the string list and updates the adapter.
        //We should consider different ordering methods. For now, let's put the new elements on the top of the list
        contents.add(0,calculatePreview(elem.getContent()));
        contentIds.add(0,elem.getId());
        try{
            adapter.notifyDataSetChanged();
        }catch(NullPointerException ex){
            //Occurs when OnCreate is called
        }

    }

    private void removeFromList(Element elem){
        contents.remove(calculatePreview(elem.getContent()));
        contentIds.remove(elem.getId());
        adapter.notifyDataSetChanged();
    }

    private void launchNoteActivity (int arraysPosition){
        Intent intent = new Intent(getContext(), NoteActivity.class);
        intent.putExtra("User Name", currentUser);
        //searching for the element in the db, with the id
        Element element = SQLite.select()
                .from(Element.class)
                .where(Element_Table.id.is(contentIds.get(arraysPosition)))
                .querySingle()
                ;
        intent.putExtra("Initial Text", element.getContent());
        intent.putExtra("ID", element.getId());
        startActivity(intent);

        //This is not optimal, but we will keep track of which element the user clicked
        lastClickedElement = element;
    }

    private void launchPasswordActivity (int arraysPosition){
        Intent intent = new Intent(getContext(), PasswordActivity.class);
        long idToSearch = this.contentIds.get(arraysPosition);
        intent.putExtra("ID", idToSearch);
        startActivityForResult(intent,1);
    }

    // Call Back method  to get the Message form other Activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // Result of PasswordActivity
        if(requestCode==1)
        {
            String message=data.getStringExtra("MESSAGE");

        }
    }

}
