package com.marsanpat.greta.Views;

import android.app.AlertDialog;
import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.marsanpat.greta.R;

public class ScrollableDialogView {

    private Context context;
    private String title;
    private CharSequence content;

    public ScrollableDialogView(Context context, String title, CharSequence message){
        this.context=context;
        this.title=title;
        this.content=message;
    }

    public void show(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_scrollable_dialog, null);
        TextView textView = (TextView)view.findViewById(R.id.license_text);
        textView.setText(this.content);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setPositiveButton("OK", null);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}