package com.gribanskij.trembling.network;


import com.gribanskij.trembling.model.dto_usgs.DataModel;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface UsgsAPI {
    @GET("fdsnws/event/1/query?format=geojson")
    Observable<DataModel> worldEathquakes(
            @Query("starttime") String start,
            @Query("endtime") String end,
            @Query("minmagnitude") float mag
    );

    @GET("fdsnws/event/1/query?format=geojson")
    Observable<DataModel> locationEathquakes(
            @Query("starttime") String start,
            @Query("endtime") String end,
            @Query("minmagnitude") float mag,
            @Query("latitude") float lat,
            @Query("longitude") float lon,
            @Query("maxradiuskm") int radius
    );

    @GET("fdsnws/event/1/query?format=geojson")
    Call<DataModel> worldEathquakesBackgroun(
            @Query("starttime") String start,
            @Query("endtime") String end,
            @Query("minmagnitude") float mag
    );

    @GET("fdsnws/event/1/query?format=geojson")
    Call<DataModel> locationEathquakesBackground(
            @Query("starttime") String start,
            @Query("endtime") String end,
            @Query("minmagnitude") float mag,
            @Query("latitude") float lat,
            @Query("longitude") float lon,
            @Query("maxradiuskm") int radius
    );


    @GET("fdsnws/event/1/query?format=geojson")
    Observable<Response<ResponseBody>> rawEathquakes(
            @Query("starttime") String start,
            @Query("endtime") String end,
            @Query("minmagnitude") float mag
    );
}
