package com.marsanpat.greta.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.util.Pair;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.marsanpat.greta.Database.Element;
import com.marsanpat.greta.Database.Salt;
import com.marsanpat.greta.R;
import com.marsanpat.greta.Utils.Backups.JSONManager;
import com.marsanpat.greta.Utils.Dialog.DialogManager;
import com.marsanpat.greta.Utils.Notes.NoteManager;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.nononsenseapps.filepicker.Utils;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;


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

        //Request shared storage permission, some settings need it.
        isStoragePermissionGranted();

    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getContext().checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("debug","Permission is granted by the system");
                return true;
            } else {

                Log.v("debug","Permission is revoked");
                Log.v("debug", "Detected activity: "+SettingsActivity.this);
                ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("debug","Permission is granted because of sdk<23");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //resume tasks needing this permission
        }else{
            //We should not let the user continue, some things might not work properly
            finish();
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
                    try{
                        JSONManager manager = new JSONManager();
                        JSONObject backup = manager.createJSONBackup();

                        File dir = new File(MainActivity.backupFolder.getPath());
                        if(!dir.exists()){
                            dir.mkdir();
                        }
                        File exportFile = new File(dir,"gretaExport.json");
                        FileWriter writer = new FileWriter(exportFile);
                        writer.write(backup.toString());
                        writer.flush();
                        writer.close();


                        Toast.makeText(getContext(), "Export finished", Toast.LENGTH_LONG)
                                .show();

                        Log.d("debug", "Exported to: "+exportFile.getAbsolutePath());
                    }catch(JSONException ex){
                        Log.w("debug", "exception creating json export:\n"+ex.toString());
                    }catch (IOException ex){
                        Log.w("debug", "exception writing json file:\n"+ex.toString());
                    }

                    return true;
                }
            });


            Preference importNotesButton = findPreference("import");
            importNotesButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startLibraryFilePicker(getContext(), MainActivity.backupFolder.getPath());
                    return true;

                }
            });
        }

        @Override
        //We'll use these in a future
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            switch(requestCode) {
                case 1: //File picker
                    if(resultCode == Activity.RESULT_OK){
                        List<Uri> files = Utils.getSelectedFilesFromResult(data);
                        for (Uri uri: files) { //just in case we support multiple backup files in the future, but we expect only one
                            File file = Utils.getFileForUri(uri);
                            // Do something with the result...
                            Log.d( "debug", "Detected: "+file.getAbsolutePath());

                            try {
                                importBackup(file);
                            }catch (IOException ex){
                                Log.w("debug", "Something went wrong reading the file to import");
                            }catch (JSONException ex){
                                Log.w("debug", "Something went wrong while working with the JSON: "+ex.toString());
                            }

                        }
                    }
                    break;
                case 2: //Directory tree permission
                    if (resultCode == Activity.RESULT_OK) {
                        Uri uri = data.getData();
                        getContext().grantUriPermission(getContext().getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        getContext().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }
                    break;
            }
        }


        private void showTreeDirectoryPermissionPicker() {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            i.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(i, 2);
        }

        private void startLibraryFilePicker(Context context, String path){
            // FILE PICKER
            Intent i = new Intent(getContext(), FilePickerActivity.class);

            // Set these depending on your use case. These are the defaults.
            i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
            i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
            i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

            // Configure initial directory by specifying a String.
            //i.putExtra(FilePickerActivity.EXTRA_START_PATH, getContext().getExternalFilesDir(null).getAbsolutePath());
            i.putExtra(FilePickerActivity.EXTRA_START_PATH, path);


            startActivityForResult(i, 1);
        }

        private void importBackup(File file) throws IOException, JSONException {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null){
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();// This is a string representing the json file
            String jsonString = stringBuilder.toString();

            JSONManager manager = new JSONManager();
            Pair<List<Element>, List<Salt>> backup = manager.extractJSONBackup(jsonString);

            //Now we import the lists into the database, after cleaning all the previous ones
            NoteManager.deleteAllElements();

            for(Element elem : backup.first){
                Log.d("debug", "IMPORTED ELEMENT:\n"+elem.toString());
                elem.save();
            }

            for(Salt salt : backup.second){
                Log.d("debug", "IMPORTED SALT:\n"+salt.toString());
                salt.save();
            }

            DialogManager.showSimpleDialog(getContext(),"Import successful", "Please restart the app now");







        }


    }
}