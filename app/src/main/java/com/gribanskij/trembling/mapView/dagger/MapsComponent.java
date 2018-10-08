package com.gribanskij.trembling.mapView.dagger;


import com.gribanskij.trembling.app.dagger.ActivityScope;
import com.gribanskij.trembling.mapView.mvp.MapsActivity;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {MapsModule.class})
public interface MapsComponent {
    void inject(MapsActivity mapsActivity);

}
