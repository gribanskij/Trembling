package com.gribanskij.trembling.services;

import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.gribanskij.trembling.R;
import com.gribanskij.trembling.model.dto_usgs.DataModel;
import com.gribanskij.trembling.network.APIservice;
import com.gribanskij.trembling.utils.Utils;

import java.io.IOException;

import retrofit2.Response;


public class EarthquakeService extends IntentService {


    private static final String ACTION_FETCH_EARTHQUAKES = "com.gribanskij.trembling.services.action.fetch.earthquakes";
    private static final String ACTION_BAZ = "com.gribanskij.trembling.services.action.BAZ";
    private static final String EXTRA_PARAM1 = "com.gribanskij.trembling.services.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.gribanskij.trembling.services.extra.PARAM2";
    public static String TAG = IntentService.class.getSimpleName();

    public EarthquakeService() {
        super("EarthquakeService");
    }


    public static void startActionFetch(Context context, String param1, String param2) {
        Intent intent = new Intent(context, EarthquakeService.class);
        intent.setAction(ACTION_FETCH_EARTHQUAKES);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }

    }

    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, EarthquakeService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FETCH_EARTHQUAKES.equals(action)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Utils.createNotificationChannel(this);
                    Notification notification = Utils.createNotificationForBackground(this, "start fetch...");
                    startForeground(Utils.NOTIFICATION_ID, notification);
                }

                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFetchEartquakes(param1, param2);

            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Fetch in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFetchEartquakes(String param1, String param2) {

        String result = this.getResources().getString(R.string.information_not_available);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Resources resources = this.getResources();
        Response<DataModel> response;

        if (Utils.getTypeQuery(preferences, resources) == 0) {
            response = getWorldEarthquakes(
                    Utils.getStartTimePref(preferences, resources),
                    Utils.getEndTime(),
                    Utils.getMagnitudePref(preferences, resources)
            );
        } else {
            response = getLocalEarthquakes(
                    Utils.getStartTimePref(preferences, resources),
                    Utils.getEndTime(),
                    Utils.getMagnitudePref(preferences, resources),
                    (float) Utils.getLatPref(preferences, resources),
                    (float) Utils.getLonPref(preferences, resources),
                    Utils.getRadiusPref(preferences, resources)
            );
        }

        if (response == null) {
            Utils.sendResult(this, result);
        } else {
            DataModel data = response.body();
            // get number of earthquakes
            if (data.getFeatures() != null) {
                int number = data.getFeatures().size();
                result = resources.getQuantityString(R.plurals.numberOfEarthquakes, number, number);
            }

            Utils.sendResult(this, result);
        }
    }


    private void handleActionBaz(String param1, String param2) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private Response<DataModel> getWorldEarthquakes(String startTime, String endTime, float minMagnitude) {
        APIservice apIservice = new APIservice();
        Response<DataModel> response = null;
        try {
            response = apIservice.getUsgsAPIforBackground().worldEathquakesBackgroun(startTime, endTime, minMagnitude).execute();
        } catch (IOException e) {
            Log.i(TAG, "IOException");
            e.printStackTrace();
        }
        return response;
    }

    private Response<DataModel> getLocalEarthquakes(String startTime, String endTime, float minMagnitude, float lat, float lon, int radius) {
        APIservice apIservice = new APIservice();
        Response<DataModel> response = null;
        try {
            response = apIservice.getUsgsAPIforBackground().locationEathquakesBackground(startTime, endTime, minMagnitude, lat, lon, radius).execute();
        } catch (IOException e) {
            Log.i(TAG, "IOException");
            e.printStackTrace();
        }
        return response;
    }

}
