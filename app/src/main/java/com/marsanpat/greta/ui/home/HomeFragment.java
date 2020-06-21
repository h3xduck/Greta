package com.marsanpat.greta.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.marsanpat.greta.Activities.MainActivity;
import com.marsanpat.greta.Database.Element;
import com.marsanpat.greta.Database.Element_Table;
import com.marsanpat.greta.Database.Keys;
import com.marsanpat.greta.Database.User;
import com.marsanpat.greta.Database.User_Table;
import com.marsanpat.greta.R;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        //For SQLite
        final User user = new User();
        user.setId(0);
        user.setName(MainActivity.currentUser);
        user.save();


        Button insertBut = root.findViewById(R.id.insert);
        insertBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Inserting a record", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Element elem = new Element();
                elem.setName("Test Yeah");
                elem.setUser(user);
                elem.setId(0);
                elem.save();

                Element elem2 = new Element();
                elem2.setName("Test2 Yeah");
                elem2.setUser(user);
                elem2.setId(1);
                elem2.save();
            }
        });

        Button selectBut = root.findViewById(R.id.select);
        selectBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Element elem = SQLite.select()
                        .from(Element.class)
                        .querySingle();
                Snackbar.make(view, "Retrieved: "+elem.getName(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        Button inputBut = root.findViewById(R.id.inputBut);
        inputBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText text = (EditText)root.findViewById(R.id.inputText);
                String input = text.getText().toString();
                if(!input.equals("")){
                    Element elem = new Element();
                    elem.setName(input);
                    elem.setUser(user);
                    //Our private key will be an id, corresponding to the time and date of the insertion
                    long time = System.currentTimeMillis();
                    elem.setId(time);
                    elem.save();
                }else{
                    Snackbar.make(view, "Invalid input string", Snackbar.LENGTH_LONG)
                            .show();
                }

            }
        });

        Button deleteBut = root.findViewById(R.id.deleteBut);
        deleteBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText text = (EditText)root.findViewById(R.id.inputText);
                String input = text.getText().toString();
                //First let's calculate how many records we will delete, and show it to the user
                List<Element> elem = SQLite.select()
                        .from(Element.class)
                        .where(Element_Table.name.is(input))
                        .queryList();
                Snackbar.make(view, elem.size()+" records will be deleted", Snackbar.LENGTH_LONG)
                    .show();

                SQLite.delete(Element.class)
                        .where(Element_Table.name.is(input))
                        .execute();
            }
        });

        Button genKeyBut = root.findViewById(R.id.genKeyBut);
        genKeyBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Key generated", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Keys keys = new Keys();
                keys.generateKey();
                keys.setUser("Guest");
                keys.save();
            }
        });

        Button resetBut = root.findViewById(R.id.resetBut);
        resetBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Delete.table(Element.class);
                Delete.table(Keys.class);
                Delete.table(User.class);
                Snackbar.make(view, "The whole DB was reset", Snackbar.LENGTH_LONG).show();
                //Toast.makeText(getContext(), "Test", Toast.LENGTH_LONG).show();
            }
        });


        return root;
    }
}
