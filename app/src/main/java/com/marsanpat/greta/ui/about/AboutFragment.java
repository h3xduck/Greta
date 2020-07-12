package com.marsanpat.greta.ui.about;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.marsanpat.greta.R;

public class AboutFragment extends Fragment {

    private View root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_about, container, false);

        TextView githubLink = (TextView) root.findViewById(R.id.tv_telegram);
        githubLink.setText(Html.fromHtml("<a href=https://github.com/marsan27> My github profile "));
        githubLink.setMovementMethod(LinkMovementMethod.getInstance());



        return root;
    }
}
