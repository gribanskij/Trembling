package com.gribanskij.trembling.network;

import com.gribanskij.trembling.model.dto_hazard.Xmlresponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface HazardAPI {

    @GET("GetEarthquakeProbability?")
    Observable<Xmlresponse> getProbability(
            @Query("q") String place,
            @Query("m") float mag,
            @Query("r") int radius,
            @Query("w") int days
    );
}
