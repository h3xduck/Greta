package com.marsanpat.greta.ui.about;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.marsanpat.greta.R;
import com.marsanpat.greta.Utils.Dialog.DialogManager;

public class AboutFragment extends Fragment {

    private View root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_about, container, false);

        TextView githubLink = (TextView) root.findViewById(R.id.tv_telegram);
        githubLink.setText(Html.fromHtml(getString(R.string.message_contact_github)));
        githubLink.setHighlightColor(getResources().getColor(R.color.colorPrimary));
        githubLink.setMovementMethod(LinkMovementMethod.getInstance());

        Button dbflowLicenseButton = root.findViewById(R.id.dbflow_licenseButton);
        dbflowLicenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogManager dialogManager = new DialogManager();
                dialogManager.showScrollableDialog(getContext(), getString(R.string.message_mit_license), getResources().getString(R.string.dbflow_license));
            }
        });

        Button javaaescryptoLicenseButton = root.findViewById(R.id.javaaescrypto_licenseButton);
        javaaescryptoLicenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogManager dialogManager = new DialogManager();
                dialogManager.showScrollableDialog(getContext(), getString(R.string.message_mit_license), getResources().getString(R.string.java_aes_crypto_license));
            }
        });

        Button nononsenseFilePickerLicenseButton = root.findViewById(R.id.nononsensefilepicker_licenseButton);
        nononsenseFilePickerLicenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogManager dialogManager = new DialogManager();
                dialogManager.showScrollableDialog(getContext(), getString(R.string.message_mozilla_license), getResources().getString(R.string.nononsensefilepicker_license));
            }
        });

        Button explanationButton = root.findViewById(R.id.explanation_button);
        explanationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogManager dialogManager = new DialogManager();
                dialogManager.showScrollableDialog(getContext(), getResources().getString(R.string.explanation_button_text),  Html.fromHtml(getResources().getString(R.string.explanation_text)));
            }
        });


        return root;
    }
}
