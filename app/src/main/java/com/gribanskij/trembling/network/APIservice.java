package com.gribanskij.trembling.network;

import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIservice {


    private static final String BASE_USGS_URL = "https://earthquake.usgs.gov/";
    private static final String BASE_HAZARD_URL = "http://api.openhazards.com/";


    private UsgsAPI usgsAPI;
    private HazardAPI hazardAPI;
    private UsgsAPI usgsAPI_raw;

    private Retrofit retrofit_usgs = new Retrofit.Builder()
            .baseUrl(BASE_USGS_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

    private Retrofit retrofit_hazard = new Retrofit.Builder()
            .baseUrl(BASE_HAZARD_URL)
            .addConverterFactory(TikXmlConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

    private Retrofit retrofit_usgs_raw = new Retrofit.Builder()
            .baseUrl(BASE_USGS_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();


    private Retrofit getRetrofit_usgs_background = new Retrofit.Builder()
            .baseUrl(BASE_USGS_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    public UsgsAPI getUsgsAPI() {
        return retrofit_usgs.create(UsgsAPI.class);
    }

    public HazardAPI getHazardAPI() {
        return retrofit_hazard.create(HazardAPI.class);
    }

    public UsgsAPI getUsgsAPI_raw() {
        return usgsAPI_raw;
    }

    public UsgsAPI getUsgsAPIforBackground() {
        return getRetrofit_usgs_background.create(UsgsAPI.class);
    }
}
