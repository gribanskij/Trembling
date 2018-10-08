package com.gribanskij.trembling.model;


import android.os.Parcel;
import android.os.Parcelable;

public class ViewEarthquake implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ViewEarthquake> CREATOR = new Parcelable.Creator<ViewEarthquake>() {
        @Override
        public ViewEarthquake createFromParcel(Parcel in) {
            return new ViewEarthquake(in);
        }

        @Override
        public ViewEarthquake[] newArray(int size) {
            return new ViewEarthquake[size];
        }
    };
    private String time;
    private String date;
    private String mag;
    private String place;
    private String lon;
    private String lat;
    private String depth;
    private String country;


    public ViewEarthquake() {
    }

    public ViewEarthquake(Parcel in) {
        time = in.readString();
        mag = in.readString();
        place = in.readString();
        lon = in.readString();
        lat = in.readString();
        depth = in.readString();
        country = in.readString();
        date = in.readString();
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMag() {
        return mag;
    }

    public void setMag(String mag) {
        this.mag = mag;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(time);
        dest.writeString(mag);
        dest.writeString(place);
        dest.writeString(lon);
        dest.writeString(lat);
        dest.writeString(depth);
        dest.writeString(country);
        dest.writeString(date);
    }
}