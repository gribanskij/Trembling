package com.gribanskij.trembling.mapView.mvp;


import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import com.gribanskij.trembling.R;
import com.gribanskij.trembling.model.Model;
import com.gribanskij.trembling.model.ViewEarthquake;
import com.gribanskij.trembling.utils.Mapper;
import com.gribanskij.trembling.utils.MapperJson;
import com.gribanskij.trembling.utils.Utils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;


public class MapsPresenter implements MapsContract.Presenter {


    public static final String LOG_TAG = MapsPresenter.class.getSimpleName();


    private MapsContract.View view;
    private Model model;
    private CompositeDisposable disposables;
    private SimpleDateFormat format;
    private Mapper mapper;
    private SharedPreferences sharedPreferences;
    private List<ViewEarthquake> buffer;
    private Resources resources;


    public MapsPresenter(Model model, SharedPreferences preferences
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

    @Override
    public void mapReady() {
        if (buffer.size() != 0) {
            getView().updateMap(buffer);
        } else {
            setQuery(sharedPreferences, resources);
        }
    }

    @Override
    public void refresh() {
        setQuery(sharedPreferences, resources);
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
    public void attachView(MapsContract.View mvpView) {
        view = mvpView;
    }

    @Override
    public void viewIsReady() {

    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void destroy() {
        disposables.dispose();
        disposables = null;
        model = null;
        sharedPreferences = null;
        resources = null;
        mapper = null;
        format = null;
        buffer = null;
    }

    private MapsContract.View getView() {
        return view;
    }

    private void onSpecifyTime(String startTime, String endTime) {


        disposables.add(
                model.getEarthquakes_raw(startTime, endTime, 3f)
                        .map(new MapperJson())
                        .subscribeWith(new DisposableObserver<JSONObject>() {
                            @Override
                            public void onNext(JSONObject jsonObject) {
                                Log.i("ONNEXT:", "");

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
                                Log.i(LOG_TAG, "onComplete: ");
                            }
                        })
        );
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
                                getView().updateMap(buffer);
                                Log.i(LOG_TAG, "onComplete: ");
                            }
                        })
        );
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
                                getView().updateMap(buffer);
                                Log.i(LOG_TAG, "onComplete: ");
                            }
                        })
        );

    }
}
