package com.marsanpat.greta;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private static final String CHANNEL_ID = "GRETACHANNEL";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Debug","Hello");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        final NotificationUtils notificationsUtils = new NotificationUtils(this);
        notificationsUtils.createChannel(CHANNEL_ID,"GRETACHANNEL");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                notificationsUtils.sendNotificationInDefaultChannel("Title of not","Hey Its working",101);


            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //For SQLite
        final Organization organization = new Organization();
        organization.setId(1);
        organization.setName("StaticOrganization");
        organization.save();

        final TextView resultstv = findViewById(R.id.results);
        resultstv.setText("Nothing");

        Button insertBut = findViewById(R.id.insert);
        insertBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Inserting a record", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Element elem = new Element();
                elem.setName("Test Yeah");
                elem.setOrganization(organization);
                elem.setId(0);
                elem.save();

                Element elem2 = new Element();
                elem2.setName("Test2 Yeah");
                elem2.setOrganization(organization);
                elem2.setId(1);
                elem2.save();
            }
        });

        Button selectBut = findViewById(R.id.select);
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

        Button selectAllBut = findViewById(R.id.selectAll);
        selectAllBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Element> elem = SQLite.select()
                        .from(Element.class)
                        //.where(Organization_Table.id.is(1))
                        .queryList();
                String result ="";
                for(int ii=0; ii<elem.size(); ii++){
                    result = result+"\n"+elem.get(ii).getName();
                    Log.d("Result", result+ii);
                }
                TextView resultstv = findViewById(R.id.results);
                resultstv.setText(result);
            }
        });


        Organization newOrg = new Select()
                .from(Organization.class)
                //.where(Organization_table.id.eq(1))
                .querySingle();
        Log.d("Debug",newOrg.getName());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
