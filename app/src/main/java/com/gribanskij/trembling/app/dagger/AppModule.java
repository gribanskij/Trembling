package com.gribanskij.trembling.app.dagger;


import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.gribanskij.trembling.model.ViewEarthquake;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private Application application;

    public AppModule(Application aplication) {
        this.application = aplication;
    }

    @Provides
    @Singleton
    Application getApplication() {
        return application;
    }

    @Provides
    @Singleton
    SharedPreferences getSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    Resources getResources(Application application) {
        return application.getResources();
    }

    @Provides
    @Singleton
    List<ViewEarthquake> provideBuffer() {
        return new ArrayList<>();
    }

    @Provides
    @Singleton
    FusedLocationProviderClient providerClient() {
        return LocationServices.getFusedLocationProviderClient(application);
    }

}
