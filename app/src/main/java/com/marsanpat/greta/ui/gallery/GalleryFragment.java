package com.marsanpat.greta.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.marsanpat.greta.Activities.MainActivity;
import com.marsanpat.greta.Activities.NoteActivity;
import com.marsanpat.greta.Database.Element;
import com.marsanpat.greta.Database.Element_Table;
import com.marsanpat.greta.R;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

import static com.marsanpat.greta.Activities.MainActivity.currentUser;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private static ArrayAdapter<String> adapter;
    private static ArrayList<String> contents = new ArrayList<>();
    private static List<Long> contentIds = new ArrayList<>();
    public static final int MAXIMUM_PREVIEW_LENGTH = 100;
    public static Element newElement = null;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
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
        ListView lv = root.findViewById(R.id.listyView);
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
            contents.add(calculatePreview(elem.get(ii).getName()));
            contentIds.add(elem.get(ii).getId());
        }

        adapter = new ArrayAdapter<String>(root.getContext(),android.R.layout.simple_list_item_1, contents);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "Clicked: "+contents.get(position), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), NoteActivity.class);
                intent.putExtra("User Name", currentUser);
                //searching for the element in the db, with the id
                Element element = SQLite.select()
                        .from(Element.class)
                        .where(Element_Table.id.is(contentIds.get(position)))
                        .querySingle()
                        ;
                intent.putExtra("Initial Text", element.getName());

                //This is not optimal, but instead of rewriting the note we will delete it and create it again
                SQLite.delete()
                        .from(Element.class)
                        .where(Element_Table.id.is(contentIds.get(position)));
                //TODO needs refactoring
                contents.remove(element.getName());
                contentIds.remove(element.getId());
                startActivity(intent);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if(newElement != null){
            //A new element is in the db. This is a hacky but fast and easy way to know which one it is
            contents.add(calculatePreview(newElement.getName()));
            contentIds.add(newElement.getId());
            newElement = null;
            Log.d("debug", "added element");
        }
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
        //TODO
    }

    private void removeFromList(Element elem){
        //TODO
    }

}
