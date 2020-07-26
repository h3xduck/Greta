package com.marsanpat.greta.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.marsanpat.greta.R;
import com.marsanpat.greta.ui.settings.SettingsFragment;
import com.marsanpat.greta.ui.welcome.WelcomeFragment;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.welcome, new WelcomeFragment())
                .commit();
    }

}

