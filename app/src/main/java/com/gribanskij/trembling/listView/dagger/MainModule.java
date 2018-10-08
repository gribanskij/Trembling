package com.gribanskij.trembling.listView.dagger;


import android.content.SharedPreferences;
import android.content.res.Resources;

import com.gribanskij.trembling.app.dagger.ActivityScope;
import com.gribanskij.trembling.listView.mvp.MainPresenter;
import com.gribanskij.trembling.model.Model;
import com.gribanskij.trembling.model.ViewEarthquake;
import com.gribanskij.trembling.utils.Mapper;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;


@Module
public class MainModule {

    @ActivityScope
    @Provides
    CompositeDisposable provadeDisposable() {
        return new CompositeDisposable();
    }

    @ActivityScope
    @Provides
    Mapper provideMapper() {
        return new Mapper();
    }


    @ActivityScope
    @Provides
    SimpleDateFormat provideDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
    }

    @ActivityScope
    @Provides
    MainPresenter provideMainPresenter(Model model,
                                       SharedPreferences preferences,
                                       CompositeDisposable disposable,
                                       Mapper mapper,
                                       List<ViewEarthquake> buffer,
                                       Resources resources,
                                       SimpleDateFormat format) {
        return new MainPresenter(model, preferences, disposable, mapper, buffer, resources, format);
    }
}
