package com.gribanskij.trembling.listView.dagger;

import com.gribanskij.trembling.app.dagger.ActivityScope;
import com.gribanskij.trembling.listView.mvp.MainActivity;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {MainModule.class})
public interface MainComponent {
    void inject(MainActivity activity);
}
