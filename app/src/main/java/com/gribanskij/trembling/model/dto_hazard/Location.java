package com.gribanskij.trembling.model.dto_hazard;


import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;

@Xml()
public class Location {
    @PropertyElement
    private String place;
    @PropertyElement
    private String lat;
    @PropertyElement
    private String lng;
    @Element
    private Radius radius;


    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public Radius getRadius() {
        return radius;
    }

    public void setRadius(Radius radius) {
        this.radius = radius;
    }

    @Override
    public String toString() {
        return "Place: " + place + "Lat: = " + lat + "Lng: = " + lng + "Radius: =" + radius.toString();
    }
}
