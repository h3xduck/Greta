package com.marsanpat.greta.Utils.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.marsanpat.greta.Database.Element;
import com.marsanpat.greta.Database.Element_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Arrays;
import java.util.List;


public class DialogManager {
    public static void showApplicationError(Context context, int errorCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("ERROR")
                .setMessage("Are you sure you want to perform this action?")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO maybe a contactMe
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showSimpleDialog(Context context, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //It just quits
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
