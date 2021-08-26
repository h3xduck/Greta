package com.marsanpat.greta.Database;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import com.marsanpat.greta.Utils.Language.LanguageHelper;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.config.FlowConfig;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initLanguage();
        FlowManager.init(new FlowConfig.Builder(this).build());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initLanguage();
    }

    private void initLanguage() {
        String ul = LanguageHelper.getUserLanguage(this);
        // if null the language doesn't need to be changed as the user has not chosen one.
        if (ul != null) {
            LanguageHelper.updateLanguage(this, ul);
        }
        Log.d("d", "initLanguage: "+ul);
    }

}
