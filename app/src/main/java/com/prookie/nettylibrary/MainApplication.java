package com.prookie.nettylibrary;

import android.content.Context;

import org.litepal.LitePalApplication;

/**
 * MainApplication
 * Created by brin on 2018/7/10.
 */
public class MainApplication extends LitePalApplication {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext() {
        return context;
    }
}
