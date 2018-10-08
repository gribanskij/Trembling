package com.gribanskij.trembling.forecast.dagger;


import com.gribanskij.trembling.app.dagger.ActivityScope;
import com.gribanskij.trembling.forecast.mvp.ForecastActivity;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {ForecastModule.class})
public interface ForecastComponent {
    void inject(ForecastActivity activity);
}
