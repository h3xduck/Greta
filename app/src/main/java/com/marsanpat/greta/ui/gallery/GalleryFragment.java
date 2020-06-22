package com.marsanpat.greta.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
    public static final int MAXIMUM_PREVIEW_LENGTH = 30;
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
                startActivityForResult(intent, 0);

            }
        });

        showResults(root);
        return root;
    }

    private void showResults(View root){
        //Dynamically show stored notes in the db
        ListView lv = root.findViewById(R.id.listyView);

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

    }

    @Override
    public void onResume() {
        super.onResume();
        if(newElement != null){
            //A new element is in the db. This is a hacky but fast and easy way to know which one it is
            contents.add(newElement.getName());
            contentIds.add(newElement.getId());
            newElement = null;
            Log.d("debug", "added element");
        }
    }

    private String calculatePreview(String txt){
        if(txt.length()>MAXIMUM_PREVIEW_LENGTH){
            return txt.substring(0,MAXIMUM_PREVIEW_LENGTH-4)+"...";
        }
        return txt;
    }

}
