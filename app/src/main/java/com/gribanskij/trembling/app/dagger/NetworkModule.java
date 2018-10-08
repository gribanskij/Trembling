package com.gribanskij.trembling.app.dagger;

import com.gribanskij.trembling.network.APIservice;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class NetworkModule {

    @Singleton
    @Provides
    APIservice provideAPIservice() {
        return new APIservice();
    }
}
