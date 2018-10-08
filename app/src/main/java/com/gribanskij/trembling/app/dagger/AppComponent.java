package com.gribanskij.trembling.app.dagger;


import com.gribanskij.trembling.forecast.dagger.ForecastComponent;
import com.gribanskij.trembling.listView.dagger.MainComponent;
import com.gribanskij.trembling.mapView.dagger.MapsComponent;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {NetworkModule.class, ModelModule.class, AppModule.class})
public interface AppComponent {

    MainComponent createMainComponent();

    ForecastComponent createForecastComponent();

    MapsComponent createMapsComponent();

}



