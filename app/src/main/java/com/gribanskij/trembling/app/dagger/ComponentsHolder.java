package com.gribanskij.trembling.app.dagger;

import android.app.Application;

import com.gribanskij.trembling.forecast.dagger.ForecastComponent;
import com.gribanskij.trembling.listView.dagger.MainComponent;
import com.gribanskij.trembling.mapView.dagger.MapsComponent;

public class ComponentsHolder {


    private AppComponent appComponent;
    private MainComponent mainComponent;
    private MapsComponent mapsComponent;
    private ForecastComponent forecastComponent;
    private Application application;

    public ComponentsHolder(Application application) {

        this.application = application;

    }

    public void init() {
        appComponent = DaggerAppComponent.builder().appModule(new AppModule(application)).build();
    }

    // AppComponent

    public AppComponent getAppComponent() {
        return appComponent;
    }

    // MainComponent
    public MainComponent getMainComponent() {
        if (mainComponent == null) {
            mainComponent = getAppComponent().createMainComponent();
        }
        return mainComponent;
    }

    //MapsComponent
    public MapsComponent getMapsComponent() {
        if (mapsComponent == null) {
            mapsComponent = getAppComponent().createMapsComponent();
        }
        return mapsComponent;
    }

    public ForecastComponent getForecastComponent() {
        if (forecastComponent == null) {
            forecastComponent = getAppComponent().createForecastComponent();
        }
        return forecastComponent;
    }

    public void clearAppComponent() {
        application = null;
        appComponent = null;
    }

    public void clearMapsComponent() {
        mapsComponent = null;
    }

    public void clearMainComponent() {
        mainComponent = null;
    }

    public void clearForecastComponent() {
        forecastComponent = null;
    }


}

