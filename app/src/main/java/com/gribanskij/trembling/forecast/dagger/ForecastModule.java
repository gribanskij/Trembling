package com.gribanskij.trembling.forecast.dagger;

import com.gribanskij.trembling.app.dagger.ActivityScope;
import com.gribanskij.trembling.forecast.mvp.ForecastPresenter;
import com.gribanskij.trembling.model.Model;

import dagger.Module;
import dagger.Provides;

@Module
public class ForecastModule {

    @ActivityScope
    @Provides
    ForecastPresenter provideForecastPresenter(Model model) {
        return new ForecastPresenter(model);
    }
}
