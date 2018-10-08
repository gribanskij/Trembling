package com.gribanskij.trembling.base;

import com.gribanskij.trembling.model.dto_hazard.Xmlresponse;
import com.gribanskij.trembling.model.dto_usgs.DataModel;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;


public interface MvpModel {

    Observable<DataModel> getEarthquakes(String startTime, String endTime, float minMag);

    Observable<DataModel> getLocEarthquakes(String startTime, String endTime, float minMag, float lat, float lon, int r);

    Observable<Xmlresponse> getProbability(String location, float mag, int radius, int days);

    Observable<Response<ResponseBody>> getEarthquakes_raw(String startTime, String endTime, float minMag);


}

