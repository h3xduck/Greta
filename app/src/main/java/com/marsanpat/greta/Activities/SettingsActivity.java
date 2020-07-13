package com.marsanpat.greta.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.marsanpat.greta.Database.Element;
import com.marsanpat.greta.Database.Element_Table;
import com.marsanpat.greta.R;
import com.marsanpat.greta.Utils.Dialog.DialogManager;
import com.marsanpat.greta.Utils.Notes.NoteManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            Preference deleteAllButton = findPreference(getString(R.string.resetAllButton_key));
            deleteAllButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setTitle("Delete Everything")
                        .setMessage("Are you sure you want to perform this action?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Nothing
                            }
                        })
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(NoteManager.deleteEverything()==-1){
                                    Log.w("debug", "something went wrong while deleting everything");
                                    DialogManager.showApplicationError(getContext(), R.integer.Error_Delete_Everything);
                                }else{
                                    //Restart recommended, since notes are cached in the notefragment.
                                    //We could erase them, but for now let's just inform the user.
                                    DialogManager.showSimpleDialog(getContext(), "All notes were deleted", "Please restart the app now");
                                }
                            }
                        });
                    AlertDialog alert = builder.create();
                    alert.show();

                    return true;
                }
            });

            Preference exportNotesButton = findPreference(getString(R.string.export_note_key));
            exportNotesButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    //The full notes backup
                    JSONArray elementsBackup = new JSONArray();
                    List<Element> elementsList = SQLite.select().from(Element.class).queryList();
                    try{
                        for(int ii = 0; ii<elementsList.size(); ii++) {
                            JSONObject object = new JSONObject();
                            Element elem = elementsList.get(ii);
                            object.put("Content", elem.getContent());
                            object.put("ID", elem.getId());
                            object.put("IsEncrypted", elem.isEncrypted());
                            object.put("LastMod", elem.getLastModification());
                            elementsBackup.put(object.toString());
                        }

                    }catch(JSONException ex){
                        //TODO
                        Log.w("debug", "exception creating json export");
                    }

                    try {
                        File dir = new File(getContext().getFilesDir(), "GRETADIR");
                        if(!dir.exists()){
                            dir.mkdir();
                        }
                        File gpxfile = new File(dir,"gretaExport.json");
                        FileWriter writer = new FileWriter(gpxfile);
                        writer.write(elementsBackup.toString());
                        writer.flush();
                        writer.close();
                    } catch (Exception e){
                        //TODO
                        Log.w("debug", "exception writing json file");
                    }
                    Toast.makeText(getContext(), "Export finished", Toast.LENGTH_LONG)
                            .show();


                    return true;
                }
            });
        }
    }
}