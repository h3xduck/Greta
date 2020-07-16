package com.marsanpat.greta.Utils.Dialog;

import android.content.Context;
import android.widget.Toast;

public class ToastManager {
    public void showSimpleToast(Context context, String message, int length){
        Toast.makeText(context, message, length)
                .show();
    }
}
