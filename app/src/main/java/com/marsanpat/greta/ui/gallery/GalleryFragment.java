package com.marsanpat.greta.ui.gallery;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.marsanpat.greta.Element;
import com.marsanpat.greta.R;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        showResults(root);
        Log.d("debug","called");
        return root;
    }

    private void showResults(View root){
        LinearLayout ll = root.findViewById(R.id.linearlay);
        List<Element> elem = SQLite.select()
                .from(Element.class)
                //.where(Organization_Table.id.is(1))
                .queryList();
        String result ="";
        for(int ii=0; ii<elem.size(); ii++){
            result = result+"\n"+elem.get(ii).getName();
            Log.d("Result", result+ii);
        }
        TextView tv = new TextView(this.getActivity());
        tv.setText(result);
        ll.addView(tv);
    }
}
