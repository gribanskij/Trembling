package com.gribanskij.trembling.utils;

import com.gribanskij.trembling.model.ViewEarthquake;
import com.gribanskij.trembling.model.dto_usgs.DataModel;
import com.gribanskij.trembling.model.dto_usgs.Features;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.functions.Function;

public class Mapper implements Function<DataModel, List<ViewEarthquake>> {

    private static final String LOCATION_SEPARATOR = ", ";


    @Override
    public List<ViewEarthquake> apply(DataModel dataModel) {


        int DEPTH = 2;
        int LATITUDE = 1;
        int LONGITUDE = 0;

        String locationDetail;
        String locationCountry;

        List<Features> listDTO = dataModel.getFeatures();
        List<ViewEarthquake> listVE = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        //String timeZone = timeFormat.getTimeZone().getID();
        String timeZone = "LT";


        for (Features feature : listDTO) {

            ViewEarthquake viewEarthquake = new ViewEarthquake();

            viewEarthquake.setDepth(feature.getGeometry().getCoordinates()[DEPTH]);
            viewEarthquake.setLat(feature.getGeometry().getCoordinates()[LATITUDE]);
            viewEarthquake.setLon(feature.getGeometry().getCoordinates()[LONGITUDE]);

            viewEarthquake.setMag(feature.getProperties().getMag());

            String originalLocation = feature.getProperties().getPlace();
            if (originalLocation.contains(LOCATION_SEPARATOR)) {
                String[] parts = originalLocation.split(LOCATION_SEPARATOR);
                viewEarthquake.setCountry(parts[1]);
                viewEarthquake.setPlace(parts[0]);
            } else {
                viewEarthquake.setCountry("?");
                viewEarthquake.setPlace("?");
            }
            Date date = new Date(Long.parseLong(feature.getProperties().getTime()));
            viewEarthquake.setTime(timeFormat.format(date) + " (" + timeZone + ")");
            viewEarthquake.setDate(dateFormat.format(date));

            listVE.add(viewEarthquake);
        }

        return listVE;
    }
}
