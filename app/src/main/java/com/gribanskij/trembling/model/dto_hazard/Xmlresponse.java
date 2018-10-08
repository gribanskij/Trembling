package com.gribanskij.trembling.model.dto_hazard;


import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.Xml;

@Xml()
public class Xmlresponse {
    @Element
    private Location location;
    @Element
    private Forecast forecast;
    @Element
    private Error error;

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Forecast getForecast() {
        return forecast;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }


    @Override
    public String toString() {
        return "LOCATION: " + location + "FORECAST: " + forecast + "ERROR: " + error;
    }
}
