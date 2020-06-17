package com.marsanpat.greta.ui.slideshow;

import android.graphics.Color;
import android.icu.text.RelativeDateTimeFormatter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.marsanpat.greta.Database.Element;
import com.marsanpat.greta.Database.Keys;
import com.marsanpat.greta.Database.User;
import com.marsanpat.greta.R;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

        RadioGroup rg = (RadioGroup) root.findViewById(R.id.radioGroupTable);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch(checkedId)
                {
                    case R.id.radioButtonElements:
                        drawElementsDB_element(root);
                        break;
                    case R.id.radioButtonKeys:
                        drawElementsDB_keys(root);
                        break;
                    case R.id.radioButtonUsers:
                        drawElementsDB_users(root);
                        break;
                }
            }
        });




        return root;
    }

    public void drawElementsDB_element(View root){
        TableLayout mainTable = (TableLayout)root.findViewById(R.id.mainTable);
        mainTable.removeAllViews();
        initializeTitleRow_element(root, mainTable);
        includeDBContents_element(root, mainTable);
    }

    public void drawElementsDB_keys(View root){
        TableLayout mainTable = (TableLayout)root.findViewById(R.id.mainTable);
        mainTable.removeAllViews();
        initializeTitleRow_keys(root, mainTable);
        includeDBContents_keys(root, mainTable);
    }

    public void drawElementsDB_users(View root){
        TableLayout mainTable = (TableLayout)root.findViewById(R.id.mainTable);
        mainTable.removeAllViews();
        initializeTitleRow_user(root, mainTable);
        includeDBContents_user(root, mainTable);
    }

    private void initializeTitleRow_element(View root, TableLayout mainTable){
        //This creates dynamically the title row for the mainTable
        TableRow title = new TableRow(this.getContext());
        TextView id = new TextView(this.getContext());
        TextView content = new TextView(this.getContext());
        TextView user = new TextView(this.getContext());
        id.setText(" ID ");
        id.setBackgroundResource(R.drawable.cell_shape);
        content.setText(" Element ");
        content.setBackgroundResource(R.drawable.cell_shape);
        user.setText(" User ");
        user.setBackgroundResource(R.drawable.cell_shape);
        //Hacky solution to achieve column separation
        id.setPadding(300,0,300,0);
        content.setPadding(300,0,300,0);
        user.setPadding(300,0,300,0);
        title.addView(id);
        title.addView(content);
        title.addView(user);


        mainTable.addView(title);
    }

    private void initializeTitleRow_keys(View root, TableLayout mainTable){
        TableRow title = new TableRow(this.getContext());
        TextView user = new TextView(this.getContext());
        TextView key = new TextView(this.getContext());
        user.setText(" USER ");
        user.setBackgroundResource(R.drawable.cell_shape);
        key.setText(" KEY ");
        key.setBackgroundResource(R.drawable.cell_shape);
        //Hacky solution to achieve column separation
        user.setPadding(300,0,300,0);
        key.setPadding(300,0,300,0);
        title.addView(user);
        title.addView(key);
        mainTable.addView(title);
    }

    private void initializeTitleRow_user(View root, TableLayout mainTable){
        TableRow title = new TableRow(this.getContext());
        TextView id = new TextView(this.getContext());
        TextView user = new TextView(this.getContext());
        id.setText(" ID ");
        id.setBackgroundResource(R.drawable.cell_shape);
        user.setText(" USER ");
        user.setBackgroundResource(R.drawable.cell_shape);
        //Hacky solution to achieve column separation
        id.setPadding(300,0,300,0);
        user.setPadding(300,0,300,0);
        title.addView(id);
        title.addView(user);
        mainTable.addView(title);
    }


    private void includeDBContents_element(View root, TableLayout mainTable){
        List<Element> elem = SQLite.select()
                .from(Element.class)
                .queryList();
        for(int ii=0; ii<elem.size(); ii++){
            String resultName = elem.get(ii).getName();
            long resultId = elem.get(ii).getId();
            User resultUser = elem.get(ii).getUser();
            String resultUserName = resultUser.getName();
            TableRow tr = new TableRow(this.getContext());
            TextView tv = new TextView(this.getContext());
            tv.setBackgroundResource(R.drawable.cell_shape);
            tv.setText(""+resultId);
            //tv.setGravity(Gravity.CENTER);
            tr.addView(tv);
            TextView tv2 = new TextView(this.getContext());
            tv2.setBackgroundResource(R.drawable.cell_shape);
            tv2.setText(resultName);
            //tv2.setGravity(Gravity.CENTER);
            tr.addView(tv2);
            TextView tv3 = new TextView(this.getContext());
            tv3.setBackgroundResource(R.drawable.cell_shape);
            tv3.setText(resultUserName);
            //tv3.setGravity(Gravity.CENTER);
            tr.addView(tv3);
            mainTable.addView(tr);
        }
    }

    private void includeDBContents_keys(View root, TableLayout mainTable){
        List<Keys> elem = SQLite.select()
                .from(Keys.class)
                .queryList();
        for(int ii=0; ii<elem.size(); ii++){
            String resultUserName = elem.get(ii).getUser();
            String resultKey = elem.get(ii).getKey();
            TableRow tr = new TableRow(this.getContext());
            TextView tv = new TextView(this.getContext());
            tv.setBackgroundResource(R.drawable.cell_shape);
            tv.setText(resultUserName);
            //tv.setGravity(Gravity.CENTER);
            tr.addView(tv);
            TextView tv2 = new TextView(this.getContext());
            tv2.setBackgroundResource(R.drawable.cell_shape);
            tv2.setText(resultKey);
            //tv2.setGravity(Gravity.CENTER);
            tr.addView(tv2);
            mainTable.addView(tr);
        }
    }

    private void includeDBContents_user(View root, TableLayout mainTable){
        List<User> elem = SQLite.select()
                .from(User.class)
                .queryList();
        for(int ii=0; ii<elem.size(); ii++){
            long resultId = elem.get(ii).getId();
            String resultUserName = elem.get(ii).getName();
            TableRow tr = new TableRow(this.getContext());
            TextView tv = new TextView(this.getContext());
            tv.setBackgroundResource(R.drawable.cell_shape);
            tv.setText(""+resultId);
            //tv.setGravity(Gravity.CENTER);
            tr.addView(tv);
            TextView tv2 = new TextView(this.getContext());
            tv2.setBackgroundResource(R.drawable.cell_shape);
            tv2.setText(resultUserName);
            //tv2.setGravity(Gravity.CENTER);
            tr.addView(tv2);
            mainTable.addView(tr);
        }
    }
}
