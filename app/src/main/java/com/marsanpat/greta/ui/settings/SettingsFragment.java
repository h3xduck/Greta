package com.marsanpat.greta.ui.settings;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Pair;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.marsanpat.greta.Activities.MainActivity;
import com.marsanpat.greta.Activities.SettingsActivity;
import com.marsanpat.greta.Database.Element;
import com.marsanpat.greta.Database.Salt;
import com.marsanpat.greta.R;
import com.marsanpat.greta.Utils.Backups.JSONManager;
import com.marsanpat.greta.Utils.Dialog.DialogManager;
import com.marsanpat.greta.Utils.Dialog.ToastManager;
import com.marsanpat.greta.Utils.Files.FileManager;
import com.marsanpat.greta.Utils.Notes.NoteManager;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.nononsenseapps.filepicker.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.ref.WeakReference;
import java.util.List;


public class SettingsFragment extends PreferenceFragmentCompat {

    private ToastManager toastManager = new ToastManager();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        Preference deleteAllButton = findPreference(getString(R.string.resetAllButton_key));
        deleteAllButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.message_title_delete_everything)
                        .setMessage(R.string.message_sure_action)
                        .setNegativeButton(R.string.message_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Nothing
                            }
                        })
                        .setPositiveButton(R.string.message_accept, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NoteManager noteManager = new NoteManager();
                                if(noteManager.deleteEverything()==-1){
                                    Log.w("debug", "something went wrong while deleting everything");
                                    DialogManager.showApplicationError(getContext(), R.integer.Error_Delete_Everything);
                                }else{
                                    //Restart recommended, since notes are cached in the notefragment.
                                    //We could erase them, but for now let's just inform the user.
                                    toastManager.showSimpleToast(getContext(),getString(R.string.message_notes_sucess_deleted), Toast.LENGTH_LONG);
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

                    FileManager fileManager = new FileManager();
                    String returnedPath = fileManager.createAndWriteFile(backup.toString(), MainActivity.backupFolder, "gretaExport.json");
                    toastManager.showSimpleToast(getContext(), getString(R.string.message_export_successful), Toast.LENGTH_LONG);
                    Log.d("debug", "Exported to: "+returnedPath);
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

        Preference reportIssuesButton = findPreference("reportIssues");
        reportIssuesButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String url = "https://github.com/h3xduck/Greta/issues";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
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
            case 2: //Directory tree permission still TODO
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
        NoteManager noteManager = new NoteManager();
        noteManager.deleteAllElements();

        for(Element elem : backup.first){
            Log.d("debug", "IMPORTED ELEMENT:\n"+elem.toString());
            elem.save();
        }

        for(Salt salt : backup.second){
            Log.d("debug", "IMPORTED SALT:\n"+salt.toString());
            salt.save();
        }
        toastManager.showSimpleToast(getContext(),getString(R.string.message_import_successful), Toast.LENGTH_LONG);

    }


}

