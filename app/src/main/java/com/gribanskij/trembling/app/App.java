package com.gribanskij.trembling.app;


import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.gribanskij.trembling.app.dagger.ComponentsHolder;


public class App extends MultiDexApplication {

    private ComponentsHolder holder;


    public static App getApp(Context context) {
        return (App) context.getApplicationContext();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        holder = new ComponentsHolder(this);
        holder.init();
    }

    public ComponentsHolder getHolder() {
        return holder;
    }

    @Override
    public void onTerminate() {
        holder.clearAppComponent();
        holder = null;
        super.onTerminate();
    }
}
