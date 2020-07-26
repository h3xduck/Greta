package com.marsanpat.greta.ui.notes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.marsanpat.greta.Activities.MainActivity;
import com.marsanpat.greta.Database.Element;
import com.marsanpat.greta.R;
import com.marsanpat.greta.Utils.Database.DatabaseManager;


import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {
    private Context context;
    private NotesFragment notesFragment;
    private ArrayList<Element> listContents;
    public static final int MAXIMUM_PREVIEW_LENGTH = 100;

    public ListAdapter(Context context, ArrayList<Element> listItem, NotesFragment notesFragment){
        this.context = context;
        this.listContents = listItem;
        this.notesFragment = notesFragment;

    }

    @Override
    public int getCount(){
        return this.listContents.size();
    }

    @Override
    public Element getItem(int position){
        return this.listContents.get(position);
    }

    @Override
    public long getItemId(int position){
        return 0; //
    }

    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView==null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.view_note_item, null);
        }
        final Element element = listContents.get(position);
        TextView content = (TextView)convertView.findViewById(R.id.item_preview);
        ImageButton imageButton = (ImageButton)convertView.findViewById(R.id.item_image);
        ImageButton deleteButton = (ImageButton)convertView.findViewById(R.id.item_delete);

        content.setText(calculatePreview(element.getContent(), element.isEncrypted()));
        if(!element.isEncrypted()){
            imageButton.setImageResource(R.drawable.ic_open_lock);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notesFragment.launchPasswordActivity(element.getId());
                }
            });
        }else{
            imageButton.setImageResource(R.drawable.ic_lock_locked);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notesFragment.promptForPassword(context, element, false, false);
                }
            });
        }
        //If we remove this, then the listView items cannot be clicked
        imageButton.setFocusable(false);
        deleteButton.setFocusable(false);
        imageButton.setFocusableInTouchMode(false);
        deleteButton.setFocusableInTouchMode(false);

        if(MainActivity.RECYCLE_BIN_BUTTON_DEACTIVATED){
            deleteButton.setVisibility(View.GONE);
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Are you sure you want to remove this note?");
                builder.setMessage("This action cannot be undo");
                builder.setNegativeButton("Cancel", null );
                builder.setPositiveButton("Accept", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NotesFragment.removeFromList(element);
                        new DatabaseManager().deleteElement(element.getId());
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });


        return convertView;

    }

    /**
     * Calculates preview of the note, which will be shown to the user
     * @param txt
     * @param isEncrypted
     * @return preview of the note
     */
    private String calculatePreview(String txt, boolean isEncrypted){
        String ENCRYPTED_NOTE_PREVIEW = MainActivity.ENCRYPTED_NOTE_PREVIEW;
        if(isEncrypted) {
            //Log.d("debug", "encryption detected on element with content "+txt);
            return ENCRYPTED_NOTE_PREVIEW;
        }else{
            //No longer used, the textView is set to limit the lines now
            /*if (txt.contains("\n")) {
                String first = txt.split("\n")[0];
                if (first.length() > MAXIMUM_PREVIEW_LENGTH) {
                    first = first.substring(0, MAXIMUM_PREVIEW_LENGTH - 4);
                }
                return first + "\n...";
            }
            if (txt.length() > MAXIMUM_PREVIEW_LENGTH) {
                return txt.substring(0, MAXIMUM_PREVIEW_LENGTH - 4) + "...";
            }*/
            return txt;
        }
    }
}
