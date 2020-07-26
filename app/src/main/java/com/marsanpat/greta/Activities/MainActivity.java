package com.marsanpat.greta.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.marsanpat.greta.R;
import com.marsanpat.greta.Utils.Notifications.NotificationUtils;
import com.marsanpat.greta.ui.notes.NotesFragment;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private AppBarConfiguration mAppBarConfiguration;

    private static final String CHANNEL_ID = "GRETACHANNEL";
    public static Uri backupFolder; //Default backup folder
    public static String ENCRYPTED_NOTE_PREVIEW;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final NotificationUtils notificationsUtils = new NotificationUtils(this);
        notificationsUtils.createChannel(CHANNEL_ID,"GRETACHANNEL");
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_gallery, R.id.nav_about)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        setupSharedPreferences();
        //startWelcomeActivity();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //For now, we only have the settings option but let's build a switch for future options
        switch (item.getItemId()){
            case R.id.action_settings:
                startSettingsActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startSettingsActivity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        //Since the user may perform some operations which directly affect the fragment, we will refresh it.
        NotesFragment.mustIgnoreCache = true;
        this.startActivity(intent);
    }

    private void startWelcomeActivity(){
        Intent intent = new Intent(this, WelcomeActivity.class);
        this.startActivity(intent);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        ENCRYPTED_NOTE_PREVIEW = sharedPreferences.getString("encryption_preview", "************");

        if(ENCRYPTED_NOTE_PREVIEW.equals("")){
            ENCRYPTED_NOTE_PREVIEW="************";
        }
        //TODO CHANGE THIS ONCE THE SETTING IS CREATED
        backupFolder =  Uri.fromFile(getExternalFilesDir(null)); //Default backup folder
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("encryption_preview")) {
            Log.d("debug", "encrypt_preview preference changed");
            ENCRYPTED_NOTE_PREVIEW = sharedPreferences.getString("encryption_preview", "*********");
            if(ENCRYPTED_NOTE_PREVIEW.equals("")){
                ENCRYPTED_NOTE_PREVIEW="************";
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

   /* @Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = super.getTheme();
        //Switch to light mode
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("colorMode", false)){
            theme.applyStyle(R.style.Theme_AppCompat_Light, true);
        }
        // you could also use a switch if you have many themes that could apply
        return theme;
    }*/
}
