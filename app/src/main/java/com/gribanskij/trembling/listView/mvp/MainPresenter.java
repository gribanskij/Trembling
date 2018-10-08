package com.gribanskij.trembling.listView.mvp;


import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import com.gribanskij.trembling.R;
import com.gribanskij.trembling.base.BasePresenter;
import com.gribanskij.trembling.model.Model;
import com.gribanskij.trembling.model.ViewEarthquake;
import com.gribanskij.trembling.utils.Mapper;
import com.gribanskij.trembling.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;


public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter {


    public static final String LOG_TAG = MainPresenter.class.getSimpleName();


    private Model model;
    private SharedPreferences sharedPreferences;
    private CompositeDisposable disposables;
    private Mapper mapper;
    private List<ViewEarthquake> buffer;
    private Resources resources;
    private SimpleDateFormat format;


    public MainPresenter(Model model, SharedPreferences preferences
            , CompositeDisposable disposable
            , Mapper mapper
            , List<ViewEarthquake> buffer
            , Resources resources
            , SimpleDateFormat format) {
        this.model = model;
        this.sharedPreferences = preferences;
        this.disposables = disposable;
        this.mapper = mapper;
        this.buffer = buffer;
        this.resources = resources;
        this.format = format;
    }


    private void onSpecifyTime(String startTime, String endTime, float minMag) {
        getView().showProgress();
        getView().hideEmpty();

        Log.i("START_TIME = ", startTime);
        Log.i("END_TIME = ", endTime);
        Log.i("MIN MAG = ", String.valueOf(minMag));

        disposables.add(
                model.getEarthquakes(startTime, endTime, minMag)
                        .map(mapper)
                        .subscribeWith(new DisposableObserver<List<ViewEarthquake>>() {
                            @Override
                            public void onNext(List<ViewEarthquake> earthquakes) {

                                //Put list of earthquakes to buffer
                                if (buffer.size() != 0) buffer.clear();
                                buffer.addAll(earthquakes);
                                Log.i(LOG_TAG, "OnNext: events = " + Integer.toString(buffer.size()));
                            }

                            @Override
                            public void onError(Throwable e) {
                                getView().hideProgress();
                                getView().showMessage(R.string.error_loading_data);
                                Log.i(LOG_TAG, "onError: " + e.toString());
                            }

                            @Override
                            public void onComplete() {
                                getView().hideProgress();
                                if (buffer.size() == 0) getView().showEmpty();
                                getView().updateEathquakeList(buffer);
                                Log.i(LOG_TAG, "onComplete: ");
                            }
                        })
        );
    }


    @Override
    public void refresh() {
        setQuery(sharedPreferences, resources);
    }

    public void onSpecifyLocation(String startTime, String endTime, float minMag, float latitude, float longitude, int radius) {

        getView().showProgress();
        getView().hideEmpty();

        Log.i("START_TIME = ", startTime);
        Log.i("END_TIME = ", endTime);
        Log.i("MIN MAG = ", String.valueOf(minMag));
        Log.i("LATITUDE =", String.valueOf(latitude));
        Log.i("LONGITUDE = ", String.valueOf(longitude));
        Log.i("RADIUS = ", String.valueOf(radius));

        disposables.add(
                model.getLocEarthquakes(startTime, endTime, minMag, latitude, longitude, radius)
                        .map(mapper)
                        .subscribeWith(new DisposableObserver<List<ViewEarthquake>>() {
                            @Override
                            public void onNext(List<ViewEarthquake> earthquakes) {

                                //Put list of earthquakes to buffer
                                if (buffer.size() != 0) buffer.clear();
                                buffer.addAll(earthquakes);
                                Log.i(LOG_TAG, "OnNext: events = " + Integer.toString(buffer.size()));
                            }

                            @Override
                            public void onError(Throwable e) {
                                getView().hideProgress();
                                getView().showMessage(R.string.error_loading_data);
                                Log.i(LOG_TAG, "onError: " + e.toString());
                            }

                            @Override
                            public void onComplete() {
                                getView().hideProgress();
                                if (buffer.size() == 0) getView().showEmpty();
                                getView().updateEathquakeList(buffer);
                                Log.i(LOG_TAG, "onComplete: ");
                            }
                        })
        );

    }

    @Override
    public void onActivityReady() {

        if (buffer.size() != 0) {
            getView().updateEathquakeList(buffer);
        } else {
            setQuery(sharedPreferences, resources);
        }
    }


    @Override
    public void onChangeMag(int magId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(resources.getString(R.string.magnitude_value), magId);
        editor.apply();
    }

    @Override
    public void onChangeTimeInterval(int intervalId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(resources.getString(R.string.time_interval_value), intervalId);
        editor.apply();
    }

    @Override
    public void onChangePlace(int placeId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(resources.getString(R.string.local_place), placeId);
        editor.apply();
    }

    @Override
    public void onChangeLocation(int radius, double lat, double lon) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(resources.getString(R.string.place_radius_value), radius);
        long temp = Double.doubleToLongBits(lat);
        editor.putLong(resources.getString(R.string.place_lat_value), temp);
        temp = Double.doubleToLongBits(lon);
        editor.putLong(resources.getString(R.string.place_lon_value), temp);
        editor.apply();
    }


    @Override
    public void destroy() {
        super.destroy();

        disposables.dispose();
        disposables = null;
        model = null;
        sharedPreferences = null;
        buffer = null;
        resources = null;
    }


    private void setQuery(SharedPreferences preferences, Resources resources) {

        if (!getView().getNetworkAvailableAndConnected()) {
            getView().hideProgress();
            getView().showMessage(R.string.no_internet_connection);
            return;
        }

        if (Utils.getTypeQuery(preferences, resources) == 0) {
            onSpecifyTime(
                    Utils.getStartTimePref(preferences, resources),
                    Utils.getEndTime(),
                    Utils.getMagnitudePref(preferences, resources)
            );
        } else {
            onSpecifyLocation(
                    Utils.getStartTimePref(preferences, resources),
                    Utils.getEndTime(),
                    Utils.getMagnitudePref(preferences, resources),
                    (float) Utils.getLatPref(preferences, resources),
                    (float) Utils.getLonPref(preferences, resources),
                    Utils.getRadiusPref(preferences, resources)
            );
        }
    }
}
