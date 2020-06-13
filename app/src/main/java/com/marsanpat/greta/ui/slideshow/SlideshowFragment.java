package com.marsanpat.greta.ui.slideshow;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.marsanpat.greta.Element;
import com.marsanpat.greta.R;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.w3c.dom.Text;

import java.util.List;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        TableLayout mainTable = (TableLayout)root.findViewById(R.id.mainTable);
        initializeTitleRow(root, mainTable);
        includeDBContents(root, mainTable);

        return root;
    }

    private void initializeTitleRow(View root, TableLayout mainTable){
        //This creates dynamically the title row for the mainTable
        TableRow title = new TableRow(this.getContext());
        TextView id = new TextView(this.getContext());
        TextView content = new TextView(this.getContext());
        id.setText(" ID ");
        content.setText(" Element ");
        title.addView(id);
        title.addView(content);
        mainTable.addView(title);
    }

    private void includeDBContents(View root, TableLayout mainTable){
        List<Element> elem = SQLite.select()
                .from(Element.class)
                .queryList();
        for(int ii=0; ii<elem.size(); ii++){
            String resultName = elem.get(ii).getName();
            long resultId = elem.get(ii).getId();
            TableRow tr = new TableRow(this.getContext());
            TextView tv = new TextView(this.getContext());
            tv.setText(""+resultId);
            //tv.setGravity(Gravity.CENTER);
            tr.addView(tv);
            TextView tv2 = new TextView(this.getContext());
            tv2.setText(resultName);
            tv2.setGravity(Gravity.CENTER);
            tr.addView(tv2);
            mainTable.addView(tr);
        }
    }
}
