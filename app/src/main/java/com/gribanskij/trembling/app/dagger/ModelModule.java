package com.gribanskij.trembling.app.dagger;


import com.gribanskij.trembling.model.Model;
import com.gribanskij.trembling.model.MyModel;
import com.gribanskij.trembling.network.APIservice;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ModelModule {

    @Singleton
    @Provides
    Model provideModel(APIservice apIservice) {
        return new MyModel(apIservice);
    }
}


