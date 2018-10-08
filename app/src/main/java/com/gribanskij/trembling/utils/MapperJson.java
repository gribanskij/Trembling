package com.gribanskij.trembling.utils;

import org.json.JSONObject;

import io.reactivex.functions.Function;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class MapperJson implements Function<Response<ResponseBody>, JSONObject> {

    //Convert а row response from USGS server to Java JSONObject
    @Override
    public JSONObject apply(Response<ResponseBody> response) throws Exception {

        if (response.body() != null) {
            String s = response.body().string();
            return new JSONObject(s);
        }
        return new JSONObject();
    }
}
