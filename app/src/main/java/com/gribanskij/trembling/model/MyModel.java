package com.gribanskij.trembling.model;

import com.gribanskij.trembling.model.dto_hazard.Xmlresponse;
import com.gribanskij.trembling.model.dto_usgs.DataModel;
import com.gribanskij.trembling.network.APIservice;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;


public class MyModel implements Model {


    private APIservice apIservice;


    public MyModel(APIservice apIservice) {
        this.apIservice = apIservice;
    }


    @Override
    public Observable<DataModel> getEarthquakes(String startTime, String endTime, float minMag) {
        return apIservice.getUsgsAPI()
                .worldEathquakes(startTime, endTime, minMag)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DataModel> getLocEarthquakes(String startTime, String endTime, float minMag, float lat, float lon, int r) {
        return apIservice.getUsgsAPI()
                .locationEathquakes(startTime, endTime, minMag, lat, lon, r)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Xmlresponse> getProbability(String location, float mag, int radius, int days) {
        return apIservice.getHazardAPI()
                .getProbability(location, mag, radius, days)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public Observable<Response<ResponseBody>> getEarthquakes_raw(String startTime, String endTime, float minMag) {
        return apIservice.getUsgsAPI()
                .rawEathquakes(startTime, endTime, minMag)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
