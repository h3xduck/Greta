package com.marsanpat.greta.ui.gallery;

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

import com.marsanpat.greta.Activities.MainActivity;
import com.marsanpat.greta.Database.Element;
import com.marsanpat.greta.Database.Element_Table;
import com.marsanpat.greta.R;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    public static ArrayAdapter<String> adapter;
    public static ArrayList<String> contents;
    public static final int MAXIMUM_PREVIEW_LENGTH = 20;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

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
        for(int ii=0; ii<elem.size(); ii++){
            contents.add(calculatePreview(elem.get(ii).getName()));
        }


        adapter = new ArrayAdapter<String>(root.getContext(),android.R.layout.simple_list_item_1, contents);
        lv.setAdapter(adapter);

    }

    private String calculatePreview(String txt){
        if(txt.length()>MAXIMUM_PREVIEW_LENGTH){
            return txt.substring(0,16)+"...";
        }
        return txt;
    }
}
